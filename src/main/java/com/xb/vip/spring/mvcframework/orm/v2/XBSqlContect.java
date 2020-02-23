package com.xb.vip.spring.mvcframework.orm.v2;

import com.xb.vip.spring.mvcframework.orm.annotation.Column;
import com.xb.vip.spring.mvcframework.orm.annotation.Table;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XBSqlContect {

    public static List<Object> select(Object condition){

        List<Object> result = new ArrayList<Object>();
        Class<?> entityClass = condition.getClass();

        Connection con = null;
        PreparedStatement pstm = null;
        ResultSet rs = null;

        try {
            //加载驱动类
            Class.forName("com.mysql.jdbc.Driver");

            //建立连接
            con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3307/spring5study","root","root");

            //根据类名找属性名
            Map<String,String> columnMapper = new HashMap<String, String>();

            //根据属性名找字段名
            Map<String,String> fieldMapper = new HashMap<String, String>();

            Field[] fields = entityClass.getDeclaredFields();

            for (Field field : fields ) {
                field.setAccessible(true);
                String fieldName = field.getName();
                if(field.isAnnotationPresent(Column.class)){
                   Column column = field.getAnnotation(Column.class);
                   String columnName = column.name();
                   columnMapper.put(columnName, fieldName);
                   fieldMapper.put(fieldName,columnName);
                }else{
                    //默认就是字段名、属性名一致
                    columnMapper.put(fieldName,fieldName);
                    fieldMapper.put(fieldName,fieldName);
                }
            }

            //3.创建语句集
            Table table = entityClass.getAnnotation(Table.class);
            String sql = "select * from " + table.name();


            StringBuffer where = new StringBuffer("");
            if (fields != null && fields.length > 0){
                where = new StringBuffer(" where 1=1 ");
            }

            for (Field field: fields ) {
                Object value = field.get(condition);

                if(null != value){
                    if(String.class == field.getType()){
                        where.append(" and " + fieldMapper.get(field.getName()) + "='" + value + "'");
                    }else{
                        where.append(" and " + fieldMapper.get(field.getName()) + "=" + value);
                    }//其他暂不一一举例
                }
            }

            String real_sql = sql + where.toString();
            System.out.println(real_sql);

            pstm = con.prepareStatement(real_sql);
            rs = pstm.executeQuery(sql + where.toString());

            while (rs.next()){
                Object instance = mapperRow(rs, entityClass, columnMapper);
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

    private static Object mapperRow(ResultSet rs, Class<?> entityClass, Map<String,String> columnMapper) throws Exception{

        Object instance = entityClass.newInstance();

        //保存了处理真正数值以外的所有附加信息
        int columnCounts = rs.getMetaData().getColumnCount();

        //通过反射机制拿到拿到实体类的所有字段
        for(int i = 1; i <= columnCounts; i++){
            String columnName = rs.getMetaData().getColumnName(i);
            Field field = entityClass.getDeclaredField(columnMapper.get(columnName));
            field.setAccessible(true);
            field.set(instance,rs.getObject(columnName));
        }

        return instance;
    }
}



