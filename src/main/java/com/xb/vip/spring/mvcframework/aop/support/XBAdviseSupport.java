package com.xb.vip.spring.mvcframework.aop.support;

import com.xb.vip.spring.mvcframework.aop.XBAopConfig;
import com.xb.vip.spring.mvcframework.aop.aspect.XBAfterReturningAdvice;
import com.xb.vip.spring.mvcframework.aop.aspect.XBAfterThrowingAdvice;
import com.xb.vip.spring.mvcframework.aop.aspect.XBMethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主要用来解析和封装AOP配置
 */
public class XBAdviseSupport {
    private Class targetClass;
    private Object target;
    private Pattern pointCutCClassPattern;


    private  transient Map<Method,List<Object>> methodCache;

    private XBAopConfig config;

    public XBAdviseSupport(Object target, Class targetClass, XBAopConfig config) {
        this.target = target;
        this.targetClass = targetClass;
        this.config = config;
        parse();
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public List<Object> getInterceptorsAndDynaicInterceptionAdvice(Method method,Class<?> targetClass) throws Exception{
        List<Object> cached = methodCache.get(method);

        //缓存未命中，则进行下一步处理
        if(cached==null){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached = methodCache.get(m);
            //存入缓存
            this.methodCache.put(m, cached);
        }
        return cached;
    }

    public boolean pointCutMatch(){
        return pointCutCClassPattern.matcher(this.targetClass.toString()).matches();
    }

    private void parse(){
        //pointCut表达式
//        String pointCut = config.getPointCut().replaceAll("\\.","\\\\.")
//                .replaceAll("\\\\.\\*",".*").replaceAll("\\(","\\\\(")
//                .replaceAll("\\)","\\\\)");

        String pointCut = config.getPointCut();

        String pointCutForClass = pointCut.substring(0,pointCut.lastIndexOf("(") -3 );

        pointCutCClassPattern = Pattern.compile("class " + pointCutForClass.substring(pointCutForClass.lastIndexOf(" ")+1));

        methodCache = new HashMap<Method, List<Object>>();
        Pattern pattern = Pattern.compile(pointCut);

        try {
            Class aspectClass = Class.forName(config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String, Method>();
            for (Method m : aspectClass.getMethods() ) {
                aspectMethods.put(m.getName(),m);
            }

            //在这里得到的方法都是原始方法
            for (Method m : targetClass.getMethods() ) {

                String methodString = m.toString();
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0, methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //能满足切面规则的类，添加到AOP配置中
                    List<Object> advice = new LinkedList<Object>();
                    //前置通知
                    if(!(null==config.getAspectBefore() || "".equals(config.getAspectBefore().trim()))){
                        advice.add(new XBMethodBeforeAdvice(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance()));
                    }

                    //后置通知
                    if(!(null==config.getAspectAfter() || "".equals(config.getAspectAfter().trim()))){
                        advice.add(new XBAfterReturningAdvice(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance()));
                    }

                    //异常通知
                    if(!(null==config.getAspectAfterThrow() || "".equals(config.getAspectAfterThrow().trim()))){
                        XBAfterThrowingAdvice afterThrowingAdvice = new XBAfterThrowingAdvice(aspectMethods.get(config.getAspectAfterThrow())
                        ,aspectClass.newInstance());
                        afterThrowingAdvice.setThrowingName(config.getAspectAfterThrowingName());
                        advice.add(afterThrowingAdvice);
                    }
                    methodCache.put(m,advice);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
