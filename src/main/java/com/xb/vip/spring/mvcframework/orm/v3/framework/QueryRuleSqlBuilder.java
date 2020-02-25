package com.xb.vip.spring.mvcframework.orm.v3.framework;

import com.xb.vip.spring.mvcframework.orm.v3.QueryRule;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 根据QueryRule自动构建SQL语句
 */
public class QueryRuleSqlBuilder {
    /**记录参数所在的位置*/
    private int CURR_INDEX = 0;
    /**保存列名列表*/
    private List<String> properties;
    /**保存参数值列表*/
    private List<Object> values;
    /**保存排序规则表*/
    private List<Object> orders;

    private String whereSql = "";
    private String orderSql = "";
    private Object[] valueArr = new Object[]{};
    private Map<Object,Object> valueMap = new HashMap<Object, Object>();

    /**
     * 获取参数值列表
     * @return
     */
    public Object[] getValues() {
        return valueArr;
    }

    /**
     * 获取查询条件
     * @return
     */
    public String getWhereSql() {
        return whereSql;
    }

    /**
     * 获取排序条件
     * @return
     */
    public String getOrderSql() {
        return orderSql;
    }

    /**
     * 获取参数列表
     * @return
     */
    public Map<Object, Object> getValueMap() {
        return valueMap;
    }

    public QueryRuleSqlBuilder(QueryRule queryRule) {
        CURR_INDEX = 0;
        properties = new ArrayList<String>();
        values = new ArrayList<Object>();
        orders = new ArrayList<Object>();
        for(QueryRule.Rule rule: queryRule.getRuleList()){
            switch (rule.getType()){
                case QueryRule.BETWEEN:
                    processBetween(rule);
                    break;
                case QueryRule.EQ:
                    processEqual(rule);
                    break;

                case QueryRule.LIKE:
                    processLike(rule);
                    break;
                case QueryRule.NOTEQ:
                    prcessNotEqual(rule);
                    break;
                case QueryRule.GT:
                    processGreaterThen(rule);
                    break;
                case QueryRule.GE:
                    processGreaterEqual(rule);
                    break;
                case QueryRule.LT:
                    processLessThen(rule);
                    break;
                case QueryRule.LE:
                    processLessEqual(rule);
                    break;
                case QueryRule.IN:
                    processIn(rule);
                    break;
                case QueryRule.NOTIN:
                    processNotIn(rule);
                    break;
                case QueryRule.ISNULL:
                    processIsNull(rule);
                    break;

                case QueryRule.ISNOTNULL:
                    processIsNotNull(rule);
                    break;

                case QueryRule.ISEMPTY:
                    processIsEmpty(rule);
                    break;
                case QueryRule.ISNOTEMPTY:
                    processIsNotEmpty(rule);
                    break;
                case QueryRule.ASC_ODER:
                    processOrder(rule);
                    break;
                case QueryRule.DESC_ODER:
                    processOrder(rule);
                    break;
                default:
                    throw new IllegalArgumentException("type" + rule.getType() + "not supported.");
            }
        }
        //拼装where语句
        appendWhereSql();
        //拼装排序语句
        appendOrderSql();
        //拼装参数值
        appendValues();
    }

    /**
     * 去掉order
     * @param sql
     * @return
     */
    protected String removeOrders(String sql) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(sql);
        StringBuilder sb = new StringBuilder();
        while (m.find()){
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 去掉select
     * @param sql
     * @return
     */
    protected String removeSelect(String sql){
        if(sql.toLowerCase().matches("form\\s+")){
            int beginPos = sql.toLowerCase().indexOf("from");
            return sql.substring(beginPos);
        }else{
            return sql;
        }
    }

    /**
     * 处理like
     * @param rule
     */
    private void processLike(QueryRule.Rule rule){
        if (ArrayUtils.isEmpty(rule.getValues())){
            return;
        }
        Object obj = rule.getValues()[0];

        if( obj != null){
            String value = obj.toString();
            if (!StringUtils.isEmpty(value)){
                value = value.replace('*','%');
                obj = value;
            }
        }
        add(rule.getAndOr(), rule.getPropertyName(), "like", "%" + rule.getValues()[0] + "%");
    }

    /**
     * 处理between
     * @param rule
     */
    private void processBetween(QueryRule.Rule rule){
        if ( (ArrayUtils.isEmpty(rule.getValues()))
             || (rule.getValues().length < 2)
            ){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "between","", rule.getValues()[0] , "and");
        add(0 ,"", "","", rule.getValues()[1] , "");
    }

    /**
     * 处理 =
     * @param rule
     */
    private void processEqual(QueryRule.Rule rule){
        if (ArrayUtils.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "=",rule.getValues()[0]);
    }

    /**
     * 处理 <>
     * @param rule
     */
    private void processNotEqual(QueryRule.Rule rule){
        if (ArrayUtils.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "<>",rule.getValues()[0]);
    }

    /**
     * 处理 >
     * @param rule
     */
    private void processGreaterThen(QueryRule.Rule rule){
        if (ArrayUtils.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), ">",rule.getValues()[0]);
    }

