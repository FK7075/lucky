package com.lucky.jacklamb.jdbc.potable;

import com.lucky.datasource.sql.LuckyDataSourceManage;
import com.lucky.jacklamb.annotation.jpa.ManyToMany;
import com.lucky.jacklamb.annotation.jpa.ManyToOne;
import com.lucky.jacklamb.annotation.jpa.OneToMany;
import com.lucky.jacklamb.annotation.jpa.OneToOne;
import com.lucky.jacklamb.annotation.table.*;
import com.lucky.jacklamb.enums.PrimaryType;
import com.lucky.jacklamb.exception.NotFindPrimaryKeyFieldException;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 实体类管理工具
 * @author fk-7075
 *
 */
public abstract class PojoManage {

	private static final Class<? extends Annotation>[] JPA_ANNOTATION
			=new Class[]{ManyToMany.class,ManyToOne.class,OneToMany.class,OneToOne.class};
	
	public static String getIpPort(String dbname){
		String url = LuckyDataSourceManage.getDataSource(dbname).getJdbcUrl();
		return url.substring(url.indexOf("//"),url.lastIndexOf("/")+1);
		
	}
	
	/**
	 * 获取当前数据源对应数据库的类型
	 * @param dbname
	 * @return
	 */
	public static String getDatabaseType(String dbname) {
		String jdbcDriver= LuckyDataSourceManage.getDataSource(dbname).getDriverClass();
		if(jdbcDriver.contains("mysql")) {
			return "MYSQL";
		}
		if(jdbcDriver.contains("db2")) {
			return "DB2";
		}
		if(jdbcDriver.contains("oracle")) {
			return "ORACLE";
		}
		if(jdbcDriver.contains("postgresql")) {
			return "POSTGRESQL";
		}
		if(jdbcDriver.contains("sqlserver")) {
			return "SQL SERVER";
		}
		if(jdbcDriver.contains("sybase")) {
			return "SYBASE";
		}
		if(jdbcDriver.contains("access")) {
			return "ACCESS";
		}
		return null;
	}
	
	/**
	 * 获取当前数据源对应数据库的名字
	 * @param dbname
	 * @return
	 */
	public static String getDatabaseName(String dbname) {
		String url = LuckyDataSourceManage.getDataSource(dbname).getJdbcUrl();
		String databasename=url.substring((url.lastIndexOf("/")+1),url.length());
		if(databasename.contains("?")) {
			databasename=databasename.substring(0, databasename.indexOf("?"));
		}
		return databasename;
	}
	
	/**
	 * 得到该实体类属性对应的数据库字段映射
	 * @param field
	 * @return
	 */
	public static String getTableField(String dbname,Field field) {
		if(field.isAnnotationPresent(Columns.class)){
			Column column=getColumn(field.getAnnotation(Columns.class),dbname);
			if(column!=null){
				return column.value();
			}
			return field.getName();

		}else if(field.isAnnotationPresent(Ids.class)){
			Id id=getId(field.getAnnotation(Ids.class),dbname);
			if(id!=null){
				return id.value();
			}
			return field.getName();
		}else if(field.isAnnotationPresent(Keys.class)){
			Key key=getKey(field.getAnnotation(Keys.class),dbname);
			if(key!=null){
				return key.value();
			}
			return field.getName();

		}else if(isNoColumn(field,dbname)){
			return "";
		}else if(field.isAnnotationPresent(Column.class)) {
			Column coumn=field.getAnnotation(Column.class);
			if(UNIVERSAL.equals(coumn.dbname())){
				if("".equals(coumn.value())) {
					return field.getName();
				}
				return coumn.value();
			}
			if(dbname.equals(coumn.dbname())){
				if("".equals(coumn.value())) {
					return field.getName();
				}
				return coumn.value();
			}

			return field.getName();
		}else if(field.isAnnotationPresent(Id.class)) {
			Id id=field.getAnnotation(Id.class);
			if(UNIVERSAL.equals(id.dbname())){
				if("".equals(id.value())) {
					return field.getName();
				}
				return id.value();
			}
			if(dbname.equals(id.dbname())){
				if("".equals(id.value())) {
					return field.getName();
				}
				return id.value();
			}
			return field.getName();
		}else if(field.isAnnotationPresent(Key.class)) {
			Key key=field.getAnnotation(Key.class);
			if(UNIVERSAL.equals(key.dbname())){
				if("".equals(key.value())) {
					return field.getName();
				}
				return key.value();
			}
			if(dbname.equals(key.dbname())){
				if("".equals(key.value())) {
					return field.getName();
				}
				return key.value();
			}
			return field.getName();
		}else if(field.isAnnotationPresent(ManyToOne.class)){
			return field.getAnnotation(ManyToOne.class).column();
		}else if(field.isAnnotationPresent(NoColumn.class)){
			return "";
		}else{
			return field.getName();
		}
	}
	
