package com.xb.vip.spring.mvcframework.aop.intercept;

/**
 * 方法拦截器顶层接口
 */
public interface XBMethodIntercepteor {
    Object invoke(XBMethodInvocation mi) throws Throwable;
}
