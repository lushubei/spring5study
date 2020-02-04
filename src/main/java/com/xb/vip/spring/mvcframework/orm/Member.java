package com.xb.vip.spring.mvcframework.orm;

import lombok.Data;

import java.io.Serializable;

@Data
public class Member implements Serializable {

   private Long id;
   private String name;
   private int age;

}
