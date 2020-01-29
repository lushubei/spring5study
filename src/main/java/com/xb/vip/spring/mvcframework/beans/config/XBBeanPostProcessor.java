package com.xb.vip.spring.mvcframework.beans.config;

public class XBBeanPostProcessor {

    //为在Bean的初始化之前提供回调入口
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception {
        //todo
        return bean;
    }

    //为在Bean的初始化之后提供回调入口
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception {
        //todo
        return bean;
    }
}
