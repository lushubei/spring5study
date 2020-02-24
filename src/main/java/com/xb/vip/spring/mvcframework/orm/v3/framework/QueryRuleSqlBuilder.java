package com.xb.vip.spring.mvcframework.orm.v3.framework;

import com.xb.vip.spring.mvcframework.orm.v3.QueryRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//                case
            }
        }
    }
}
