package com.xb.vip.spring.demo.impl;

import com.xb.vip.spring.demo.service.IModifyService;
import com.xb.vip.spring.mvcframework.annotation.XBService;
import lombok.extern.slf4j.Slf4j;

@XBService
@Slf4j
public class ModifyService implements IModifyService {
    public String add(String name, String addr) throws Exception{
        if(name=="aop"){
            throw new Exception("故意抛出异常，测试切面通知是否生效");
        }
        return "modifyService add, name+" + name + ",addr=" + addr;
    }

    public String edit(Integer id, String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    public String remove(Integer id) {
        return "modifyService remove,id=" + id ;
    }
}