	/**
	 * 得到该字段是否可以为null的配置
	 * @param field
	 * @return
	 */
	public static boolean allownull(Field field,String dbname) {
		if(field.isAnnotationPresent(Columns.class)){
			Column column=getColumn(field.getAnnotation(Columns.class),dbname);
			if(column!=null){
				return column.allownull();
			}
			return true;
		}else if(field.isAnnotationPresent(Keys.class)){
			Key key=getKey(field.getAnnotation(Keys.class),dbname);
			if(key!=null){
				return key.allownull();
			}
			return true;
		}else if(field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			if(UNIVERSAL.equals(column.dbname())){
				return column.allownull();
			}
			if(dbname.equals(column.dbname())){
				return column.allownull();
			}
			return true;
		}else if(field.isAnnotationPresent(Key.class)) {
			Key key = field.getAnnotation(Key.class);
			if(UNIVERSAL.equals(key.dbname())){
				return key.allownull();
			}
			if(dbname.equals(key.dbname())){
				return key.allownull();
			}
			return true;
		}else {
			return true;
		}
	}
	
	/**
	 * 得到属性的长度配置
	 * @param field
	 * @return
	 */
	public static int getLength(Field field,String dbname) {
		if(field.isAnnotationPresent(Ids.class)){
			Id id=getId(field.getAnnotation(Ids.class),dbname);
			if(id!=null){
				return id.length();
			}
			return 100;
		}else if(field.isAnnotationPresent(Keys.class)){
			Key key=getKey(field.getAnnotation(Keys.class),dbname);
			if(key!=null){
				return key.length();
			}
			return 100;
		}else if(field.isAnnotationPresent(Columns.class)){
			Column column=getColumn(field.getAnnotation(Columns.class),dbname);
			if(column!=null){
				return column.length();
			}
			return 100;
		}else if(field.isAnnotationPresent(Id.class)) {
			Id id = field.getAnnotation(Id.class);
			if(UNIVERSAL.equals(id.dbname())){
				return id.length();
			}
			if(dbname.equals(id.dbname())){
				return id.length();
			}
			return 100;
		}else if(field.isAnnotationPresent(Key.class)) {
			Key key = field.getAnnotation(Key.class);
			if(UNIVERSAL.equals(key.dbname())){
				return key.length();
			}
			if(dbname.equals(key.dbname())){
				return key.length();
			}
			return 100;
		}else if(field.isAnnotationPresent(Column.class)) {
			Column column = field.getAnnotation(Column.class);
			if(UNIVERSAL.equals(column.dbname())){
				return column.length();
			}
			if(dbname.equals(column.dbname())){
				return column.length();
			}
			return 100;
		}else {
			return 100;
		}
	}
	
	/**
	 * 得到该实体类的Id属性
	 * @param pojoClass
	 * @return
	 */
	public static Field getIdField(Class<?> pojoClass) {
		Field[] pojoFields= ClassUtils.getAllFields(pojoClass);
		for(Field field:pojoFields) {
			if(field.isAnnotationPresent(Id.class)||field.isAnnotationPresent(Ids.class)) {
				return field;
			}
		}
		throw new NotFindPrimaryKeyFieldException("没有找到"+pojoClass.getName()+"的Id属性，请检查该类的ID属性上是否有配置@Id或@Ids注解.");
	}
	
	/**
	 * 得到该实体类的映射表名
	 * @param pojoClass
	 * @return
	 */
	public static String getTable(Class<?> pojoClass,String dbname) {
		pojoClass=getTableClass(pojoClass);
		if(pojoClass.isAnnotationPresent(Tables.class)) {
			Table table=getTable(pojoClass.getAnnotation(Tables.class),dbname);
			if(table!=null){
				if("".equals(table.value())){
					return pojoClass.getSimpleName().toLowerCase();
				}
				return table.value();
			}else {
				return pojoClass.getSimpleName().toLowerCase();
			}
		}else if(pojoClass.isAnnotationPresent(Table.class)){
			Table table=pojoClass.getAnnotation(Table.class);
			if(UNIVERSAL.equals(table.dbname())){
				if("".equals(table.value())) {
					return pojoClass.getSimpleName().toLowerCase();
				}
				return table.value();
			}
			if(dbname.equals(table.dbname())){
				if("".equals(table.value())){
					return pojoClass.getSimpleName().toLowerCase();
				}
				return table.value();
			}
			return pojoClass.getSimpleName().toLowerCase();
		}else {
			return pojoClass.getSimpleName().toLowerCase();
		}
	}

