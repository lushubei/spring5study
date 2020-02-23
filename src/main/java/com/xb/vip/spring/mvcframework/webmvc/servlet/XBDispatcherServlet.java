package com.xb.vip.spring.mvcframework.webmvc.servlet;

import com.xb.vip.spring.mvcframework.annotation.XBController;
import com.xb.vip.spring.mvcframework.annotation.XBRequestMapping;
import com.xb.vip.spring.mvcframework.beans.XBBeanWrapper;
import com.xb.vip.spring.mvcframework.context.XBApplicationContext;
import com.xb.vip.spring.mvcframework.webmvc.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
public class XBDispatcherServlet extends HttpServlet{

    private final String LOCATION = "contextConfigLocation";

    //XBHandlerMapping最核心的设计，也是最经典的
    //它直接干掉了Structs、Webwork等MVC框架
    private List<XBHandlerMapping> handlerMappings = new ArrayList<XBHandlerMapping>();

    private Map<XBHandlerMapping,XBHandlerAdapter> handlerAdapters = new HashMap<XBHandlerMapping, XBHandlerAdapter>();

    private List<XBViewResolver> viewResolvers = new ArrayList<XBViewResolver>();

    private XBApplicationContext context;


    @Override
    public void init(ServletConfig config) throws ServletException{
        log.debug("现在开始初始化");
        //相当于把IOC容器初始化
        context = new XBApplicationContext(config.getInitParameter(LOCATION));
        initStrategies(context); //mvc九大组件初始化
        log.debug("init初始化结束喽");
    }

    private void initStrategies(XBApplicationContext context) {
        //有九种策略
        //针对每个用户请求，都会经过一些处理策略处理，最终才能有结果输出
        //每种策略可以自定义干扰，但是最终的结果都一致
        //========这就是传说中的九大组件========
        initMultipartResolve(context);//文件上传解析，如果请求类型是multipart,将通过MultipartResolver进行文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析

        /** 我们自己会实现 */
        //XBHandlerMappring用来保存Controller中配置的RequestMapping和Method的对应关系
        initHandlerMappings(context);//通过HandlerMapping将请求映射到处理器
        /** 我们自己会实现 */
        //XBHandlerAdapters 用例动态匹配Method参数，包括类型转换、动态赋值
        initHandlerAdapters(context);//通过HandlerAdapter进行多类型的参数动态匹配

        initHandlerExceptionResolvers(context);//如果执行过程中遇到异常，将交给它来解析

        /** 我们自己会实现 */
        //通过ViewResolvers实现动态模板的解析
        //自己解析一套模板语言
        initViewResolvers(context);//通过viewResolver将逻辑视图解析到具体视图实现


        initFlashMapManager(context);//Flash映射管理器


    }

