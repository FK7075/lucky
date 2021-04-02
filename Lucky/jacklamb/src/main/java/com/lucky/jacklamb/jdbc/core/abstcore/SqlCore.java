package com.lucky.jacklamb.jdbc.core.abstcore;

import com.lucky.datasource.sql.LuckyDataSource;
import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.jacklamb.createtable.CreateTableSqlGenerate;
import com.lucky.jacklamb.enums.PrimaryType;
import com.lucky.jacklamb.exception.CreateMapperException;
import com.lucky.jacklamb.jdbc.core.fixedcoreImpl.GeneralObjectCoreBase;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.jacklamb.jdbc.transaction.Transaction;
import com.lucky.jacklamb.mapper.LuckyMapperProxy;
import com.lucky.jacklamb.querybuilder.Page;
import com.lucky.jacklamb.querybuilder.QFilter;
import com.lucky.jacklamb.querybuilder.QueryBuilder;
import com.lucky.jacklamb.querybuilder.Translator;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 对所有关系型数据库操作的抽象，本抽象类聚合对StatementCore接口和GeneralObjectCore接口的所有实现，
 * 对UniqueSqlCore接口的方法留给其子类去实现
 * @author fk-7075
 *
 */
public abstract class SqlCore extends GeneralObjectCoreBase implements Closeable {

	public Transaction openTransaction(){
		return super.openTransaction();
	}

	public Transaction openTransaction(int isolationLevel){
		return super.openTransaction(isolationLevel);
	}

	private LuckyMapperProxy mapperProxy;

	public void setFullMap(boolean fullMap) {
        statementCore.setFullMap(fullMap);
	}

	public SqlCore(String dbname) {
		super(dbname);
		mapperProxy = new LuckyMapperProxy(this);
	}

	@Override
	public void createTable() {
		createTableSqlExecute.executeCreateTableSql(getCreateTableSqlGenerate());
	}

	@Override
	public void createTable(Class<?>...tableClasses) {
		createTableSqlExecute.executeCreateTableSql(getCreateTableSqlGenerate(),tableClasses);
	}

	/**
	 * 获取建表Sql生成器
	 * @return 生成建表语句的工具
	 */
	public abstract CreateTableSqlGenerate getCreateTableSqlGenerate();

	/**
	 * ID查询
	 * @param pojoClass
	 * 包装类的Class
	 * @param id
	 * @return
	 */
	public <T> T getOne(Class<T> pojoClass, Object id) {
		return super.getOne(pojoClass, id);
	}
	
	/**
	 * 对象方式获得单个对象
	 * @param pojo
	 * @return
	 */
	public <T> T getObject(T pojo) {
		return super.getObject(pojo);
	}
	
	/**
	 * 对象查询
	 * @param pojo
	 * 对象
	 * @return
	 */
	public <T> List<T> getList(T pojo){
		return super.getList(pojo);
	}
	
	/**
	 * 查询class对应表中得所有数据
	 * @param aClass
	 * @return
	 */
	public <T> List<T> getList(Class<T> aClass){
		StringBuilder sql=new StringBuilder("SELECT ");
		sql.append(new QFilter(aClass,dbname).lines()).append(" FROM ").append("`").append(PojoManage.getTable(aClass,dbname)).append("`");
		return getList(aClass,sql.toString());
	}
	
	/**
	 * 条件数据统计
	 * @param pojo
	 * @return
	 */
	public <T> int count(T pojo) {
		return super.count(pojo);
	}
	
	/**
	 * 总数统计
	 * @param clzz
	 * @return
	 */
	public <T> int count(Class<T> clzz) {
		StringBuilder countSql=new StringBuilder("SELECT COUNT(")
				.append("`").append(PojoManage.getIdString(clzz,dbname)).append("`")
				.append(") FROM ").append("`").append(PojoManage.getTable(clzz,dbname)).append("`");
		return getObject(int.class,countSql.toString());
	}
	
	
	/**
	 * 删除数据
	 * @param pojo
	 * 包含删除信息的包装类的对象
	 * @return
	 */
	public <T> int delete(T pojo) {
		return super.delete(pojo);
	}
	
	/**
	 * 跟新操作
	 * @param pojo 实体类对象
	 * @param conditions 作为更新条件的字段(支持多值，缺省默认使用Id字段作为更新条件)
	 * @return
	 */
	public <T> int update(T pojo,String...conditions) {
		return super.updateRow(pojo,conditions);
	}
	
	/**
	 * 批量删除-数组模式
	 * @param pojos
	 * 包含删除信息的对象数组
	 * @return
	 */
	public int deleteByArray(Object...pojos) {
		return super.deleteByArray(pojos);
	}
	
	
	/**
	 * 批量更新-数组模式
	 * @param pojos
	 * 包含更新信息的对象数组
	 * @return
	 */
	public int updateByArray(Object...pojos) {
		return super.updateByArray(pojos);
	}
	
