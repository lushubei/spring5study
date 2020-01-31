package com.xb.vip.spring.demo.impl;

import com.xb.vip.spring.mvcframework.annotation.XBService;
import com.xb.vip.spring.demo.IDemoService;

@XBService
public class DemoService implements IDemoService {

    public String get(String name){
        return "My name is " + name;
    }
}

