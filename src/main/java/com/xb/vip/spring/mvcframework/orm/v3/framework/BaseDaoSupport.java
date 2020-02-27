package com.xb.vip.spring.mvcframework.orm.v3.framework;

import com.xb.vip.spring.mvcframework.orm.v3.QueryRule;
import com.xb.vip.spring.mvcframework.orm.v3.core.common.Page;
import com.xb.vip.spring.mvcframework.orm.v3.core.jdbc.BaseDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BaseDao 扩展类，主要功能是支持自动拼装SQL语句，
 * @param <T>
 * @param <PK>
 */
@Slf4j
public abstract class BaseDaoSupport<T extends Serializable, PK extends Serializable> implements BaseDao<T,PK> {

    private String tableName = "";

    private JdbcTemplate jdbcTemplateWrite;
    private JdbcTemplate jdbcTemplateReadOnly;

    private DataSource dataSourceReadOnly;
    private DataSource dataSourceWrite;

    private EntityOperation<T> op;

    @SuppressWarnings("unchecked")
    protected BaseDaoSupport(){
        try {
            Class<T> entityClass = GenericsUtils.getSuperClassGenricType(getClass(), 0);
            op = new EntityOperation<T>(entityClass, this.getPKColumn());
            this.setTableName(op.tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected String getTableName() {
        return tableName;
    }

    protected DataSource getDataSourceReadOnly() {
        return dataSourceReadOnly;
    }

    protected DataSource getDataSourceWrite() {
        return dataSourceWrite;
    }

    /**
     * 动态切换表名
     * @param tableName
     */
    protected void setTableName(String tableName) {
        if(StringUtils.isEmpty(tableName))
             this.tableName = op.tableName;
        else this.tableName = tableName;
    }

    protected void setDataSourceWrite(DataSource dataSourceWrite) {
        this.dataSourceWrite = dataSourceWrite;
        jdbcTemplateWrite = new JdbcTemplate(dataSourceWrite);
    }

    protected void setDataSourceReadOnly(DataSource dataSourceReadOnly) {
        this.dataSourceReadOnly = dataSourceReadOnly;
        jdbcTemplateReadOnly = new JdbcTemplate(dataSourceReadOnly);
    }

    private JdbcTemplate getJdbcTemplateReadOnly(){
        return this.jdbcTemplateReadOnly;
    }

    private JdbcTemplate getJdbcTemplateWrite(){
        return this.jdbcTemplateWrite;
    }

    /**
     * 还原默认表名
     */
    protected void restoreTableName(){
        this.setTableName(op.tableName);
    }

    /**
     * 获取主键列名称，建议子类重写
     * @return
     */
    protected abstract String getPKColumn();

    protected abstract void  setDataSouce(DataSource dataSouce);

    //此处有省略




    /**
     * 泛型工具类
     * @author guwenren
     *
     */
    public static class GenericsUtils {
        /**
         * 通过反射,获得指定类的父类的泛型参数的实际类型. 如BuyerServiceBean extends DaoSupport<Buyer>
         *
         * @param clazz clazz 需要反射的类,该类必须继承范型父类
         * @param index 泛型参数所在索引,从0开始.
         * @return 范型参数的实际类型, 如果没有实现ParameterizedType接口，即不支持泛型，所以直接返回<code>Object.class</code>
         */
        @SuppressWarnings({ "rawtypes" })
        public static Class getSuperClassGenricType(Class clazz, int index) {
            Type genType = clazz.getGenericSuperclass();//得到泛型父类
            //如果没有实现ParameterizedType接口，即不支持泛型，直接返回Object.class
            if (!(genType instanceof ParameterizedType)) {
                return Object.class;
            }
            //返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class, 如BuyerServiceBean extends DaoSupport<Buyer,Contact>就返回Buyer和Contact类型
            Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
            if (index >= params.length || index < 0) {
                throw new RuntimeException("你输入的索引"+ (index<0 ? "不能小于0" : "超出了参数的总数"));
            }
            if (!(params[index] instanceof Class)) {
                return Object.class;
            }
            return (Class) params[index];
        }
    }

    /**
     * 查询函数，使用查询规则
     * 例如以下代码查询条件为匹配的数据
     * @param queryRule 查询规则
     * @return 查询的结果List
     * @throws Exception
     */
    public List<T> select(QueryRule queryRule) throws Exception{
        QueryRuleSqlBuilder builder = new QueryRuleSqlBuilder(queryRule);
        String ws = removeFirstAnd(builder.getWhereSql());
        String whereSql = ("".equals(ws) ? ws :(" where " + ws));
        String sql = "selec " + op.allColumn + " from " + getTableName() + whereSql;
        Object [] values = builder.getValues();
        String orderSql = builder.getOrderSql();
        orderSql = (StringUtils.isEmpty(orderSql) ? " " : (" order by " + orderSql));
        sql += orderSql;
        log.debug(sql);
        return (List<T>) this.getJdbcTemplateReadOnly().query(sql, this.op.rowMapper, values);
    }

    //...todo

    /**
     * 根据SQL语境执行查询，参数为Object数字对象
     * @param sql sql语句
     * @param args Object参数
     * @return 符合条件的所有对象
     * @throws Exception
     */
    public List<Map<String,Object>> selectBySql(String sql,Object... args) throws Exception{
        return this.getJdbcTemplateReadOnly().queryForList(sql, args);
    }
    //...todo

    /**
     * 分页查询函数，使用查询规则
     * @param queryRule 查询条件
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return
     * @throws Exception
     */
    public Page<T> select(QueryRule queryRule, final int pageNo, final int pageSize) throws Exception{
        QueryRuleSqlBuilder builder = new QueryRuleSqlBuilder(queryRule);
        Object[] values = builder.getValues();
        String ws = removeFirstAnd(builder.getWhereSql());
        String whereSql = ("".equals(ws) ? ws : (" where " + ws));
        String countSql = "select count(1) from " + getTableName() + whereSql;
        long count = (Long) this.getJdbcTemplateReadOnly().queryForMap(countSql, values).get("count(1)");

        if(count == 0){
            return new Page<T>();
        }
        long start = (pageNo - 1) * pageSize;
        //在有数据的情况下，继续查询
        String orderSql = builder.getOrderSql();
        orderSql = (StringUtils.isEmpty(orderSql) ? " " : (" order by " + orderSql));
        String sql = "select " + op.allColumn + " from " + getTableName() + whereSql + orderSql + " limit " +
                start + "," + pageSize;
        List<T> list = (List<T>) this.getJdbcTemplateReadOnly().query(sql, this.op.rowMapper,values);
        log.debug(sql);
        return new Page<T>(start, count, pageSize, list);
    }

    /**
     * 分页查询特殊SQL语句
     * @param sql 语句
     * @param param 查询条件
     * @param pageNo 页面
     * @param pageSize 每页数量
     * @return
     * @throws Exception selectBySqlToPage
     */
    public Page<Map<String,Object>> selectBySqlToPage(String sql, Object[] param, final int pageNo, final int pageSize)
        throws Exception{
        String countSql = "select count(1) from (" + sql + ") a";
        long count = (Long) this.getJdbcTemplateReadOnly().queryForMap(countSql, param).get("count(1)");
        if(count == 0){
            return new Page<Map<String, Object>>();
        }
        long start = (pageNo - 1) * pageSize;
        sql = sql + " limit " + start + "," + pageSize;
        List<Map<String,Object>> list = (List<Map<String,Object>>) this.getJdbcTemplateReadOnly().queryForList(sql, param);

        log.debug(sql);
        return new Page<Map<String,Object>>(start, count, pageSize, list);
    }


    /**
     * 获取默认的实例对象
     * @param pkValue
     * @param rowMapper
     * @return
     */
    private <T> T doLoad(Object pkValue, RowMapper<T> rowMapper){
        Object obj = this.doLoad(getTableName(), getPKColumn(), pkValue, rowMapper);
        if(obj != null){
            return (T)obj;
        }
        return null;
    }

    /**
     * 插入并返回ID
     * @param entity 只要entity 不等于null,就执行插入操作
     * @return
     * @throws Exception
     */
    public PK insertAndReturnId(T entity) throws Exception{
        return (PK)this.doInsertRuturnKey(parse(entity));
    }

    /**
     * 插入一条记录
     * @param entity
     * @return
     * @throws Exception
     */
    public boolean insert(T entity) throws  Exception{
        return this.doIsert(parse(entity));
    }

    /**
     * 批量保存对象
     * @param list 待保存对象List
     * @return
     * @throws Exception
     */
    public int insertAll(List<T> list) throws Exception{
        int count = 0 , len = list.size(),step = 50000;
        Map<String, PropertyMapping> pm = op.mappings;

        int maxPage = (len % step == 0) ? (len / step) : (len / step + 1);
        for (int i = 1; i<= maxPage; i++){
            Page<T> page = pagination(list, i, step);

            // (" + valstrtoString() + ")"
            String sql = "insert into " + getTableName() + "(" + op.allColumn + ") valuses ";
            StringBuffer valstr = new StringBuffer();
            Object[] values = new Object[pm.size() * page.getRows().size()];
            for(int j = 0; j < page.getRows().size(); j++){
                if(j > 0 && j < page.getRows().size()){
                    valstr.append(",");
                }
                valstr.append("(");
                int k = 0;
                for(PropertyMapping p : pm.values()){
                    values[(j * pm.size()) + k] = p.getter.invoke(page.getRows().get(j));
                    if(k>0 && k < pm.size()){ valstr.append(",");}
                    valstr.append("?");
                    k ++;
                }
                valstr.append(")");
            }
            int result = getJdbcTemplateWrite().update(sql + valstr.toString(), values);
            count += result;
        }
        return count;
    }


    private Serializable doInsertRuturnKey(Map<String,Object> params){
        final List<Object> values = new ArrayList<Object>();
        final String sql = makeSimpleInsertSql(getTableName(), params);//参数减掉values, 无用
        KeyHolder keyHolder = new GeneratedKeyHolder();
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(getDataSourceWrite());

        try {
            jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                    for(int i = 0; i<values.size(); i ++){
                        ps.setObject(i+1, values.get(i) == null ?null : values.get(i));
                    }
                    return ps;
                }
            },keyHolder);
        } catch (DataAccessException e) {
            log.error("error",e);
        }

        if(keyHolder ==null) return "";

        Map<String, Object> keys = keyHolder.getKeys();
        if(keys == null || keys.size() == 0 || keys.values().size() == 0) return "";

        Object key = keys.values().toArray()[0];
        if(key==null || !(key instanceof Serializable)) return "";

        if(key instanceof Number){
            Class<?> clazz = key.getClass();
            return (clazz == int.class || clazz == Integer.class ? ((Number) key).intValue() : ((Number) key).longValue());
        } else if(key instanceof String){
            return (String) key;
        }else{
            return (Serializable) key;
        }
    }

