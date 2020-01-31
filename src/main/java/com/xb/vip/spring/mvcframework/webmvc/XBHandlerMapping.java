package com.xb.vip.spring.mvcframework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class XBHandlerMapping {
    private Object controller;//目标方法所在的controller对象
    private Method method;//URL对应的目标方法
    private Pattern pattern;//URL的封装

    public XBHandlerMapping(Pattern pattern,Object controller, Method method) {
        this.controller = controller;
        this.method = method;
        this.pattern = pattern;
    }

    public Object getController() {
        return controller;
    }

    public void setController(Object controller) {
        this.controller = controller;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }
}
