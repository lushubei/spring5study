package com.xb.vip.spring.mvcframework.context;

/**
 * 通过解耦方式获得IOC容器的顶层设计
 * 后面通过一个监听器去扫描所欲的类，只要实现了此接口，
 * 将自动调用setApplicationContext()方法，从而将IOC容器注入密保类中
 */
public interface XBApplicationContextAware {
    void setApplicationContext(XBApplicationContext applicationContext);
}