	/**
	 * 批量删除-集合模式
	 * @param pojoCollection 要操作的对象所组成的集合
	 * @return false or true
	 */
	public <T> int deleteByCollection(Collection<T> pojoCollection) {
		return super.deleteByCollection(pojoCollection);
	}
	
	
	/**
	 * 批量更新-集合模式
	 * @param pojoCollection 要操作的对象所组成的集合
	 * @return false or true
	 */
	public <T> int updateByCollection(Collection<T> pojoCollection) {
		return super.updateByCollection(pojoCollection);
	}
	
	/**
	 * SQL查询
	 * @param pojoClass
	 * 包装类的Class
	 * @param sql
	 * 预编译的sql语句
	 * @param obj
	 * @return
	 */
	public <T> List<T> getList(Class<T> pojoClass, String sql, Object... obj){
		return statementCore.getList(pojoClass, sql, obj);
	}

	public <T> List<T> getListMethod(Class<T> pojoClass,Method method ,String sql, Object[] obj){
		return statementCore.getListMethod(pojoClass, method,sql, obj);
	}
	
	/**
	 * 预编译SQL方式获得单一对象
	 * @param pojoClass
	 * @param sql
	 * @param obj
	 * @return
	 */
	public <T> T getObject(Class<T> pojoClass,String sql,Object...obj) {
		return statementCore.getObject(pojoClass, sql, obj);
	}

	public <T> T getObjectMethod(Class<T> pojoClass,Method method,String sql,Object[] obj) {
		return statementCore.getObjectMethod(pojoClass,method, sql, obj);
	}
	
	/**
	 * 预编译SQL非查询操作
	 * @param sql
	 * @param obj
	 * @return
	 */
	public int updateBySql(String sql, Object...obj) {
		return statementCore.update(sql, obj);
	}

	public int updateMethod(String sql,Method method,Object[] obj) {
		return statementCore.updateMethod(method,sql, obj);
	}
	
	/**
	 * id删除
	 * @param pojoClass
	 * 所操作类
	 * @param id
	 * id值
	 * @return
	 */
	public int delete(Class<?> pojoClass,Object id) {
		return super.delete(pojoClass, id);
	}
	
	/**
	 * 批量ID删除
	 * @param pojoClass 要操作表对应类的Class
	 * @param ids 要删除的id所组成的集合
	 * @return
	 */
	public int deleteByIdIn(Class<?> pojoClass,Object[] ids) {
		return super.deleteByIdIn(pojoClass, ids);
	}

	/**
	 * 批量ID删除
	 * @param pojoClass 要操作表对应类的Class
	 * @param ids 要删除的id所组成的集合
	 * @return
	 */
	public int deleteByIdIn(Class<?> pojoClass,List<?> ids) {
		return deleteByIdIn(pojoClass, ids.toArray());
	}

	/**
	 * 批量ID查询
	 * @param clazz 要操作表对应类的Class
	 * @param ids 要删除的id所组成的集合
	 * @return
	 */
	@Override
	public <T> List<T> getByIdIn(Class<T> clazz, Object[] ids) {
		return super.getByIdIn(clazz, ids);
	}

	/**
	 * 批量ID查询
	 * @param clazz 要操作表对应类的Class
	 * @param ids 要删除的id所组成的集合
	 * @return
	 */
	public <T> List<T> getByIdIn(Class<T> clazz,List<?> ids) {
		return getByIdIn(clazz, ids.toArray());
	}

	/**
	 * 批量SQL非查询操作
	 * @param sql
	 * 模板预编译SQL语句
	 * @param obj
	 * 填充占位符的一组组对象数组组成的二维数组
	 * [[xxx],[xxx],[xxx]]
	 * @return
	 */
	public int[] updateBatch(String sql,Object[][] obj) {
		return statementCore.updateBatch(sql, obj);
	}

	/**
	 * 向数据库发送一组SQL语句
	 * @param completeSql 完整的SQL
	 * @return
	 */
	public int[] updateBatch(String...completeSql){
		return statementCore.updateBatch(completeSql);
	}
	
	/**
	 * 得到当前SqlCore对象对应的数据源的dbname
	 * @return
	 */
	public String getDbName() {
		return dbname;
	}
	
	/**
	 * 清空缓存
	 */
	public final void clear() {
		statementCore.clear();
		
	}

	/**
	 * Mapper接口式开发,返回该接口的代理对象
	 * @param clazz Mapper接口的Class
	 * @return Mapper接口的代理对象
	 */
	public <T> T getMapper(Class<T> clazz) {
		try {
			return mapperProxy.getMapperProxyObject(clazz);
		} catch (InstantiationException | IllegalAccessException | IOException e) {
			throw new CreateMapperException(clazz,e);
		}
	}

	@Override
	public <T> int insert(T t) {
		if(PojoManage.getIdType(t.getClass(),getDbName())== PrimaryType.AUTO_UUID)
			setNextUUID(t);
		return super.insert(t);
	}
	
