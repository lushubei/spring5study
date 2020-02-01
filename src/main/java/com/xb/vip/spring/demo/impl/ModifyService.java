package com.xb.vip.spring.demo.impl;

import com.xb.vip.spring.demo.service.IModifyService;
import com.xb.vip.spring.mvcframework.annotation.XBService;
import lombok.extern.slf4j.Slf4j;

@XBService
@Slf4j
public class ModifyService implements IModifyService {
    public String add(String name, String addr) {
        return "modifyService add, name+" + name + ",addr=" + addr;
    }

    public String edit(Integer id, String name) {
        return "modifyService edit,id=" + id + ",name=" + name;
    }

    public String remove(Integer id) {
        return "modifyService remove,id=" + id ;
    }
}
