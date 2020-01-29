package com.xb.vip.spring.mvcframework.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XBRequestMapping {
    String value() default "";
}
