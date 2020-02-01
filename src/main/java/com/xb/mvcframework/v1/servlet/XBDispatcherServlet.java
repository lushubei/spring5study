package com.xb.mvcframework.v1.servlet;

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
import com.xb.demo.common.LogFormatter;

public class XBDispatcherServlet extends HttpServlet {
    /**
     * 《Spring 5 核心原理与30类手写实践》 第七章 1.0版本
     */
    static final Logger logger = LogFormatter.getLog(XBDispatcherServlet.class);

    private Map<String, Object> mapping = new HashMap<String, Object>();

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
        if (!mapping.containsKey(url)) {
            resp.getWriter().write("404 not found!!");
            return;
        }

        Method method = (Method) mapping.get(url);

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

        Object o = mapping.get(method.getDeclaringClass().getName());

        method.invoke(o, paramValues);

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        InputStream is = null;

        try {
            Properties configContext = new Properties();

            is = this.getClass().getClassLoader().getResourceAsStream(config.getInitParameter("contextConfigLocation"));
            configContext.load(is);

            String scanPackage = configContext.getProperty("scanPackage");

            doScanner(scanPackage);
            Set<String> classSet = new HashSet<String>(mapping.keySet());

            for (String className : classSet) {
                if (!mapping.containsKey(className)) {
                    continue;
                }
                Class<?> clazz = Class.forName(className);
                if (clazz.isAnnotationPresent(XBController.class)) {
                    String baseUrl = "";
                    if (clazz.isAnnotationPresent(XBRequestMapping.class)) {
                        XBRequestMapping requestMapping = clazz.getAnnotation(XBRequestMapping.class);
                        baseUrl = requestMapping.value();
                    }

                    Method[] methods = clazz.getMethods();

                    for (Method method : methods) {
                        if (!method.isAnnotationPresent(XBRequestMapping.class)) {
                            continue;
                        }

                        XBRequestMapping requestMapping = method.getAnnotation(XBRequestMapping.class);

                        String url = (baseUrl + "/" + requestMapping.value()).replaceAll("/+", "/");
                        mapping.put(url, method);

                    }

                    mapping.put(className,clazz.newInstance());

                } else if (clazz.isAnnotationPresent(XBService.class)) {
                    XBService service = clazz.getAnnotation(XBService.class);
                    String beanName = service.value();
                    if ("".equals(beanName)) {
                        beanName = clazz.getName();
                    }
                    Object instance = clazz.newInstance();
                    mapping.put(beanName, instance);

                    for (Class<?> i : clazz.getInterfaces()) {
                        mapping.put(i.getName(), instance);
                    }

                } else {
                    continue;
                }
            }

            for (Object object : mapping.values()) {
                if (object == null) {
                    continue;
                }

                Class clazz = object.getClass();
                if (clazz.isAnnotationPresent(XBController.class)) {
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        if (!field.isAnnotationPresent(XBAutowired.class)) {
                            continue;
                        }
                        XBAutowired autowired = field.getAnnotation(XBAutowired.class);
                        String beanName = autowired.value();
                        if ("".equals(beanName)) {
                            beanName = field.getType().getName();
                        }
                        field.setAccessible(true);

                        field.set(mapping.get(clazz.getName()), mapping.get(beanName));

                    }
                }

            }
            System.out.println("XB MVC Framework 1.0 is init！");
            System.out.println(mapping.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void doScanner(String scanPackage) {

        URL url = this.getClass().getClassLoader().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());

        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {//非目录
                if (!file.getName().endsWith(".class")) {continue;}
                String clazzName = (scanPackage + "." + file.getName().replace(".class", ""));
                mapping.put(clazzName, null);
            }
        }

    }
}
