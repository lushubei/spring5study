package com.xb.vip.spring.mvcframework.beans;

import com.xb.vip.spring.mvcframework.beans.config.XBBeanDefinition;
import com.xb.vip.spring.mvcframework.context.support.XBAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于封装创建后的对象实例，代理对象或者原生对象都有BeanWrapper来保存
 */
public class XBBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public XBBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return wrappedInstance.getClass();
    }

}