	private static Class<?> getTableClass(Class<?> quasiTableClass){
		Class<?> quasiClass = getQuasiTableClass(quasiTableClass);
		return quasiClass==null?quasiTableClass:quasiClass;
	}

	private static Class<?> getQuasiTableClass(Class<?> quasiTableClass){
		if(quasiTableClass==Object.class){
			return null;
		}
		if(quasiTableClass.isAnnotationPresent(Table.class)
				||quasiTableClass.isAnnotationPresent(Tables.class)){
			return quasiTableClass;
		}else{
			return getQuasiTableClass(quasiTableClass.getSuperclass());
		}
	}
	
	/**
	 * 得到该实体对应表的级联删除信息
	 * @param pojoClass
	 * @return
	 */
	public static boolean cascadeDelete(Class<?> pojoClass,String dbname) {
		pojoClass=getTableClass(pojoClass);
		if(pojoClass.isAnnotationPresent(Tables.class)){
			Table table=getTable(pojoClass.getAnnotation(Tables.class),dbname);
			if(table!=null){
				return table.cascadeDelete();
			}
			return false;
		}else if(pojoClass.isAnnotationPresent(Table.class)) {
			Table table=pojoClass.getAnnotation(Table.class);
			if(UNIVERSAL.equals(table.dbname())){
				return table.cascadeDelete();
			}
			if(dbname.equals(table.dbname())){
				return table.cascadeDelete();
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 得到该实体对应表的级更新除信息
	 * @param pojoClass
	 * @return
	 */
	public static boolean cascadeUpdate(Class<?> pojoClass,String dbname) {
		pojoClass=getTableClass(pojoClass);
		if(pojoClass.isAnnotationPresent(Tables.class)){
			Table table=getTable(pojoClass.getAnnotation(Tables.class),dbname);
			if(table!=null){
				return table.cascadeUpdate();
			}
			return false;
		}else if(pojoClass.isAnnotationPresent(Table.class)) {
			Table table=pojoClass.getAnnotation(Table.class);
			if(UNIVERSAL.equals(table.dbname())){
				return table.cascadeUpdate();
			}
			if(dbname.equals(table.dbname())){
				return table.cascadeUpdate();
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 得到该实体类的映射主键名
	 * @param pojoClass
	 * @return
	 */
	public static String getIdString(Class<?> pojoClass,String dbname) {
		Field idField = getIdField(pojoClass);
		if(idField.isAnnotationPresent(Ids.class)){
			Id id=getId(idField.getAnnotation(Ids.class),dbname);
			if(id!=null){
				if("".equals(id.value())){
					return idField.getName();
				}
				return id.value();
			}
			return idField.getName();
		}else{
			Id id = idField.getAnnotation(Id.class);
			if(UNIVERSAL.equals(id.dbname())){
				if("".equals(id.value())) {
					return idField.getName();
				}
				return id.value();
			}
			if(dbname.equals(id.dbname())){
				if("".equals(id.value())){
					return idField.getName();
				}
				return id.value();
			}
			return idField.getName();
		}
	}
	

	/**
	 * 得到该实体类的所有映射外键名与属性组成的Map
	 * @param pojoClass
	 * @return
	 */
	public static Map<Field,Class<?>> getKeyFieldMap(Class<?> pojoClass,String dbname){
		Map<Field,Class<?>> keys=new HashMap<>();
		Field[] pojoFields= ClassUtils.getAllFields(pojoClass);
		for(Field field:pojoFields) {
			if(field.isAnnotationPresent(Keys.class)){
				Key key=getKey(field.getAnnotation(Keys.class),dbname);
				if(key!=null){
					keys.put(field, key.pojo());
				}
			}else if(field.isAnnotationPresent(Key.class)) {
				Key key=field.getAnnotation(Key.class);
				keys.put(field, key.pojo());
			}
		}
		return keys;
	}
	
	/**
	 * 外键对应类反推外键属性
	 * @param clap 主表类
	 * @param clak 外键表类
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Field classToField(Class<?> clap,Class<?> clak,String dbname) {
		List<Field> clapKeyFields = (List<Field>) getKeyFields(clap, dbname,true);
		for(Field field: clapKeyFields) {
			Key key=field.getAnnotation(Key.class);
			if(key.pojo().equals(clak)) {
				return field;
			}
		}
		return null;
	}
	
	/**
	 * 得到该实体对应表的外键信息
	 * @param pojoClass
	 * @param iskey true(返回外键属性集合)/false(返回外键对应的的实体Class)
	 * @return
	 */
	public static List<?> getKeyFields(Class<?> pojoClass,String dbname,boolean iskey){
		Map<Field,Class<?>> keyAdnField=getKeyFieldMap(pojoClass,dbname);
		List<Field> keys=new ArrayList<>();
		List<Class<?>> clzzs=new ArrayList<>();
		for(Entry<Field,Class<?>> entry:keyAdnField.entrySet()) {
			keys.add(entry.getKey());
			clzzs.add(entry.getValue());
		}
		if(iskey) {
			return keys;
		} else {
			return clzzs;
		}
	}
	
	/**
	 * 判断该实体对应表的主键类型(自增int主键/UUID主键/普通主键)
	 * @param pojoClass
	 * @return
	 */
	public static PrimaryType getIdType(Class<?> pojoClass,String dbname) {
		Field idF=getIdField(pojoClass);
		if(idF.isAnnotationPresent(Ids.class)){
			Id id=getId(idF.getAnnotation(Ids.class),dbname);
			if(id!=null){
				return id.type();
			}
			return PrimaryType.DEFAULT;
		}else if(idF.isAnnotationPresent(Id.class)){
			Id id=idF.getAnnotation(Id.class);
			if(UNIVERSAL.equals(id.dbname())) {
				return id.type();
			}
			if(dbname.equals(id.dbname())){
				return id.type();
			}
			return PrimaryType.DEFAULT;
		}else{
			return PrimaryType.DEFAULT;
		}

	}
	
	/**
	 * 得到设置主键索引的信息
	 * @param pojoClass
	 * @return
	 */
	public static String primary(Class<?> pojoClass,String dbname) {
		pojoClass=getTableClass(pojoClass);
		if(pojoClass.isAnnotationPresent(Tables.class)){
			Table table=getTable(pojoClass.getAnnotation(Tables.class),dbname);
			if(table!=null){
				return table.primary();
			}
			return "";
		}else if(pojoClass.isAnnotationPresent(Table.class)) {
			Table table=pojoClass.getAnnotation(Table.class);
			if(!"".equals(table.primary())){
				return table.primary();
			}
			return "";
		}else {
			return "";
		}
	}
	
	/**
	 * 得到设置普通索引的信息
	 * @param pojoClass
	 * @return
	 */
	public static String[] index(Class<?> pojoClass,String dbname) {
		pojoClass=getTableClass(pojoClass);
		if(pojoClass.isAnnotationPresent(Tables.class)){
			Table table=getTable(pojoClass.getAnnotation(Tables.class),dbname);
			if(table!=null){
				return table.index();
			}
			return new String[0];
		}else if(pojoClass.isAnnotationPresent(Table.class)) {
			Table table=pojoClass.getAnnotation(Table.class);
			if(table.index().length!=0){
				return table.index();
			}
			return new String[0];
		}else {
			return new String[0];
		}
	}
	
	/**
	 * 得到设置唯一值索引的信息
	 * @param pojoClass
	 * @return
	 */
	public static String[] unique(Class<?> pojoClass,String dbname) {
		pojoClass=getTableClass(pojoClass);
		if(pojoClass.isAnnotationPresent(Tables.class)){
			Table table=getTable(pojoClass.getAnnotation(Tables.class),dbname);
			if(table!=null){
				return table.unique();
			}
			return new String[0];
		}else if(pojoClass.isAnnotationPresent(Table.class)) {
			Table table=pojoClass.getAnnotation(Table.class);
			if(table.unique().length!=0){
				return table.unique();
			}
			return new String[0];
		}else {
			return new String[0];
		}
	}
	
	/**
	 * 得到设置全文索引的信息
	 * @param pojoClass
	 * @return
	 */
	public static String[] fulltext(Class<?> pojoClass,String dbname) {
		pojoClass=getTableClass(pojoClass);
		if(pojoClass.isAnnotationPresent(Tables.class)){
			Table table=getTable(pojoClass.getAnnotation(Tables.class),dbname);
			if(table!=null){
				return table.fulltext();
			}
			return new String[0];
		}else if(pojoClass.isAnnotationPresent(Table.class)) {
			Table table=pojoClass.getAnnotation(Table.class);
			if(table.fulltext().length!=0){
				return table.fulltext();
			}
			return new String[0];
		}else {
			return new String[0];
		}
	}

	/**
	 * 得到表的别名，在连接操作时使用
	 * @param pojoClass
	 * @return
	 */
	public static String tableAlias(Class<?> pojoClass,String dbname){
		pojoClass=getTableClass(pojoClass);
		if(pojoClass.isAnnotationPresent(Table.class)){
			String alias=pojoClass.getAnnotation(Table.class).alias();
			if(!"".equals(alias)) {
				return alias;
			}
			return getTable(pojoClass,dbname);
		}
		return getTable(pojoClass,dbname);
	}

	/**
	 * 别名，From语句后使用
	 * @param pojoClass
	 * @return
	 */
	public static String selectFromTableAlias(Class<?> pojoClass,String dbname){
		if(tableAlias(pojoClass,dbname).equals(getTable(pojoClass,dbname))) {
			return getTable(pojoClass,dbname);
		}
		return getTable(pojoClass,dbname)+" "+tableAlias(pojoClass,dbname);
	}

	private static final String UNIVERSAL="UNIVERSAL";

	private static Column getColumn(Columns columns,String dbname){
		Column[] columnsArray=columns.value();
		Map<String,Column> columnMap=new HashMap<>();
		for (Column column : columnsArray) {
			columnMap.put(column.dbname(),column);
		}
		if(columnMap.containsKey(dbname)){
			return columnMap.get(dbname);
		}
		return columnMap.containsKey(UNIVERSAL)?columnMap.get(UNIVERSAL):null;
	}

	private static Id getId(Ids ids,String dbname){
		Id[] idArray=ids.value();
		Map<String,Id> idMap=new HashMap<>();
		for (Id id : idArray) {
			idMap.put(id.dbname(),id);
		}
		if(idMap.containsKey(dbname)){
			return idMap.get(dbname);
		}
		return idMap.containsKey(UNIVERSAL)?idMap.get(UNIVERSAL):null;
	}

	private static Key getKey(Keys keys,String dbname){
		Key[] keyArray=keys.value();
		Map<String,Key> keyMap=new HashMap<>();
		for (Key key : keyArray) {
			keyMap.put(key.dbname(),key);
		}
		if(keyMap.containsKey(dbname)){
			return keyMap.get(dbname);
		}
		return keyMap.containsKey(UNIVERSAL)?keyMap.get(UNIVERSAL):null;
	}

	private static boolean isNoColumn(NoColumns noColumns,String dbname){
		NoColumn[] noColumnArray=noColumns.value();
		Map<String,NoColumn> noColumnMap=new HashMap<>();
		for (NoColumn noColumn : noColumnArray) {
			noColumnMap.put(noColumn.value(),noColumn);
		}
		if(noColumnMap.containsKey(dbname)){
			return true;
		}
		return false;
	}

	private static boolean isNoPackage(NoPackages noPackages,String dbname){
		NoPackage[] noPackageArray=noPackages.value();
		Map<String,NoPackage> noColumnMap=new HashMap<>();
		for (NoPackage noPackage : noPackageArray) {
			noColumnMap.put(noPackage.value(),noPackage);
		}
		if(noColumnMap.containsKey(dbname)){
			return true;
		}
		return false;
	}

	private static Table getTable(Tables tables,String dbname){
		Table[] tableArray=tables.value();
		Map<String,Table> tableMap=new HashMap<>();
		for (Table table : tableArray) {
			tableMap.put(table.dbname(),table);
		}
		if(tableMap.containsKey(dbname)){
			return tableMap.get(dbname);
		}
		return tableMap.containsKey(UNIVERSAL)?tableMap.get(UNIVERSAL):null;
	}

	public static boolean isNoColumn(Field field,String dbname){
		if(field.isAnnotationPresent(NoColumn.class)
				||field.isAnnotationPresent(OneToMany.class)
				||field.isAnnotationPresent(OneToOne.class)
				||field.isAnnotationPresent(ManyToMany.class)){
			return true;
		}else if(field.isAnnotationPresent(NoColumns.class)){
			return isNoColumn(field.getAnnotation(NoColumns.class),dbname);
		}else{
			return false;
		}
	}

	public static boolean isNoPackage(Field field,String dbname){
		if(field.isAnnotationPresent(NoPackage.class)){
			return true;
		}else if(field.isAnnotationPresent(NoPackages.class)){
			return isNoPackage(field.getAnnotation(NoPackages.class),dbname);
		}else{
			return false;
		}
	}


	public static boolean isJpaAnnField(Field field,String dbname){
		return AnnotationUtils.isExistOrByArray(field,JPA_ANNOTATION);
	}
}
