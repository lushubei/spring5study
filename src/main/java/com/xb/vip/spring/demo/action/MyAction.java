package com.xb.vip.spring.demo.action;

import com.xb.vip.spring.demo.impl.ModifyService;
import com.xb.vip.spring.demo.impl.QueryService;
import com.xb.vip.spring.demo.service.IModifyService;
import com.xb.vip.spring.demo.service.IQueryService;
import com.xb.vip.spring.mvcframework.annotation.XBAutowired;
import com.xb.vip.spring.mvcframework.annotation.XBController;
import com.xb.vip.spring.mvcframework.annotation.XBRequestMapping;
import com.xb.vip.spring.mvcframework.annotation.XBRequestParam;
import com.xb.vip.spring.mvcframework.webmvc.XBModeAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@XBController
@XBRequestMapping("/web")
public class MyAction {

    @XBAutowired
    QueryService queryService;

    @XBAutowired
    ModifyService modifyService;

    @XBRequestMapping("/query.json")
    public XBModeAndView query(HttpServletRequest req, HttpServletResponse resp,
                               @XBRequestParam("name") String name){
        String result = queryService.query(name);
        return out(resp,result);

    }


    @XBRequestMapping("/add*.json")
    public XBModeAndView add(HttpServletRequest req, HttpServletResponse resp,
                               @XBRequestParam("name") String name, @XBRequestParam("addr") String addr) throws Exception{
        String result = modifyService.add(name,addr);
        return out(resp,result);

    }


    @XBRequestMapping("/remove.json")
    public XBModeAndView remove(HttpServletRequest req, HttpServletResponse resp,
                               @XBRequestParam("id") Integer id){
        String result = modifyService.remove(id);
        return out(resp,result);

    }

    @XBRequestMapping("/edit.json")
    public XBModeAndView remove(HttpServletRequest req, HttpServletResponse resp,
                                @XBRequestParam("id") Integer id,@XBRequestParam("name") String name){
        String result = modifyService.edit(id,name);
        return out(resp,result);
    }


    private XBModeAndView out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
