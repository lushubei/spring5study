package com.xb.vip.spring.mvcframework.orm.v3;

/**
 * Sql排序组件
 */
public class Order {
    private boolean ascending; //升序还是降序
    private String propertyName; //哪个字段升序，哪个字段降序

    public String toString(){
        return propertyName + ' ' + (ascending ? "asc" : "desc");
    }

    public Order(String propertyName, boolean ascending) {
        this.ascending = ascending;
        this.propertyName = propertyName;
    }

    /**
     * 升序排列
     * @param propertyName
     * @return
     */
    public static Order asc(String propertyName){
        return new Order(propertyName, true);
    }

    /**
     * 降序排列
     * @param propertyName
     * @return
     */
    public static Order desc(String propertyName){
        return new Order(propertyName,false);
    }
}
