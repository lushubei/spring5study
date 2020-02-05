package com.xb.vip.spring.mvcframework.orm.v2;
import com.xb.vip.spring.demo.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

@Slf4j
public class MyTest{

    XBSqlContect xbSqlContect;

    @Before
    public void before(){
        XBSqlContect xbSqlContect = new XBSqlContect();
        log.info("初始化工作！");
    }

    @After
    public void after(){
        log.info("后续清理工作！");
    }

    @Test
    public void test1(){
        /**
         * 查询
         */
        String sql = "select * from xborm";
        com.xb.vip.spring.mvcframework.orm.v1.XBSqlContect xbSqlContect = new com.xb.vip.spring.mvcframework.orm.v1.XBSqlContect();
        List<Member> s = xbSqlContect.select(sql);
        System.out.println(s);
    }


    @Test
    public void test2(){
        /**
         * 优化JDBC操作
         */

        Member condition = new Member();
        condition.setName("xiaobei");
        condition.setAge(24);

        List<?> result  = xbSqlContect.select(condition);
        System.out.println(result);
    }

}
