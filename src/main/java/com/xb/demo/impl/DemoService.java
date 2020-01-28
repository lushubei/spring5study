package com.xb.demo.impl;

import com.xb.demo.IDemoService;
import com.xb.mvcframework.annotation.XBService;

@XBService
public class DemoService implements IDemoService{

    public String get(String name){
        return "My name is " + name;
    }
}

