package com.xb.vip.spring.demo.entity;

import com.xb.vip.spring.mvcframework.orm.annotation.Column;
import com.xb.vip.spring.mvcframework.orm.annotation.Table;
import lombok.Data;
import java.io.Serializable;

@Table(name = "xborm")
@Data
public class Member implements Serializable {

   @Column(name = "id")
   private Integer id;

   @Column(name = "name")
   private String name;

   @Column(name = "age")
   private int age;


   @Override
   public String toString() {
      return "Member{" +
              "id=" + id +
              ", name='" + name + '\'' +
              ", age=" + age +
              '}';
   }
}
