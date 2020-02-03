package com.xb.vip.spring.mvcframework.aop.aspect;

import com.xb.vip.spring.mvcframework.aop.intercept.XBMethodIntercepteor;
import com.xb.vip.spring.mvcframework.aop.intercept.XBMethodInvocation;

import java.lang.reflect.Method;

public class XBAfterThrowingAdvice extends XBAbstractAspectJAdvice implements XBAdvice,XBMethodIntercepteor{

    private String throwingName;
    private XBMethodInvocation mi;

    public XBAfterThrowingAdvice(Method aspectMethod, Object target) {
        super(aspectMethod, target);
    }

    public void setThrowingName(String throwingName) {
        this.throwingName = throwingName;
    }

    public Object invoke(XBMethodInvocation mi) throws Throwable {

        try {
            return mi.proceed();
        } catch (Throwable ex) {
            invokeAdviceMethod(mi,null,ex.getCause());
            ex.printStackTrace();

            throw ex;
        }
    }
}
