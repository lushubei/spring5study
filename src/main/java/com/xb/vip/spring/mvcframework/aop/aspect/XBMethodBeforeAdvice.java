package com.xb.vip.spring.mvcframework.aop.aspect;

import com.xb.vip.spring.mvcframework.aop.intercept.XBMethodIntercepteor;
import com.xb.vip.spring.mvcframework.aop.intercept.XBMethodInvocation;

import java.lang.reflect.Method;

/**
 * 前置通知具体实现
 */
public class XBMethodBeforeAdvice extends XBAbstractAspectJAdvice implements XBAdvice,XBMethodIntercepteor {
    private XBJoinPoint joinPoint;

    public XBMethodBeforeAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    public void before(Method method,Object[] args, Object target) throws Throwable{
        invokeAdviceMethod(this.joinPoint,null,null);
    }

    public Object invoke(XBMethodInvocation mi) throws Throwable {
        this.joinPoint = mi;
        this.before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }
}
