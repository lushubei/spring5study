package com.xb.vip.spring.mvcframework.orm.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
}