    /**
     * 处理 >=
     * @param rule
     */
    private void processGreaterEqual(QueryRule.Rule rule){
        if (ArrayUtils.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), ">=",rule.getValues()[0]);
    }

    /**
     * 处理 <
     * @param rule
     */
    private void processLessThen(QueryRule.Rule rule){
        if (ArrayUtils.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "<",rule.getValues()[0]);
    }

    /**
     * 处理 <=
     * @param rule
     */
    private void processLessEqual(QueryRule.Rule rule){
        if (ArrayUtils.isEmpty(rule.getValues())){
            return;
        }
        add(rule.getAndOr(), rule.getPropertyName(), "<=",rule.getValues()[0]);
    }

    /**
     * 处理 is null
     * @param rule
     */
    private void processIsNull(QueryRule.Rule rule){
        add(rule.getAndOr(), rule.getPropertyName(), "is null", "");
    }

    /**
     * 处理 is not null
     * @param rule
     */
    private void processIsNotNull(QueryRule.Rule rule){
        add(rule.getAndOr(), rule.getPropertyName(), "is not null", "");
    }

    /**
     * 处理 <>''
     * @param rule
     */
    private void processIsNotEmpty(QueryRule.Rule rule){
        add(rule.getAndOr(), rule.getPropertyName(), "<>", "");
    }


    /**
     * 处理 in 和 not in
     * @param rule
     */
    private void inAndNotIn(QueryRule.Rule rule, String name){
        if (ArrayUtils.isEmpty(rule.getValues())){
            return;
        }
        if((rule.getValues().length == 1) && (rule.getValues()[0] != null)
            && (rule.getValues()[0] instanceof List)
        ){
            List<Object> list = (List) rule.getValues()[0];

            if((list != null) && (list.size() > 0)){
                for(int i = 0; i < list.size();i++){
                    if(i==0 && i ==list.size() -1){
                        add(rule.getAndOr(),rule.getPropertyName(),"",name + " (",list.get(i),"");
                    }
                    if(i > 0 && i < list.size() - 1){
                        add(0,"",",","",list.get(i),"");
                    }
                    if(i == list.size() - 1){
                        add(0,"",",","",list.get(i),")");
                    }
                }
            }
        } else{
            Object[] list = rule.getValues();
            for(int i=0; i< list.length; i++){
                if(i == 0 && i == list.length - 1){
                    add(rule.getAndOr(),rule.getPropertyName(),"",name + " (",list[i],")");
                }else if(i == 0 && i < list.length - 1){
                    add(0,"",",","",list[i],"");
                }
                if(i > 0 && i < list.length - 1){
                    add(0,"",",","",list[i],"");
                }
                if(i == list.length - 1){
                    add(0,"",",","",list[i],")");
                }

            }
        }

    }

    /**
     * 处理 not in
     * @param rule
     */
    private void processNotIn(QueryRule.Rule rule){
        inAndNotIn(rule,"not in");
    }

    /**
     * 处理 in
     * @param rule
     */
    private void processIn(QueryRule.Rule rule){
        inAndNotIn(rule,"in");
    }


    //todo

    /**
     * 加入SQL查询规则队列
     * @param andOr and 或者 or
     * @param key 列名
     * @param split 列名与值之间的间隔
     * @param value value值
     */
    private void add(int andOr, String key, String split, Object value){
        add(andOr, key, split, "",value, "");
    }

    /**
     * 加入SQL查询规则队列
     * @param andOr  and 或者 or ，如为0 则为空
     * @param key 列名
     * @param split 列名与值之间的间隔
     * @param prefix 值前缀
     * @param value value值
     * @param suffix 值后缀
     */
    private void add(int andOr, String key, String split, String prefix, Object value, String suffix){
        String andOrStr = (0 == andOr ? "" : (QueryRule.AND == andOr ? " and " : "or"));
        properties.add(CURR_INDEX, andOrStr + key + " " + split + prefix + (null != value ? " ? " : " ") + suffix);

        if(null != value){
            values.add(CURR_INDEX,value);
            CURR_INDEX ++;
        }
    }

}
