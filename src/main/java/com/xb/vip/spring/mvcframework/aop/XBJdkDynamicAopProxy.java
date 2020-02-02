package com.xb.vip.spring.mvcframework.aop;

import com.xb.vip.spring.mvcframework.aop.intercept.XBMethodInvocation;
import com.xb.vip.spring.mvcframework.aop.support.XBAdviseSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * 使用JDK Proxy API生成代理类
 */
public class XBJdkDynamicAopProxy implements XBAopProxy,InvocationHandler {
    private XBAdviseSupport config;

    public XBJdkDynamicAopProxy(XBAdviseSupport config) {
        this.config = config;
    }

    //把原生对象传进来
    public Object getProxy() {
        return getProxy(this.config.getTargetClass().getClassLoader());
    }

    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.config.getTargetClass().getInterfaces(),this);
    }

    //invoke方法是执行代理的关键入口
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //将每一个JoinPoint也就是被代理的业务方法 (Method) 封装成一个拦截器，组合成一个拦截链
        List<Object> interceptorndDynamicMethodMatchers =
                config.getInterceptorsAndDynaicInterceptionAdvice(method,this.config.getTargetClass());

        //交给拦截器链MethodInvocation的proceed()方法执行
        XBMethodInvocation invocation = new XBMethodInvocation(proxy,this.config.getTarget(),method,args,
                this.config.getTargetClass(),interceptorndDynamicMethodMatchers);
        return invocation.proceed();
    }
}
