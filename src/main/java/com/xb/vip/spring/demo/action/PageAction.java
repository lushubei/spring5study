package com.xb.vip.spring.demo.action;


import com.xb.vip.spring.demo.impl.QueryService;
import com.xb.vip.spring.mvcframework.annotation.XBAutowired;
import com.xb.vip.spring.mvcframework.annotation.XBController;
import com.xb.vip.spring.mvcframework.annotation.XBRequestMapping;
import com.xb.vip.spring.mvcframework.annotation.XBRequestParam;
import com.xb.vip.spring.mvcframework.webmvc.XBModeAndView;

import java.util.HashMap;
import java.util.Map;

/**
 * 专门设计为了演示Mini版Spring对模板引擎额支持
 * 实现从Controller层到View层的传参，以及对模板的渲染进行最终输出
 */
@XBController
@XBRequestMapping("/web")
public class PageAction {

    @XBAutowired
    QueryService queryService;

    @XBRequestMapping("/first.html")
    public XBModeAndView query(@XBRequestParam("teacher") String teacher){
        String result = queryService.query(teacher);
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("teacher", teacher);
        model.put("data",result);
        model.put("token","123456");
        return new XBModeAndView("first.html",model);
    }
}
