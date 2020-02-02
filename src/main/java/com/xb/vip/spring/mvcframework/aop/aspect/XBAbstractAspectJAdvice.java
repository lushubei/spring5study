package com.xb.vip.spring.mvcframework.aop.aspect;

import com.sun.corba.se.impl.ior.OldJIDLObjectKeyTemplate;

import java.lang.reflect.Method;

/**
 * 封装拦截器回调的通用逻辑，在Mini版中主要封装了反射动态调用方法
 */
public abstract class XBAbstractAspectJAdvice implements XBAdvice{

    private Method aspectMethod;
    private Object aspectTarget;

    public XBAbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    //反射动态调用方法
    protected Object invokeAdviceMethod(XBJoinPoint joinPoint,Object returnValue,Throwable ex) throws Throwable{
        Class<?>[] paramsTypes = this.aspectMethod.getParameterTypes();
        if(null==paramsTypes || paramsTypes.length == 0){
            return this.aspectMethod.invoke(aspectTarget);
        }else{
            Object[] args = new Object[paramsTypes.length];
            for(int i = 0; i<paramsTypes.length;i++){

                if(paramsTypes[i] == XBJoinPoint.class){
                    args[i] = joinPoint;
                }else if(paramsTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(aspectTarget,args);
        }
    }

}