    /**
     * 插入
     * @param params
     * @return
     */
    private boolean doIsert(Map<String, Object> params){
        String sql = this.makeSimpleInsertSql(this.getTableName(), params);
        int ret = this.getJdbcTemplateWrite().update(sql, params.values().toArray());
        return ret > 0;
    }

    /**
     * 删除单个对象
     * @param entity 待删除的实体对象
     * @return
     * @throws Exception
     */
    public boolean delete(T entity) throws Exception{
        return this.doDelete(getTableName(),op.pkField.getName(),op.pkField.get(entity)) > 0;
    }

    /**
     * 批量删除对象
     * @param list 待删除的实体对象列表
     * @return
     * @throws Exception
     */
    public int deleteAll(List<T> list) throws Exception{
        String pkName = op.pkField.getName();
        int count = 0 , len = list.size(), step = 1000;
        Map<String, PropertyMapping> pm = op.mappings;
        int maxPage = (len % step == 0) ? (len / step) : (len / step + 1);
        for(int i = 1; i <= maxPage; i++){
            StringBuffer valStr = new StringBuffer();
            Page<T> page = pagination(list, i, step);
            Object[] values = new Object[page.getRows().size()];

            for(int j = 0; j < page.getRows().size(); j++){
                if(j > 0 && j < page.getRows().size()){
                    valStr.append(",");
                }
                values[j] = pm.get(pkName).getter.invoke(page.getRows().get(j));
                valStr.append("?");
            }

            String sql = "delete from " + getTableName() + " where " + pkName + " in (" + valStr.toString() + ")";
            int result = getJdbcTemplateWrite().update(sql, values);
            count += result;
        }
        return count;
    }

