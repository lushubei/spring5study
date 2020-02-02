package com.xb.vip.spring.demo.service;

/**
 * 增删改业务
 */
public interface IModifyService {

    public String add(String name, String addr) throws Exception; //抛出异常，为了测试AOP

    public String edit(Integer id, String name);

    public String remove(Integer id);
}
