package com.lucky.jacklamb.querybuilder;

import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import com.sun.javafx.binding.StringFormatter;

import java.lang.reflect.Field;
import java.util.*;

/**
 *
 * 翻译器，将对象化的查询语句转化为SQL语句
 * @author fk7075
 * @version 1.0.0
 * @date 2020/8/16 10:51 上午
 */
public class Translator {

    private String dbname;

    public void setDbname(String dbname) {
        this.dbname = dbname;
    }

    private String SELECT="SELECT * FROM @:table ";

    private String UPDATE="UPDATE @:table SET @:set";

    private Class<?> pojoClass;

    private boolean isNoPack=true;

    private Class<?> packClass;

    public String getSELECT() {
        return SELECT;
    }

    public String getUPDATE() {
        return UPDATE;
    }

    private StringBuilder sql;

    private List<Object> params;

    public StringBuilder getSql() {
        return sql;
    }

    public List<Object> getParams() {
        return params;
    }

    public Translator(){
        sql=new StringBuilder(" WHERE ");
        params=new ArrayList<>();
        dbname="defaultDB";
    }

    public Translator(String dbname){
        sql=new StringBuilder(" WHERE ");
        params=new ArrayList<>();
        this.dbname=dbname;
    }

    public Translator(Class<?> pojoClass){
        sql=new StringBuilder(" WHERE ");
        setPojoClass(pojoClass);
        params=new ArrayList<>();
    }

    public Class<?> getPackClass() {
        return packClass;
    }

    public Class<?> getPojoClass() {
        return pojoClass;
    }

    public Translator setPackClass(Class<?> packClass) {
        this.packClass = packClass;
        isNoPack=false;
        return this;
    }

    public Translator setPojoClass(Class<?> pojoClass){
        this.pojoClass=pojoClass;
        if(isNoPack){
            this.packClass=pojoClass;
        }
        SELECT=SELECT.replaceAll("@:table","`"+ PojoManage.getTable(pojoClass,dbname)+"`");
        UPDATE=UPDATE.replaceAll("@:table","`"+PojoManage.getTable(pojoClass,dbname)+"`");
        return this;
    }

    public Translator setSqlSelect(String columns){
        SELECT=SELECT.replaceAll("\\*",columns);
        return this;
    }

    public Translator setSqlUpdate(String columns,Object...params){
        UPDATE=UPDATE.replaceAll("@:set",columns);
        if(this.params.isEmpty()){
            this.params.addAll(Arrays.asList(params));
        }else{
            for(int i=params.length-1;i>=0;i--){
                this.params.add(0,params[i]);
            }
        }
        return this;
    }

    public Translator add(){
        sql.append(" AND ");
        return this;
    }

    public Translator add(String sqlAnd,Object...params){
        sql.append(" AND ").append(sqlAnd).append(" ");
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public Translator or(){
        sql.append(" OR ");
        return this;
    }

    public Translator or(String sqlOr,Object...params){
        sql.append(" OR ").append(sqlOr).append(" ");
        this.params.addAll(Arrays.asList(params));
        return this;
    }


    public Translator orS(){
        sql.append(" OR (");
        return this;
    }

    public Translator andS(){
        sql.append(" AND (");
        return this;
    }

    public Translator end(){
        sql.append(")");
        return this;
    }

    public Translator eq(String columns,Object param){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" =?");
        params.add(param);
        return this;
    }

    private Field[] allField;
    public Translator allEq(Object pojo){
        if(allField==null){
            allField= ClassUtils.getAllFields(pojo.getClass());
        }
        for (Field field : allField) {
            Object value = FieldUtils.getValue(pojo, field);
            if(value!=null) {
                eq(PojoManage.getTableField(dbname,field),value);
            }
        }
        return this;
    }

    public Translator allEq(Map<String,Object> map){
        for(Map.Entry<String,Object> en:map.entrySet()){
            eq(en.getKey(),en.getValue());
        }
        return this;
    }

