package com.lucky.jacklamb.querybuilder;

import java.util.List;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/2 12:48 上午
 */
public class Page<T> {

    private List<T> data;
    private int rows;
    private int currPage;
    private int totalNum;
    private int totalPage;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(int totalNum) {
        this.totalNum = totalNum;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
