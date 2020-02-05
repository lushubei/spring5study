package com.xb.vip.spring.mvcframework.orm.v1;

import com.xb.vip.spring.demo.entity.Member;
import org.junit.Test;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class XBSqlContect {

    public List<Member> select(String sql){

        List<Member> result = new ArrayList<Member>();
        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");

            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/spring5study","root","root");

            pstm = con.prepareStatement(sql);

            rs = pstm.executeQuery();

            while (rs.next()){
                Member instance = new Member();
                instance.setId(rs.getLong("id"));
                instance.setName(rs.getString("name"));
                instance.setAge(rs.getInt("age"));

                result.add(instance);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {

            try {
                rs.close();
                pstm.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


    @Test
    public void test(){
        String sql = "select * from xborm";
        List<Member> s = this.select(sql);
        System.out.println(s);
    }
}