    /**
     * 根据id删除对象。如果有记录则删之，没有记录也不报异常
     * @param id 序列化id
     * @throws Exception
     */
//    protected  void deleteByPK(PK id) throws Exception{
//        this.doDelete(id);
//    }

    /**
     * 删除实体对象，返回删除记录数
     * @param tableName
     * @param pkName
     * @param pkValue
     * @return
     */
    private int doDelete(String tableName, String pkName, Object pkValue){
        StringBuffer sb = new StringBuffer();
        sb.append("delete from ").append(tableName).append(" where ").append(pkName).append(" = ?");
        int ret = this.getJdbcTemplateWrite().update(sb.toString(), pkValue);
        return ret;
    }

    /**
     * 更新对象
     * @param entity 待更新实体
     * @return
     * @throws Exception
     */
    public boolean update(T entity) throws Exception{
        return this.doUpdate(op.pkField.get(entity), parse(entity)) > 0;
    }

    /**
     * 更新实例对象，返回删除记录数
     * @param pkValue
     * @param params
     * @return
     */
    private int doUpdate(Object pkValue, Map<String, Object> params){
        String sql = this.makeDefaultSimpleUpdateSql(pkValue, params);
        params.put(this.getPKColumn(), pkValue);
        int ret = this.getJdbcTemplateWrite().update(sql, params.values().toArray());
        return ret;
    }


    //todo -- 以下
    protected abstract String removeFirstAnd(String whereSql);

    protected abstract String makeDefaultSimpleUpdateSql(Object pkValue, Map<String, Object> params);

    protected abstract <T> Object doLoad(String tableName, String pkColumn, Object pkValue, RowMapper<T> rowMapper);

    protected abstract <T> Map<String,Object> parse(T entity);

    protected abstract Page<T> pagination(List<T> list, int i, int step);

    protected abstract String makeSimpleInsertSql(String tableName, Map<String, Object> params);

}


