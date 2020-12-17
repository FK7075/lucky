package com.lucky.jacklamb.querybuilder;

import com.lucky.jacklamb.enums.Sort;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 查询条件对象
 * 注：queryBuilder对象的参数设置有一定的顺序，addObjects()方法必须优先执行
 * @author DELL
 *
 */
public class QueryBuilder {

    /**
     * 需要操作的对象
     */
    private List<Object> objects;

    /**
     * 设置查询返回列
     */
    private QFilter qFilter;

    /**
     * 排序信息
     */
    private List<SortSet> sortSets ;

    /**
     * 模糊查询信息
     */
    private String like="";

    private Integer page;

    private Integer rows;

    private SqlGroup sqlGroup;

    private String dbname;

    public String getDbname() {
        return dbname;
    }

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    public QueryBuilder(){
        dbname="defaultDB";
        objects=new ArrayList<>();
        sortSets=new ArrayList<>();
    }

    public QueryBuilder(String dbname){
        this.dbname=dbname;
        objects=new ArrayList<>();
        sortSets=new ArrayList<>();
    }

    public SqlGroup getWheresql() {
        return sqlGroup;
    }

    public void setWheresql(SqlGroup sqlGroup) {
        this.sqlGroup = sqlGroup;
        this.sqlGroup.setPage(page);
        this.sqlGroup.setRows(rows);
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public String getLike() {
        return this.like;
    }

    public void addLike(String...likeFile) {
        this.like=qFilter.like(likeFile);
    }

    /**
     * 返回查询对象的数组
     *
     * @return
     */
    public Object[] getObjectArray() {
        return objects.toArray();
    }

    /**
     * 添加需要操作实体类对象
     * @param obj
     * @return
     */
    public void addObject(Object...obj){
        objects.addAll(Arrays.asList(obj));
        qFilter=new QFilter(dbname,obj);
    }

    /**
     * 得到查询指定需要返回的列
     * @return
     */
    public String getResult() {
        return qFilter.lines();
    }

    /**
     * 设置查询指定返回列，不可与hiddenResult()方法同时使用
     * @param column
     * @return
     */
    public void addResult(String...column) {
        for(String col:column)
            qFilter.show(col);
    }

    /**
     * 设置查询指定隐藏的返回列,不可与addResult()方法同时使用
     * @param column
     * @return
     */
    public void hiddenResult(String...column) {
        for(String col:column)
            qFilter.hidden(col);
    }

    public String getSort() {
        return qFilter.sort(sortSets);
    }

    public QueryBuilder addSort(String field, Sort sortenum) {
        sortSets.add(new SortSet(field,sortenum));
        return this;
    }

    public void limit( int page, int rows) {
        this.page=page;
        this.rows=rows;
    }


}
