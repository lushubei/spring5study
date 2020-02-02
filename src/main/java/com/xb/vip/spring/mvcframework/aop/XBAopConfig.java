package com.xb.vip.spring.mvcframework.aop;

import lombok.Data;

/**
 * AOP配置封装
 */
@Data
public class XBAopConfig {

    //以下配置与properties文件中的属性一一对应
    private String pointCut; //切面表达式
    private String aspectBefore; //前置通知方法名
    private String aspectAfter; //后置通知方法名
    private String aspectClass;//要织入的切面类
    private String aspectAfterThrow;//异常通知方法名
    private String aspectAfterThrowingName;//需要通知的异常类型


}
