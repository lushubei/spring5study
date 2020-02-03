package com.xb.vip.spring.demo.aspect;

import com.xb.vip.spring.mvcframework.aop.aspect.XBJoinPoint;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class LogAspect {

    //在调用一个方法之前，执行before()方法
    public void before(XBJoinPoint joinPoint){
        joinPoint.setUserAttribute("startTime_" + joinPoint.getMethod().getName(),System.currentTimeMillis());
        log.info("Invoker Before Method!!!" + "\nTargetObject:" + joinPoint.getThis() +
        "\nArgs:" + Arrays.toString(joinPoint.getArguments()));
    }

    //在调用一个方法之后，执行after()方法
    public void after(XBJoinPoint joinPoint){
        log.info("Invoker After Method!!!" + "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()));

        long startTime = (Long) joinPoint.getUserAttribute("startTime_" + joinPoint.getMethod().getName());
        long endTime = (Long) System.currentTimeMillis();
        System.out.println("use time:" + (endTime - startTime));
    }

    public void afterThrowing(XBJoinPoint joinPoint, Throwable ex){


        log.info("出现异常" + "\nTargetObject:" + joinPoint.getThis() +
                "\nArgs:" + Arrays.toString(joinPoint.getArguments()) +
                "\nThrows:" + ex.getMessage()
        );
    }


}
