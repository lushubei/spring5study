package com.xb.vip.spring.mvcframework.core;

public interface XBBeanFactory {
    Object getBean(String beanName) throws  Exception;

    public Object getBean(Class<?> beanClass) throws Exception;
}
