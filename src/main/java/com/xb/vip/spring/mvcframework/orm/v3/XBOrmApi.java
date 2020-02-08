package com.xb.vip.spring.mvcframework.orm.v3;

import com.xb.vip.spring.mvcframework.orm.v3.core.common.Page;

import java.util.List;
import java.util.Map;

/**
 * ORM操作API顶层接口
 * 提供常用CRUD接口
 */
public  interface XBOrmApi<T> {

    /** 查
     * 条件查询，全部
     * @param queryRule
     * @return
     * @throws Exception
     */
    List<T> select(QueryRule queryRule) throws Exception;


    /** 查
     * 条件查询，分页
     * @param queryRule 查询条件
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return
     * @throws Exception
     */
    Page<?> select(QueryRule queryRule, int pageNo, int pageSize) throws Exception;


    /** 查
     * 根据sql获取列表
     * @param sql sql语句
     * @param args 参数
     * @return
     * @throws Exception
     */
    List<Map<String,Object>> selectBySql(String sql, Object... args) throws Exception;


    /** 查
     * 根据sql获取列表，分页
     * @param sql sql语句
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @param param 参数
     * @return
     * @throws Exception
     */
    Page<Map<String,Object>> selectBySqlToPage(String sql,Object[] param,int pageNo,int pageSize) throws Exception;


    /** 删
     * 删除一条记录
     * @param entity 都为空不执行
     * @return
     * @throws Exception
     */
    boolean delete(T entity) throws Exception;


    /** 删
     * 批量删除
     * @param list
     * @return 返回受影响的行数
     * @throws Exception
     */
    int deleteAll(List<T> list) throws Exception;


    /** 增
     * 插入一条记录自增ID
     * @param entity
     * @return
     * @throws Exception
     */
    boolean insert(T entity) throws Exception;

    /** 增
     * 批量插入
     * @param list
     * @return 返回受影响的行数
     * @throws Exception
     */
    int insertAll(List<T> list) throws Exception;


    /** 改
     * 修改一条记录
     * @param entity
     * @return
     * @throws Exception
     */
    boolean update(T entity) throws Exception;

}
