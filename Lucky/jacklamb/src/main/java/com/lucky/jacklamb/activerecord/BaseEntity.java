package com.lucky.jacklamb.activerecord;

import com.lucky.jacklamb.annotation.table.NoColumn;
import com.lucky.jacklamb.annotation.table.NoPackage;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCore;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCoreFactory;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.jacklamb.querybuilder.Page;
import com.lucky.jacklamb.querybuilder.QueryBuilder;
import com.lucky.jacklamb.querybuilder.Translator;
import com.lucky.utils.conversion.annotation.NoConversion;
import com.lucky.utils.reflect.FieldUtils;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;


/**
 * ActiveRecord操作模式的基本实体类
 * @author fk7075
 * @date 2020/8/13 9:35
 * @version 1.0
 */
public abstract class BaseEntity<Entity> {

    @NoConversion
    @NoPackage
    @NoColumn
    private transient SqlCore sqlCore;

    @NoConversion
    @NoPackage@NoColumn
    private transient final Field idField= PojoManage.getIdField(this.getClass());

    public BaseEntity(){
        sqlCore= SqlCoreFactory.createSqlCore();
    }

    public void setSqlCore(SqlCore sqlCore){
        this.sqlCore=sqlCore;
    }

    /**
     * 设置本次查询为全映射模式的查询
     * @param isFullMap 是否开启全映射模式
     */
    public void setFullMap(boolean isFullMap){
        sqlCore.setFullMap(isFullMap);
    }

    /**
     * 设置数据源内核
     * @param dbName
     */
    public void setDbName(String dbName){
        sqlCore=SqlCoreFactory.createSqlCore(dbName);
    }

    /**
     * 创建实体对应的数据库表
     */
    public void createTable(){
        sqlCore.createTable(this.getClass());
    }

    /**
     * 添加一条记录
     * @param entity 实体
     * @return
     */
    public int insert(Entity entity){
        return sqlCore.insert(entity);
    }

    /**
     * 将当前实体添加到数据库
     * @return
     */
    public int insert(){
        return sqlCore.insert(this);
    }

    /**
     * 批量添加
     * @param entities
     * @return
     */
    public int insert(Collection<Entity> entities){
        return sqlCore.insertByCollection(entities);
    }

    /**
     * 添加并返回ID
     * @param entity
     * @return
     */
    public int insertSetId(Entity entity){
        return sqlCore.insertSetId(entity);
    }

    /**
     * 添加并返回ID
     * @return
     */
    public int insertSetId(){
        return sqlCore.insertSetId(this);
    }

    /**
     * ID删除
     * @param id 字段ID
     * @return
     */
    public int deleteById(Object id){
        return sqlCore.delete(this.getClass(),id);
    }

    /**
     * 使用当前ID的ID删除
     * @return
     */
    public int deleteById(){
        return sqlCore.delete(this.getClass(), FieldUtils.getValue(this,idField));
    }

    /**
     * 实体删除
     * @param entity 要删除的实体
     * @return
     */
    public int delete(Entity entity){
        return sqlCore.delete(entity);
    }

    /**
     * 使用当前实体的实体删除
     * @return
     */
    public int delete(){
        return sqlCore.delete(this);
    }

    /**
     * 批量删除
     * @param entities
     * @return
     */
    public int delete(Collection<Entity> entities){
        return sqlCore.deleteByCollection(entities);
    }

    /**
     * 根据ID的批量删除
     * @param ids 要删除数据的ID集合
     * @return
     */
    public int batchDeleteById(Object ...ids){
        return sqlCore.deleteByIdIn(this.getClass(),ids);
    }

    /**
     * 实体更新
     * @param entity 实体
     * @return
     */
    public int update(Entity entity){
        return sqlCore.update(entity);
    }

    /**
     * 使用当前实体的实体更新
     * @return
     */
    public int update(){
        return sqlCore.update(this);
    }

    /**
     * 实体更新，指定更新条件
     * @param entity 实体
     * @param conditions WHERE条件字段名
     * @return
     */
    public int updateByColumn(Entity entity,String...conditions){
        return sqlCore.update(entity,conditions);
    }


    /**
     * 使用当前实体的实体更新，指定更新条件
     * @param conditions WHERE条件字段名
     * @return
     */
    public int updateByColumn(String...conditions){
        return sqlCore.update(this,conditions);
    }


    /**
     * ID查询
     * @param id 字段ID
     * @return
     */
    public Entity selectById(Object id){
        return (Entity) sqlCore.getOne(this.getClass(),id);
    }

    /**
     * 使用当前ID的ID查询
     * @return
     */
    public Entity selectById(){
        return (Entity) sqlCore.getOne(this.getClass(), FieldUtils.getValue(this,idField));
    }

