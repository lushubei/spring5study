package com.xb.mvcframework.v3.servlet;

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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XBDispatcherServlet extends HttpServlet {
    /**
     * 《Spring 5 核心原理与30类手写实践》 第七章 3.0版本
     */

    static final Logger logger = LogFormatter.getLog(XBDispatcherServlet.class);

    //声明全局变量
    private Properties configContext = new Properties();

    //保存扫描的所有的类名
    private  List<String> classNames = new ArrayList<>();


    //IOC容器
    private Map<String, Object> ioc = new HashMap<>();

    //URL和method的对应关系
    private List<Handler> handlerMapping = new ArrayList<>();

    private class Handler {
        protected Object controller; //保存URL
        protected Method method;//保存方法
        protected Pattern pattern; //保存正则
        protected Map<String, Integer> paramIndexMapping; //参数顺序

        protected Handler(Pattern pattern, Object controller, Method method) {
            this.controller = controller;
            this.method = method;
            this.pattern = pattern;
            paramIndexMapping = new HashMap<>();
            putParamIndexMapping(method);
        }


        private void putParamIndexMapping(Method method) {

            //提取方法中加了注解的参数
            //第一维是参数位置，第二维是注解，因一个参数可以有多个注解
            Annotation[][] pa = method.getParameterAnnotations();
            for (int i = 0; i < pa.length; i++) {
                for (Annotation a : pa[i]) {
                    if (a instanceof XBRequestParam) {
                        String paraName = ((XBRequestParam) a).value();
                        if (!"".equals(paraName.trim())) {
                            paramIndexMapping.put(paraName, i);
                        }
                    }
                }
            }

            //提取方法中的request和response参数
            Class<?>[] paramsTypes = method.getParameterTypes();
            for (int i = 0; i < paramsTypes.length; i++) {
                Class type = paramsTypes[i];

                if (type == HttpServletRequest.class || type == HttpServletResponse.class) {
                    paramIndexMapping.put(type.getName(), i);
                }
            }

        }
    }


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


        Handler handler = getHandler(req);


        if(handler==null){
            resp.getWriter().write("404 not found!!");
            return;
        }


        //获取方法的形参列表
        Class<?> [] parameterTypes = handler.method.getParameterTypes();

        //保存赋值参数的位置
        Object[] paramValues = new Object[parameterTypes.length];

        //保存请求的参数列表
        Map<String,String[]> params = req.getParameterMap();


        for(Map.Entry<String,String[]> parm: params.entrySet()){
            String value = Arrays.toString(parm.getValue())
                    .replaceAll("\\[|\\]","")
                    .replaceAll("\\s","");

            if(!handler.paramIndexMapping.containsKey(parm.getKey())){continue;}

            int index = handler.paramIndexMapping.get(parm.getKey());
            paramValues[index]= convert(parameterTypes[index],value);
        }


        if(handler.paramIndexMapping.containsKey(HttpServletRequest.class.getName())){
            int reqIndex=handler.paramIndexMapping.get(HttpServletRequest.class.getName());
            paramValues[reqIndex] = req;
        }

        if(handler.paramIndexMapping.containsKey(HttpServletResponse.class.getName())){
            int reqIndex=handler.paramIndexMapping.get(HttpServletResponse.class.getName());
            paramValues[reqIndex] = resp;
        }



        Object returnValue = handler.method.invoke(handler.controller, paramValues);
        if(returnValue==null ||returnValue instanceof Void){ return;}
        resp.getWriter().write(returnValue.toString());

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

        System.out.println("XB Spring framework 3.0 is init.");


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

//                String regex = (baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                String regex = ( "/" + baseUrl+ requestMapping.value()).replaceAll("/+", "/");
                Pattern pattern = Pattern.compile(regex);

                handlerMapping.add(new Handler(pattern,entry.getValue(),method));
                System.out.println("mapping" + regex + "," + method);
            }
        }
    }

    private String toLowerFirstCase(String simpleName){
        char [] chars = simpleName.toCharArray();

        chars[0] += 32;
        return String.valueOf(chars);
    }

    private Handler getHandler(HttpServletRequest req){
        if(handlerMapping.isEmpty()){
            return  null;
         }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");

        for (Handler handler:handlerMapping){
            try {
                Matcher matcher = handler.pattern.matcher(url);
                if(!matcher.matches()){continue;}
                return handler;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private Object convert(Class<?> type,String value){
        if(Integer.class==type){
            return Integer.valueOf(value);
        }
        return value;
    }

}
