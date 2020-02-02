package com.xb.vip.spring.mvcframework.aop.aspect;

import com.xb.vip.spring.mvcframework.aop.intercept.XBMethodIntercepteor;
import com.xb.vip.spring.mvcframework.aop.intercept.XBMethodInvocation;

import java.lang.reflect.Method;

public class XBAfterReturningAdvice extends XBAbstractAspectJAdvice implements XBAdvice,XBMethodIntercepteor{

    private  XBJoinPoint joinPoint;

    public XBAfterReturningAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    public Object invoke(XBMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }


    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable{
        invokeAdviceMethod(joinPoint,returnValue,null);
    }
}
