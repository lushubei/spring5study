package com.xb.mvcframework.v2.servlet;

import com.xb.demo.common.LogFormatter;
import com.xb.mvcframework.annotation.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class XBDispatcherServlet extends HttpServlet {
    /**
     * 《Spring 5 核心原理与30类手写实践》 第七章 2.0版本
     */
    static final Logger logger = LogFormatter.getLog(XBDispatcherServlet.class);

    //声明全局变量
    private Properties configContext = new Properties();

    //保存扫描的所有的类名
    private  List<String> classNames = new ArrayList<>();


    //IOC容器
    private Map<String, Object> ioc = new HashMap<>();

    //URL和method的对应关系
    private Map<String, Method> handlerMapping = new HashMap<>();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.info("welcome to xiaobei's servlet! begin get!");
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            doDispatch(req, resp);
        } catch (Exception e) {
            resp.getWriter().write("500 Excetpion " + Arrays.toString(e.getStackTrace()));
        }
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception {

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        if (!handlerMapping.containsKey(url)) {
            resp.getWriter().write("404 not found!!");
            return;
        }

        Method method = (Method) handlerMapping.get(url);

        //第一个参数：方法所在的实例
        //第二个参数：调用时所需要的实参
        Map<String, String[]> params = req.getParameterMap();

        //获取方法的形参列表
        Class<?> [] parameterTypes = method.getParameterTypes();

        //保存请求的参数列表
        Map<String,String[]> parameterMap = req.getParameterMap();

        //保存赋值参数的位置
        Object[] paramValues = new Object[parameterTypes.length];

        for(int i=0; i < parameterTypes.length; i++){
            Class parameterType = parameterTypes[i];

            if(parameterType == HttpServletRequest.class){
                paramValues[i] = req;
                continue;
            }else if(parameterType == HttpServletResponse.class){
                paramValues[i] = resp;
                continue;
            }
            else{
                //第一维是参数位置，第二维是注解，因一个参数可以有多个注解
                Annotation[][] pa = method.getParameterAnnotations();

                for(int j=0;j < pa.length;j++){
                    for(Annotation a:pa[j]){
                        if(a instanceof XBRequestParam){
                            String paraName = ((XBRequestParam)a).value();
                            if(!"".equals(paraName.trim())){
                                String value = Arrays.toString(parameterMap.get(paraName))
                                        .replaceAll("\\[|\\]","")
                                        .replaceAll("\\s","");
                                paramValues[i]=value;
                            }
                        }
                    }
                }
            }

        }


        Object o = ioc.get(method.getDeclaringClass().getName());

        method.invoke(o, paramValues);

    }

    @Override
    public void init(ServletConfig config) throws ServletException {


        //1.加载配置文件
        doLoadConfig(config.getInitParameter("contexConfigLocation"));

        //2. 扫描相关的类
        doScanner(configContext.getProperty("scanPackage"));

        //3. 初始化扫描到的类
        doInstance();

        //4. 完成依赖注入
        doAutowired();

        //5. 初始化HandlerMapping
        initHandlerMapping();

        System.out.println("XB Spring framework 2.0 is init.");


    }

    //1.加载配置文件
    public void doLoadConfig(String contexConfigLocation){

        InputStream fis = this.getClass().getClassLoader().getResourceAsStream(contexConfigLocation);

        try {
            configContext.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(null!=fis){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



    //2. 扫描相关的类
    public void doScanner(String scanPackage) {

        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {continue;}
                String clazzName = (scanPackage + "." + file.getName().replace(".class", ""));
                classNames.add(clazzName);
            }
        }

    }


    //3. 初始化扫描到的类
    public void doInstance(){

        if (classNames.isEmpty()){return;}
        try {
            for (String className : classNames) {

                Class<?> clazz = Class.forName(className);

                if (clazz.isAnnotationPresent(XBController.class)) {
                    Object instance =clazz.newInstance();
//                    String beanName = toLowerFirstCase(clazz.getSimpleName());
                    String beanName = clazz.getName();

                    ioc.put(beanName,instance);

                } else if (clazz.isAnnotationPresent(XBService.class)) {
                    XBService service = clazz.getAnnotation(XBService.class);
                    String beanName = service.value();
                    if ("".equals(beanName.trim())) {
//                        beanName = toLowerFirstCase(clazz.getSimpleName());
                        beanName = clazz.getName();
                    }
                    Object instance = clazz.newInstance();
                    ioc.put(beanName, instance);

                    for (Class<?> i : clazz.getInterfaces()) {
                        if(ioc.containsKey(i.getName()))
                        {
                            throw new Exception("The\"" + i.getName() + "\"is exists");
                        }
                        ioc.put(i.getName(), instance);
                    }

                } else {
                    continue;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doAutowired(){
        if (ioc.isEmpty()){return;}

        for (Map.Entry<String,Object> entry : ioc.entrySet()) {

            Field[] fields = entry.getValue().getClass().getDeclaredFields();

            for (Field field : fields) {
                if (!field.isAnnotationPresent(XBAutowired.class)) {
                    continue;
                }
                XBAutowired autowired = field.getAnnotation(XBAutowired.class);
                String beanName = autowired.value().trim();
                if ("".equals(beanName)) {
                    beanName = field.getType().getName();
                }
                field.setAccessible(true);//暴力访问

                try {
                    //反射机制，动态字段赋值
                    field.set(entry.getValue(), ioc.get(beanName));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    //4. 初始化HandlerMapping
    public void initHandlerMapping() {

        if(ioc.isEmpty()){return;}
        for (Map.Entry<String,Object> entry : ioc.entrySet()) {


            Class<?> clazz = entry.getValue().getClass();

            if (!clazz.isAnnotationPresent(XBController.class)) {continue;}

            String baseUrl = "";
            if (clazz.isAnnotationPresent(XBRequestMapping.class)) {
                XBRequestMapping requestMapping = clazz.getAnnotation(XBRequestMapping.class);
                baseUrl = requestMapping.value();
            }

            Method[] methods = clazz.getMethods();

            for (Method method : methods) {
                if (!method.isAnnotationPresent(XBRequestMapping.class)) {continue;}
                XBRequestMapping requestMapping = method.getAnnotation(XBRequestMapping.class);
                String url = (baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                handlerMapping.put(url, method);
            }
        }
    }

    private String toLowerFirstCase(String simpleName){
        char [] chars = simpleName.toCharArray();

        chars[0] += 32;
        return String.valueOf(chars);
    }

}
