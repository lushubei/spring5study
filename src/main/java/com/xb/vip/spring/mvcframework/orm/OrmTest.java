package com.xb.vip.spring.mvcframework.orm;
import com.xb.vip.spring.demo.entity.Member;
import com.xb.vip.spring.mvcframework.orm.v2.XBSqlContect;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

@Slf4j
public class OrmTest {

    XBSqlContect xbSqlContect;

    @Before
    public void before(){
        log.info("初始化工作！");
    }

    @After
    public void after(){
        log.info("后续清理工作！");
    }

    @Test
    public void testV101(){
        /**
         * 测试v1版本 查询
         * 其利用方法调用
         */
        String sql = "select * from xborm";
        com.xb.vip.spring.mvcframework.orm.v1.XBSqlContect xbSqlContect = new com.xb.vip.spring.mvcframework.orm.v1.XBSqlContect();
        List<Member> s = xbSqlContect.select(sql);
        System.out.println(s);
    }


    @Test
    public void testV201(){
        /**
         * 测试v2版本 查询，
         * 其利用反射机制，注解
         */

        Member condition = new Member();
//        condition.setName("xiaobei");
//        condition.setAge(24);

        XBSqlContect xbSqlContect = new XBSqlContect();
        List<?> result  = xbSqlContect.select(condition);
        System.out.println(result);
    }

}
