package com.xb.demo.mvc.action;

import com.xb.demo.IDemoService;
import com.xb.demo.impl.DemoService;
import com.xb.mvcframework.annotation.XBAutowired;
import com.xb.mvcframework.annotation.XBController;
import com.xb.mvcframework.annotation.XBRequestMapping;
import com.xb.mvcframework.annotation.XBRequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import com.xb.demo.common.LogFormatter;

@XBController
@XBRequestMapping("/demo")
public class DemoAction {

    static Logger logger = LogFormatter.getLog(DemoAction.class);

    @XBAutowired
    private DemoService demoService;

    @XBRequestMapping("/query")
    public void query(HttpServletRequest req, HttpServletResponse resp, @XBRequestParam("name") String name){
        String result = demoService.get(name);
        try{
            resp.getWriter().write(result);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @XBRequestMapping("/add")
    public void add(HttpServletRequest req, HttpServletResponse resp, @XBRequestParam("a") String a,
                    @XBRequestParam("b") String b){

        try{
            logger.info("add 开始了，数据流进入！ /add");
            resp.getWriter().write( a + "+" + b + "=" + (Integer.valueOf(a)+Integer.valueOf(b)));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @XBRequestMapping("/remove")
    public void remove(HttpServletRequest req, HttpServletResponse resp, @XBRequestParam("id") String id){

        try{
            resp.getWriter().write( "remove id: " + id);
        }catch (IOException e){
            e.printStackTrace();
        }
    }



}
