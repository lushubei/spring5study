package com.xb.vip.spring.mvcframework.aop;

import com.xb.vip.spring.mvcframework.aop.support.XBAdviseSupport;

/**
 * 使用CGlib API生产代理类
 */
public class XBCglibAopProxy implements XBAopProxy{

    //todo 本例未实现CglibAopProxy

    private XBAdviseSupport config;

    public XBCglibAopProxy(XBAdviseSupport config) {
        this.config = config;
    }

    public Object getProxy() {
        return null;
    }

    public Object getProxy(ClassLoader classLoader) {
        return null;
    }

    public XBAdviseSupport getConfig() {
        return config;
    }

    public void setConfig(XBAdviseSupport config) {
        this.config = config;
    }


}