	@Override
	public <T> int insertSetId(T t) {
		int result = insert(t);
		if(PojoManage.getIdType(t.getClass(),getDbName())== PrimaryType.AUTO_INT)
			setNextId(t);
		return result;
	}

	@Override
	public boolean insertByArray(Object... obj) {
		for(Object pojo:obj) {
			insert(pojo);
		}
		return true;
	}
	
	@Override
	public int insertSetIdByArray(Object... obj) {
		int[] result=new int[obj.length];
		for (int i = 0; i < obj.length; i++) {
			result[i]=insertSetId(obj[i]);
		}
		return getResult(result);
	}
	
	public void setNextUUID(Object pojo) {
		Field idField=PojoManage.getIdField(pojo.getClass());
		FieldUtils.setValue(pojo,idField,UUID.randomUUID().toString());
	}

	public int update(Translator tr){
		StringBuilder sql=new StringBuilder(tr.getUPDATE());
		if(tr.getSql().toString().toUpperCase().trim().startsWith("WHERE")){
			sql.append(tr.getSql());
		}else{
			sql.append(" WHERE ").append(tr.getSql());
		}
		return updateBySql(sql.toString(),tr.getParams().toArray());
	}

	@Override
	public <T> Page<T> getPageList(T t, int page, int size) {
		QueryBuilder queryBuilder=new QueryBuilder();
		queryBuilder.setDbname(getDbName());
		queryBuilder.limit(page,size);
		queryBuilder.setWheresql(getSqlGroup());
		queryBuilder.addObject(t);
		Page<T> returnPage=new Page<>();
		List<T> query = (List<T>) query(queryBuilder, t.getClass());
		int count = count(t.getClass());
		returnPage.setData(query);
		returnPage.setCurrPage(page);
		returnPage.setRows(size);
		int totalPage=count%size==0?count/size:count/size+1;
		returnPage.setTotalNum(count);
		returnPage.setTotalPage(totalPage);
		return returnPage;
	}

	public abstract SqlGroup getSqlGroup();

	public int update(Object pojo, Translator tr){
		StringBuilder sql=new StringBuilder("UPDATE ").append("`").append(PojoManage.getTable(pojo.getClass(),dbname)).append("`").append(" SET ");
		List<Object> params=new ArrayList<>();
		Field[] allFields = ClassUtils.getAllFields(pojo.getClass());
		for (Field field : allFields) {
			sql.append("`").append(PojoManage.getTableField(dbname,field)).append("`").append("=?,");
			params.add(FieldUtils.getValue(pojo,field));
		}
		sql.substring(0,sql.length()-1);
		if(tr.getSql().toString().toUpperCase().trim().startsWith("WHERE")){
			sql.append(tr.getSql());
		}else{
			sql.append(" WHERE ").append(tr.getSql());
		}
		if(sql.toString().toUpperCase().trim().endsWith("WHERE")){
			return updateBySql(sql.substring(0,sql.lastIndexOf("WHERE")),tr.getParams().toArray());
		}
		params.addAll(tr.getParams());
		return updateBySql(sql.toString(),params.toArray());
	}

	public int delete(Class<?> pojoClass,Translator tr){
		StringBuilder sql=new StringBuilder("DELETE FROM ").append("`").append(PojoManage.getTable(pojoClass,dbname)).append("`");
		if(tr.getSql().toString().toUpperCase().trim().startsWith("WHERE")){
			sql.append(tr.getSql());
		}else{
			sql.append(" WHERE ").append(tr.getSql());
		}
		if(sql.toString().toUpperCase().trim().endsWith("WHERE")){
			return updateBySql(sql.substring(0,sql.lastIndexOf("WHERE")),tr.getParams().toArray());
		}
		return updateBySql(sql.toString(),tr.getParams().toArray());
	}

	public List<?> getList(Translator tr){
		StringBuilder sql=new StringBuilder(tr.getSELECT());
		if(tr.getSql().toString().toUpperCase().trim().startsWith("WHERE")){
			sql.append(tr.getSql());
		}else{
			sql.append(" WHERE ").append(tr.getSql());
		}
		if(sql.toString().toUpperCase().trim().endsWith("WHERE")){
			return getList(tr.getPackClass(),sql.substring(0,sql.lastIndexOf("WHERE")),tr.getParams().toArray());
		}
		return getList(tr.getPackClass(),sql.toString(),tr.getParams().toArray());
	}

	public Object getObject(Translator tr){
		List<?> list = getList(tr);
		if(list!=null&&!list.isEmpty())
			return list.get(0);
		return null;
	}

	@Override
	public void close() throws IOException {
		LuckyDataSource dataSource = LuckyDataSourceManage.getDataSource(getDbName());
		if (dataSource != null){
			dataSource.destroy();
		}
	}
}
