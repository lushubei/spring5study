package com.xb.vip.spring.mvcframework.orm.v3;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要功能用于构造查询条件
 */
public final class QueryRule implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final int ASC_ODER = 101;
    public static final int DESC_ODER = 102;
    public static final int LIKE = 1;
    public static final int IN = 2;
    public static final int NOTIN = 3;
    public static final int BETWEEN = 4;
    public static final int EQ = 5;
    public static final int NOTEQ = 6;
    public static final int GT = 7;
    public static final int GE = 8;
    public static final int LT = 9;
    public static final int LE = 10;
    public static final int ISNULL = 11;
    public static final int ISNOTNULL = 12;
    public static final int ISEMPTY = 13;
    public static final int ISNOTEMPTY = 14;
    public static final int AND = 201;
    public static final int OR = 202;

    private List<Rule> ruleList = new ArrayList<Rule>();
    private List<QueryRule> queryRuleList = new ArrayList<QueryRule>();
    private String propertyName;

    public QueryRule() {
    }

    public QueryRule(String propertyName) {
        this.propertyName = propertyName;
    }

    public static QueryRule getInstance(){
        return new QueryRule();
    }

    /**
     * 添加升序规则
     * @param propertyName
     * @return
     */
    public QueryRule addAscOrder(String propertyName) {
        this.ruleList.add(new Rule(ASC_ODER, propertyName));
        return this;
    }


    /**
     * 添加降序规则
     * @param propertyName
     * @return
     */
    public QueryRule addDescOrder(String propertyName) {
        this.ruleList.add(new Rule(DESC_ODER, propertyName));
        return this;
    }



    public QueryRule addIsNull(String propertyName) {
        this.ruleList.add(new Rule(ISNULL, propertyName).setAndOr(AND));
        return this;
    }

    public QueryRule addIsNotNull(String propertyName) {
        this.ruleList.add(new Rule(ISNOTNULL, propertyName).setAndOr(AND));
        return this;
    }


    public QueryRule addIsEmpty(String propertyName) {
        this.ruleList.add(new Rule(ISEMPTY, propertyName).setAndOr(AND));
        return this;
    }
    public QueryRule addIsNotEmpty(String propertyName) {
        this.ruleList.add(new Rule(ISNOTEMPTY, propertyName).setAndOr(AND));
        return this;
    }
    public QueryRule addLike(String propertyName, Object value) {
        this.ruleList.add(new Rule(LIKE, propertyName, new Object[]{value}).setAndOr(AND));
        return this;
    }
    public QueryRule addBetween(String propertyName, Object... values) {
        this.ruleList.add(new Rule(BETWEEN, propertyName, values).setAndOr(AND));
        return this;
    }

    public QueryRule addEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(EQ, propertyName,new Object[]{value}).setAndOr(AND));
        return this;
    }

    public QueryRule addNotEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(NOTEQ, propertyName,new Object[]{value}).setAndOr(AND));
        return this;
    }

    public QueryRule addIn(String propertyName, List<Object> values) {
        this.ruleList.add(new Rule(IN, propertyName,new Object[]{values}).setAndOr(AND));
        return this;
    }

    public QueryRule addIn(String propertyName, Object... values) {
        this.ruleList.add(new Rule(IN, propertyName, values).setAndOr(AND));
        return this;
    }

    public QueryRule addNotIn(String propertyName, List<Object> values) {
        this.ruleList.add(new Rule(NOTIN, propertyName,new Object[]{values}).setAndOr(AND));
        return this;
    }

    public QueryRule addNotIn(String propertyName, Object... values) {
        this.ruleList.add(new Rule(NOTIN, propertyName, values).setAndOr(AND));
        return this;
    }

    public QueryRule addGreaterThan(String propertyName, Object value) {
        this.ruleList.add(new Rule(GT, propertyName,new Object[]{value}).setAndOr(AND));
        return this;
    }
    public QueryRule addGreaterEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(GE, propertyName,new Object[]{value}).setAndOr(AND));
        return this;
    }

    public QueryRule addLessThan(String propertyName, Object value) {
        this.ruleList.add(new Rule(LT, propertyName,new Object[]{value}).setAndOr(AND));
        return this;
    }
    public QueryRule addLessEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(LE, propertyName,new Object[]{value}).setAndOr(AND));
        return this;
    }




    public QueryRule orIsNull(String propertyName) {
        this.ruleList.add(new Rule(ISNULL, propertyName).setAndOr(OR));
        return this;
    }

    public QueryRule orIsNotNull(String propertyName) {
        this.ruleList.add(new Rule(ISNOTNULL, propertyName).setAndOr(OR));
        return this;
    }


    public QueryRule orIsEmpty(String propertyName) {
        this.ruleList.add(new Rule(ISEMPTY, propertyName).setAndOr(OR));
        return this;
    }
    public QueryRule orIsNotEmpty(String propertyName) {
        this.ruleList.add(new Rule(ISNOTEMPTY, propertyName).setAndOr(OR));
        return this;
    }
    public QueryRule orLike(String propertyName, Object value) {
        this.ruleList.add(new Rule(LIKE, propertyName, new Object[]{value}).setAndOr(OR));
        return this;
    }
    public QueryRule orBetween(String propertyName, Object... values) {
        this.ruleList.add(new Rule(BETWEEN, propertyName, values).setAndOr(OR));
        return this;
    }

    public QueryRule orEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(EQ, propertyName,new Object[]{value}).setAndOr(OR));
        return this;
    }

    public QueryRule orNotEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(NOTEQ, propertyName,new Object[]{value}).setAndOr(OR));
        return this;
    }

    public QueryRule orIn(String propertyName, List<Object> values) {
        this.ruleList.add(new Rule(IN, propertyName,new Object[]{values}).setAndOr(OR));
        return this;
    }

    public QueryRule orIn(String propertyName, Object... values) {
        this.ruleList.add(new Rule(IN, propertyName, values).setAndOr(OR));
        return this;
    }

    public QueryRule orNotIn(String propertyName, List<Object> values) {
        this.ruleList.add(new Rule(NOTIN, propertyName,new Object[]{values}).setAndOr(OR));
        return this;
    }

    public QueryRule orNotIn(String propertyName, Object... values) {
        this.ruleList.add(new Rule(NOTIN, propertyName, values).setAndOr(OR));
        return this;
    }

    public QueryRule orGreaterThan(String propertyName, Object value) {
        this.ruleList.add(new Rule(GT, propertyName,new Object[]{value}).setAndOr(OR));
        return this;
    }
    public QueryRule orGreaterEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(GE, propertyName,new Object[]{value}).setAndOr(OR));
        return this;
    }

    public QueryRule orLessThan(String propertyName, Object value) {
        this.ruleList.add(new Rule(LT, propertyName,new Object[]{value}).setAndOr(OR));
        return this;
    }

    public QueryRule orLessEqual(String propertyName, Object value) {
        this.ruleList.add(new Rule(LE, propertyName,new Object[]{value}).setAndOr(OR));
        return this;
    }

    public List<Rule> getRuleList(){
        return ruleList;
    }

    public List<QueryRule> getQueryRuleList() {
        return queryRuleList;
    }

    public String getPropertyName(){
        return this.propertyName;
    }

    public class Rule implements Serializable{
        private static final long serialVersionUID = 1L;
        private int type; //规则的类型
        private String propertyName;
        private Object[] values;
        private int andOr = AND;

        public Rule(int paramInt, String paramString) {
            this.type = paramInt;
            this.propertyName = paramString;
        }

        public Rule(int paramInt, String paramString, Object[] paramArrayOfObject) {
            this.type = paramInt;
            this.propertyName = paramString;
            this.values = paramArrayOfObject;
        }

        public int getType() {
            return type;
        }


        public String getPropertyName() {
            return propertyName;
        }


        public Object[] getValues() {
            return values;
        }

        public void setValues(Object[] values) {
            this.values = values;
        }

        public int getAndOr() {
            return andOr;
        }

        public Rule setAndOr(int andOr) {
            this.andOr = andOr;
            return this;
        }
    }
}