package com.xb.vip.spring.mvcframework.beans.suport;

import com.xb.vip.spring.mvcframework.beans.config.XBBeanDefinition;
import com.xb.vip.spring.mvcframework.context.support.XBAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 定义顶层的IOC缓存
 */
public class XBDefaultListableBeanFactory extends XBAbstractApplicationContext {

    //存储注册信息的BeanDefinition
    protected final Map<String, XBBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, XBBeanDefinition>();

}
