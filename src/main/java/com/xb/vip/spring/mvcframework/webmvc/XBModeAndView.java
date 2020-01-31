package com.xb.vip.spring.mvcframework.webmvc;

import java.util.Map;

/**
 * 原生Spring中ModeAndView类主要用于封装页面模板和要往页面传送的参数的对应关系
 */
public class XBModeAndView {

    private String viewName;//页面模板的名称
    private Map<String,?> model;//往页面传送的参数

    public XBModeAndView(String viewName) {
        this(viewName,null);
    }

    public XBModeAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
