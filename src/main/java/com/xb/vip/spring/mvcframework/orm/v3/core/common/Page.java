package com.xb.vip.spring.mvcframework.orm.v3.core.common;

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象，包含当前页面数据及分页信息，入总记录数
 * 能够支持和JQuery EasyUI直接对接，能够支持和BootStrap Table直接对接
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private long pageSize = DEFAULT_PAGE_SIZE; //每页的记录数
    private long start; //当前页第一条数据在List中的位置，从0开始

    private List<T> rows; //当前页中存放的记录，类型一般为List

    private long total; //总记录数

    public Page() {
    }

    /**
     * 默认构造方法
     * @param start 本页数据在数据库中的起始位置
     * @param total 数据库中总记录条数
     * @param pageSize 本页容量
     * @param rows 本页包含的数据
     */
    public Page(long start, long total, long pageSize, List<T> rows) {
        this.pageSize = pageSize;
        this.start = start;
        this.rows = rows;
        this.total = total;
    }

    /**
     * 取本页容量
     * @return
     */
    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }


    /**
     * 获取当前页中的记录
     * @return
     */
    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    /**
     * 获取记录数
     * @return
     */
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * 获取总页数
     * @return
     */
    public long getTotalPageCount(){
        if(total % pageSize == 0){
            return total / pageSize;
        }else{
            return total / pageSize + 1;
        }
    }

    /**
     * 获取该页的当前页码，页码从1开始
     * @return
     */
    public long getPageNo(){
        return start / pageSize + 1;
    }

    /**
     * 该页是否有下一页
     * @return
     */
    public boolean hasNextPage(){
        return this.getPageNo() < this.getTotalPageCount() - 1;
    }

    /**
     * 该页是否有上一页
     * @return
     */
    public boolean hasPreviousPage(){
        return this.getPageNo() > 1;
    }

    /**
     * 获取任意页第一条数据在数据集中的位置，也没条数使用默认值
     * @param pageNo
     * @return
     */
    protected static int getStartOfPage(int pageNo) {
        return getStartOfPage(pageNo, DEFAULT_PAGE_SIZE);
    }

    /**
     * 获取任意页第一条数据在数据集中的位置
     * @param pageNo 从1开始的页码
     * @param pageSize 每页记录条数
     * @return 该页第一条数据
     */
    private static int getStartOfPage(int pageNo, int pageSize) {
        return (pageNo - 1) * pageSize;
    }


}