    /** 不等于*/
    public Translator ne(String columns,Object param){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append( "<>? ");
        params.add(param);
        return this;
    }

    /** 大于 >*/
    public Translator gt(String columns,Object param){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" >? ");
        params.add(param);
        return this;
    }

    /** 大于等于 >=*/
    public Translator ge(String columns,Object param){
        sql.append(" ").append("`").append(columns).append("`").append(" >=? ");
        params.add(param);
        return this;
    }

    /** 小于 <*/
    public Translator lt(String columns,Object param){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" <? ");
        params.add(param);
        return this;
    }

    /** 小于等于 <=*/
    public Translator le(String columns,Object param){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" <=?");
        params.add(param);
        return this;
    }

    public Translator like(String columns,Object param){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" LIKE ?");
        params.add(param);
        return this;
    }

    public Translator likeE(String columns,Object param){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" LIKE ?");
        params.add("%"+param);
        return this;
    }

    public Translator likeS(String columns,Object param){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" LIKE ?");
        params.add(param+"%");
        return this;
    }

    public Translator likeC(String columns,Object param){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" LIKE ?");
        params.add("%"+param+"%");
        return this;
    }

    public Translator notLike(String columns,Object param){
        sql.append("`").append(columns).append("`").append("NOT LIKE ?");
        params.add(param);
        return this;
    }

    public Translator in(String columns, Collection<?> collection){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" IN ?C");
        params.add(collection);
        return this;
    }

    public Translator in(String column,String inSQL, Object[] params){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(column).append("`").append(String.format(" IN (%s)",inSQL).toString());
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public Translator notIn(String columns, Collection<?>collection){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" NOT IN ?C");
        params.add(collection);
        return this;
    }

    public Translator ontIn(String column,String inSQL, Object[] params){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(column).append("`").append(StringFormatter.format(" NOT IN (%s)",inSQL));
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public Translator isNull(String columns){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" IS NULL");
        return this;
    }

    public Translator isNotNull(String columns){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(columns).append("`").append(" IS NOT NULL");
        return this;
    }

    public Translator groupBy(String columns){
        sql.append(" GROUP BY ").append("`").append(columns).append("`").append(" ");
        return this;
    }

    public Translator where(){
        if(sql.toString().toUpperCase().trim().endsWith("WHERE")) {
            return this;
        }
        return where("");
    }

    public Translator where(String whereSQl,Object...params){

        sql.append(" WHERE ").append(whereSQl).append(" ");
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public Translator having(String havingSQl,Object...params){
        sql.append(" HAVING ").append(havingSQl).append(" ");
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public Translator orderAsc(String columns){
        sql.append(" ORDER BY ").append(columns).append(" ASC ");
        return this;
    }

    public Translator orderDesc(String columns){
        sql.append(" ORDER BY ").append(columns).append(" DESC ");
        return this;
    }

    public Translator exists(String existsSql,Object...params){
        sql.append(String.format(" EXISTS (%s)",existsSql).toString());
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public Translator notExists(String existsSql,Object...params){
        sql.append(StringFormatter.format(" NOT EXISTS (%s)",existsSql).toString());
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    public Translator between(String column,Object val1,Object val2){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(column).append("`").append(" BETWEEN ? AND ?");
        params.add(val1);
        params.add(val2);
        return this;
    }

    public Translator notBetween(String column,Object val1,Object val2){
        if(isEndBrackets()) {
            add();
        }
        sql.append("`").append(column).append("`").append(" NOT BETWEEN ? AND ?");
        params.add(val1);
        params.add(val2);
        return this;
    }

    public Translator setSql(String sql,Object...params){
        this.sql.append(" ").append(sql);
        this.params.addAll(Arrays.asList(params));
        return this;
    }

    private boolean isEndBrackets(){
        String trim = sql.toString().trim().toUpperCase();
        return !trim.endsWith("AND")&&!trim.endsWith("OR")&&!trim.endsWith("(")&&!trim.endsWith("WHERE")&&!trim.endsWith("HAVING");
    }

}
