package com.xb.vip.spring.mvcframework.webmvc.servlet;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class XBDispatcherServlet extends HttpServlet{

    @Override
    public void init(ServletConfig config) throws ServletException{
        log.debug("现在开始初始化");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        log.debug("get 入口");
        this.doPost(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
        log.debug("post 函数的处理部分在这里");
        resp.setHeader("Content-type", "text/html;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write("这几给你返回个数据");
    }
}

