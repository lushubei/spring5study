package com.xb.vip.spring.mvcframework.aop.intercept;


import com.xb.vip.spring.mvcframework.aop.aspect.XBJoinPoint;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行拦截链，相当于Spring中ReflectiveMethodInvocation的功能
 */
public class XBMethodInvocation implements XBJoinPoint {

    private  Object proxy;//代理对象
    private  Method method;//代理的目标方法
    private  Object target;//代理的目标对象
    private Class<?> targetClass;//代理的目标类
    private Object[] arguments;//代理的方法的实参列表
    private List<Object> interceptorsAndDynamicMethodMatchers;//回调方法链

    //保存自定义属性
    private Map<String,Object> userAttributes;

    private int currentInterceptorIndex = -1;

    public XBMethodInvocation(Object proxy, Object target,Method method,Object[] arguments,
                              Class<?> targetClass, List<Object> interceptorsAndDynamicMethodMatchers) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
    }

    public Object proceed()throws Throwable{
        /**
         * 该类的关键，先进行判断，如果拦截器为空，则说明目标方法无需增强，直接调用目标方法并返回。
         * 如果拦截器链不为空，则将拦截器链中额方法按顺序执行，直到拦截器链中所有方法全部执行完毕。
         */
        //如果Interceptor执行完了，则执行joinPoint
        if(this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMatchers.size() - 1){
            return this.method.invoke(this.target,this.arguments);
        }
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMatchers.get(++this.currentInterceptorIndex);

        //如果要动态匹配joinPoint
        if(interceptorOrInterceptionAdvice instanceof  XBMethodIntercepteor){
            XBMethodIntercepteor mi = (XBMethodIntercepteor) interceptorOrInterceptionAdvice;
            return mi.invoke(this);
        }else{//执行当前Interceptor
            return proceed();
        }
    }

    public Method getMethod() {
        return null;
    }

    public Object[] getArguments() {
        return new Object[0];
    }

    public Object getThis() {
        return null;
    }

    public void setUserAttribute(String key, Object value) {
        if(value!=null){
            if(this.userAttributes==null){
                this.userAttributes = new HashMap<String, Object>();
            }
            this.userAttributes.put(key, value);
        }else if(this.userAttributes != null){
            this.userAttributes.remove(key);
        }

    }

    public Object getUserAttribute(String key) {
        return (this.userAttributes != null ? this.userAttributes.get(key):null);
    }

}