    //将Controller中配置的RequestMappping和Method进行一一对应
    private void initHandlerMappings(XBApplicationContext context) {
        //通过HandlerMapping将请求映射到处理器

        //首先从容器中获取所有的实例
        String[] beanNames = context.getBeanDefinitionNames();

        try {
            for (String beanName: beanNames) {
                //到了MVC层，对外提供的方法只有一个getBean()方法
                //返回的对象不是BeanWrapper，怎么办
                Object controller = context.getBean(beanName);

                Class<?> clazz = controller.getClass();
                if(!clazz.isAnnotationPresent(XBController.class)){
                    continue;
                }

                String baseUrl = "";

                if(clazz.isAnnotationPresent(XBRequestMapping.class)){
                    XBRequestMapping requestMapping = clazz.getAnnotation(XBRequestMapping.class);
                    baseUrl = requestMapping.value();
                }

                //扫描所有的public类型的方法
                Method[] methods = clazz.getMethods();
                for (Method method: methods) {
                    if(!method.isAnnotationPresent(XBRequestMapping.class)){
                        continue;
                    }

                    XBRequestMapping requestMapping = method.getAnnotation(XBRequestMapping.class);
                    String reqex = ("/" + baseUrl + requestMapping.value().replaceAll("\\*",".*"))
                            .replaceAll("/+","/");
                    Pattern pattern = Pattern.compile(reqex);
                    this.handlerMappings.add(new XBHandlerMapping(pattern,controller,method));
                    log.info("Mapping: " + reqex + "," + method);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void initHandlerAdapters(XBApplicationContext context) {
        //通过HandlerAdapter进行多类型的参数动态匹配
        //在初始阶段，我们能做的就是，将这些参数的名字或者类型按一定的顺序保存下来
        //因为后面用反射调用的时候，传的形参是一个数组
        //可以通过记录这些参数的位置index，逐个从数组中取值，这样就和参数的顺序无关了

        for (XBHandlerMapping handlerMapping: this.handlerMappings) {
            //每个方法有一个参数列表，这里保存的是形参列表
            this.handlerAdapters.put(handlerMapping, new XBHandlerAdapter());
        }

        //todo 此处contex 没用排上用处哦

    }

    private void initLocaleResolver(XBApplicationContext context) {
        // 本地化解析
        //在页面中输入http://localhost/first.html
        //解决页面名字和模板文件的关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");

        this.viewResolvers.add(new XBViewResolver(templateRoot));

        //下方代码没有意义
//        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();
//        File templateRootDir = new File(templateRootPath);
//        for (File template : templateRootDir.listFiles()) {
//            this.viewResolvers.add(new XBViewResolver(template.getPath()));
//        }
    }



    private void initFlashMapManager(XBApplicationContext context) {
        //TODO Flash映射管理器
    }

    private void initViewResolvers(XBApplicationContext context) {
        //todo 将逻辑视图解析到具体视图实现
    }

    private void initHandlerExceptionResolvers(XBApplicationContext context) {
        //todo 如果执行过程中遇到异常，将交给它来解析
    }


    private void initThemeResolver(XBApplicationContext context) {
        //todo 主题解析
    }


    private void initMultipartResolve(XBApplicationContext context) {
        //todo 文件上传解析

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        log.debug("get 入口");
        this.doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        log.debug("post 函数的处理部分在这里");
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            resp.getWriter().write("<font size='25' color='blue'>500 Exception</font><br/>Details:<br/>"
            + Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","")
                            .replaceAll("\\s","\r\n") + "\n<front collor='green'><i>Copyright@XiaoBei</i></front>"
            );

            e.printStackTrace();

        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{

        //根据用户请求的URL来获得一个Hadler
        XBHandlerMapping handler = getHandler(req);
        if(handler == null){
            processDispatchResult(req, resp, new XBModeAndView("404"));
            return;
        }

        XBHandlerAdapter ha = getHanderAdaper(handler);

        //这一步只是调用方法，得到返回值
        XBModeAndView mv = ha.handle(req,resp,handler);

        //这一步才是真的输出
        processDispatchResult(req, resp, mv);

    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, XBModeAndView mv)
            throws Exception{

        //调用viewResolver的resolveViewName()方法
        if(null == mv){return;}

        if(this.viewResolvers.isEmpty()){return;}

        if(this.viewResolvers != null){
            for (XBViewResolver viewResolver: this.viewResolvers) {
                XBView view = viewResolver.resolveViewName(mv.getViewName(), null);
                if(view != null){
                    view.render(mv.getModel(),req,resp);
                    return;
                }
            }
        }
    }

    private XBHandlerAdapter getHanderAdaper(XBHandlerMapping handler) {
        if(this.handlerAdapters.isEmpty()){return  null;}

        XBHandlerAdapter ha = this.handlerAdapters.get(handler);
        if(ha.supports(handler)){
            return ha;
        }
        return null;
    }


    private XBHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){return null;}

        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");

        for (XBHandlerMapping handler: this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if(!matcher.matches()){continue;}
            return handler;
        }
        return null;
    }
}