    /**
     * 实体查询,返回单一对象
     * @param entity 要查询的实体
     * @return
     */
    public Entity selectOne(Entity entity){
        return sqlCore.getObject(entity);
    }

    /**
     * 使用当前实体的实体查询,返回单一对象
     * @return
     */
    public Entity selectOne(){
        return (Entity) sqlCore.getObject(this);
    }

    /**
     * 实体查询,返回List集合
     * @param entity 要查询的实体
     * @return
     */
    public List<Entity> select(Entity entity){
        return sqlCore.getList(entity);
    }

    /**
     * 使用当前实体的实体查询,返回List集合
     * @return
     */
    public List<Entity> select(){
        return (List<Entity>) sqlCore.getList(this);
    }

    /**
     * 执行sql脚本(逐行执行)
     * @param sqlScriptReader sql脚本
     */
    public void runScript(Reader sqlScriptReader) throws IOException {
        sqlCore.runScript(sqlScriptReader);
    }

    /**
     *  执行sql脚本(全行执行)
     * @param sqlScript sql脚本
     */
    public void runScriptFullLine(Reader sqlScript) throws IOException {
        sqlCore.runScriptFullLine(sqlScript);
    }

    /**
     * 全表内容查询
     * @return
     */
    public List<Entity> selectAll(){
        return (List<Entity>) sqlCore.getList(this.getClass());
    }

    /**
     * 分页查询
     * @param entity 实体
     * @param page 页码
     * @param rows 每页记录数
     * @return
     */
    public Page<Entity> limit(Entity entity, int page, int rows){
        return sqlCore.getPageList(entity,page,rows);
    }

    /**
     * 使用当前实体的分页查询
     * @param page 页码
     * @param rows 每页记录数
     * @return
     */
    public Page<Entity> limit(int page,int rows){
        return (Page<Entity>) sqlCore.getPageList(this,page,rows);
    }

    /**
     * 查询实体Count
     * @param entity 实体
     * @return
     */
    public int selectCount(Entity entity){
        return sqlCore.count(entity);
    }

    /**
     * 使用当前实体的查询实体Count
     * @return
     */
    public int selectCount(){
        return sqlCore.count(this);
    }

    /**
     * 查询全表的Count
     * @return
     */
    public int count(){
        return sqlCore.count(this.getClass());
    }

    /**
     * QueryBuilder查询
     * @param queryBuilder QueryBuilder对象
     * @return
     */
    public List<Entity> query(QueryBuilder queryBuilder){
        return (List<Entity>) sqlCore.query(queryBuilder,this.getClass());
    }


    /**
     * 预编译SQL查询
     * @param sql 预编译SQL
     * @param params SQl参数
     * @return
     */
    public List<Entity> query(String sql,Object...params){
        return (List<Entity>) sqlCore.getList(this.getClass(),sql,params);
    }

    /**
     * 预编译SQL的非查询操作
     * @param sql 预编译SQL
     * @param params SQl参数
     * @return
     */
    public int updateBySql(String sql,Object...params){
        return sqlCore.updateBySql(sql,params);
    }

    /**
     * Translator方式的更新，不用设置SET语句，修改以当前对象为模板
     * @param tr
     * @return
     */
    public int updateThis(Translator tr){
        return sqlCore.update(this,tr);
    }

    /**
     * Translator方式更新，需要使用Translator对象的setSqlUpdate方法设置SET规则
     * @param tr Translator对象
     * @return
     */
    public int update(Translator tr){
        tr.setPojoClass(this.getClass());
        return sqlCore.update(tr);
    }

    /**
     * Translator方式更新,以出入的对象为模板
     * @param pojo 模板对象
     * @param tr Translator对象
     * @return
     */
    public int update(Entity pojo,Translator tr){
        return sqlCore.update(pojo,tr);
    }

    /**
     * Translator方式删除
     * @param tr Translator对象
     * @return
     */
    public int delete(Translator tr){
        return sqlCore.delete(this.getClass(),tr);
    }

    /**
     * <p>
     * Translator方式查询，可以通过Translator对象的setPackClass方法来指定接受本次查询结果的类型
     * 如果不指定，则使用当前实体类型接受
     * </p>
     * @param tr Translator对象
     * @return
     */
    public List<Entity> select(Translator tr){
        tr.setPojoClass(this.getClass());
        return (List<Entity>) sqlCore.getList(tr);
    }

    /**
     * <p>
     * Translator方式查询单个对象(针对返回结果只有一条记录的情况)，可以通过Translator对象的setPackClass方法来指定接受本次查询结果的类型
     * 如果不指定，则使用当前实体类型接受
     * </p>
     * @param tr Translator对象
     * @return
     */
    public Entity selectOne(Translator tr){
        tr.setPojoClass(this.getClass());
        return (Entity) sqlCore.getObject(tr);
    }
}
