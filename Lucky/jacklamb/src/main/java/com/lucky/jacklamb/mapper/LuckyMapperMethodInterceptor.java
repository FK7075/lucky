package com.lucky.jacklamb.mapper;

import com.lucky.jacklamb.annotation.jpa.FullMapQuery;
import com.lucky.jacklamb.annotation.jpa.SimpleQuery;
import com.lucky.jacklamb.annotation.mapper.*;
import com.lucky.jacklamb.annotation.table.Id;
import com.lucky.jacklamb.enums.PrimaryType;
import com.lucky.jacklamb.enums.Sort;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCore;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.jacklamb.mapper.jpa.IllegalJPAExpressionException;
import com.lucky.jacklamb.mapper.jpa.JpaSample;
import com.lucky.jacklamb.querybuilder.QueryBuilder;
import com.lucky.jacklamb.querybuilder.SqlAndObject;
import com.lucky.jacklamb.querybuilder.SqlFragProce;
import com.lucky.jacklamb.querybuilder.Translator;
import com.lucky.utils.base.Assert;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.conversion.proxy.Conversion;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import com.lucky.utils.reflect.MethodUtils;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static com.lucky.utils.regula.Regular.Sharp;

public class LuckyMapperMethodInterceptor implements MethodInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LuckyMapperMethodInterceptor.class);

    private final Class<?> LuckyMapperGeneric;

    private final SqlCore sqlCore;

    private final Map<String, String> sqlMap;

    public LuckyMapperMethodInterceptor(Class<?> luckyMapperGeneric, SqlCore sqlCore, Map<String, String> sqlMap) {
        LuckyMapperGeneric = luckyMapperGeneric;
        this.sqlCore = sqlCore;
        this.sqlMap = sqlMap;
    }

    /**
     * 执行带有SQL的接口方法
     *
     * @param method 接口方法
     * @param args   参数列表
     * @param sql_fp SQl片段化
     * @param sql    sql语句
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private int updateSql(Method method, Object[] args, SqlFragProce sql_fp, String sql) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        if (sql.contains("#{")) {
            Class<?> obc = args[0].getClass();
            SqlAndArray sqlArr = noSqlTo(obc, sql);
            if (method.isAnnotationPresent(Change.class)) {
                return dynamicUpdateSql(sql_fp, method, sqlArr.getSql(), sqlArr.getArray());
            } else {
                return sqlCore.updateMethod(sqlArr.getSql(), method, sqlArr.getArray());
            }
        } else {
            if (method.isAnnotationPresent(Change.class)) {
                return dynamicUpdateSql(sql_fp, method, sql, args);
            } else {
                return sqlCore.updateMethod(sql, method, args);
            }
        }
    }

    /**
     * 将含有#{}的sql转化为预编译的sql
     *
     * @param obj   上下文对象
     * @param noSql 包含#{}的sql
     * @return SqlAndArray对象包含预编译sql和执行参数
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private SqlAndArray noSqlTo(Object obj, String noSql) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        SqlAndArray sqlArr = new SqlAndArray();
        List<String> fieldname = BaseUtils.getSqlField(noSql);
        Map<String, Object> fieldNameValueMap = Conversion.getSourceNameValueMap(obj, "");
        List<Object> fields = new ArrayList<>();
        for (String fieldName : fieldname) {
            if (fieldNameValueMap.containsKey(fieldName)){
                Object objp = fieldNameValueMap.get(fieldName);
                if(objp instanceof Collection){
                    noSql=noSql.replaceAll("#\\{"+fieldName+"}","?C");
                }
                fields.add(fieldNameValueMap.get(fieldName));
            }
        }
        //得到预编译的SQL语句
        noSql=noSql.replaceAll(Sharp, "?");
        sqlArr.setSql(noSql);
        sqlArr.setArray(fields.toArray());
        return sqlArr;
    }


    /**
     * 基于非空检查的SQL语句的执行
     *
     * @param sql_fp SQL片段化
     * @param sql    sql语句(预编译)
     * @param args   执行参数
     * @return true/false
     */
    private int dynamicUpdateSql(SqlFragProce sql_fp, Method method, String sql, Object[] args) {
        SqlAndObject so = sql_fp.filterSql(sql, args);
        return sqlCore.updateMethod(so.getSqlStr(), method, so.getObjects());
    }

    /**
     * 得到List的泛型的Class
     *
     * @param method 接口方法
     * @return List的泛型类型的Class
     */
    private Class<?> getGeneric(Method method) {
        ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
        Type[] entry = type.getActualTypeArguments();
        return (Class<?>) entry[0];
    }

    /**
     * 处理被@Select注解标注的接口方法
     *
     * @param method 接口方法
     * @param args   参数列表
     * @param sql_fp SQl片段化类
     * @return Object
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private <T> Object select(Method method, Object[] args, SqlFragProce sql_fp) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Class<?> c = method.getReturnType();
        Select sel = method.getAnnotation(Select.class);
        if (sel.byid()) {
            if (args.length == 2) {
                return sqlCore.getOne((Class<?>) args[0], args[1]);
            } else if (args.length == 1) {
                return sqlCore.getOne(method.getReturnType(), args[0]);
            } else {
                return false;
            }
        } else {
            String sql = sel.value();
            if ("".equals(sql)) {
                if (sel.sResults().length == 0 && sel.hResults().length == 0) {
                    if (List.class.isAssignableFrom(c)||Set.class.isAssignableFrom(c)) {
                        List<Object> listResult = sqlCore.getList(args[0]);
                        return List.class.isAssignableFrom(c)?listResult:new HashSet<>(listResult);
                    }
                    return sqlCore.getObject(args[0]);
                } else {// 有指定列的标注
                    if (sel.hResults().length != 0 && sel.sResults().length != 0) {
                        throw new RuntimeException("@Select注解的\"hResults\"属性和\"sResults\"属性不可以同时使用！错误位置：" + method);
                    }
                    Parameter[] parameters = method.getParameters();
                    QueryBuilder query = new QueryBuilder();
                    query.setDbname(sqlCore.getDbName());
                    query.addObject(args);
                    if (sel.sResults().length != 0) {
                        query.addResult(sel.sResults());
                    }
                    if (sel.hResults().length != 0) {
                        query.hiddenResult(sel.hResults());
                    }
                    if (List.class.isAssignableFrom(c)||Set.class.isAssignableFrom(c)) {
                        Class<?> listGeneric = getGeneric(method);
                        List<?> listResult = sqlCore.query(query, listGeneric);
                        return List.class.isAssignableFrom(c)?listResult: new HashSet<>(listResult);
                    }
                    List<?> list = sqlCore.query(query, c);
                    return Assert.isEmptyCollection(list)?null:list.get(0);
                }
            } else {
                if (sql.contains("#{")) {
                    if (method.getParameterCount() == 3) {
                        pageParam(method, args);
                    }
                    SqlAndArray sqlArr = noSqlTo(args[0], sql);
                    if (List.class.isAssignableFrom(c)||Set.class.isAssignableFrom(c)) {
                        Class<?> listGeneric = getGeneric(method);
                        if (method.isAnnotationPresent(Change.class)) {
                            SqlAndObject so = sql_fp.filterSql(sqlArr.getSql(), sqlArr.getArray());
                            if (method.getParameterCount() == 3) {
                                List<Object> list = new ArrayList<>();
                                list.addAll(Arrays.asList(so.getObjects()));
                                list.add(args[1]);
                                list.add(args[2]);
                                List<?> listResult = sqlCore.getListMethod(listGeneric, method, so.getSqlStr(), list.toArray());
                                return List.class.isAssignableFrom(c)?listResult:new HashSet<>(listResult);
                            }
                            List<?> listResult = sqlCore.getListMethod(listGeneric, method, so.getSqlStr(), so.getObjects());
                            return  List.class.isAssignableFrom(c)?listResult:new HashSet<>(listResult);
                        } else {
                            if (method.getParameterCount() == 3) {
                                List<Object> list = new ArrayList<>();
                                list.addAll(Arrays.asList(sqlArr.getArray()));
                                list.add(args[1]);
                                list.add(args[2]);
                                List<?> listResult = sqlCore.getListMethod(listGeneric, method, sqlArr.getSql(), list.toArray());
                                return List.class.isAssignableFrom(c)?listResult:new HashSet<>(listResult);
                            }
                            List<?> listResult = sqlCore.getListMethod(listGeneric, method, sqlArr.getSql(), sqlArr.getArray());
                            return List.class.isAssignableFrom(c)?listResult:new HashSet<>(listResult);
                        }
                    } else {
                        List<T> list = new ArrayList<>();
                        if (method.isAnnotationPresent(Change.class)) {
                            SqlAndObject so = sql_fp.filterSql(sqlArr.getSql(), sqlArr.getArray());
                            if (method.getParameterCount() == 3) {
                                List<Object> lists = new ArrayList<>();
                                lists.addAll(Arrays.asList(so.getObjects()));
                                lists.add(args[1]);
                                lists.add(args[2]);
                                List<?> listResult = sqlCore.getListMethod(c, method, so.getSqlStr(), lists.toArray());
                                return Assert.isEmptyCollection(listResult)?null:listResult.get(0);
                            }
                            return sqlCore.getObjectMethod(c, method, so.getSqlStr(), so.getObjects());
                        } else {
                            if (method.getParameterCount() == 3) {
                                List<Object> lists = new ArrayList<>();
                                lists.addAll(Arrays.asList(sqlArr.getArray()));
                                lists.add(args[1]);
                                lists.add(args[2]);
                                List<?> listResult = sqlCore.getListMethod(c, method, sqlArr.getSql(), list.toArray());
                                return  Assert.isEmptyCollection(listResult)?null:listResult.get(0);
                            }
                            return sqlCore.getObjectMethod(c, method, sqlArr.getSql(), sqlArr.getArray());
                        }
                    }
                } else {
                    pageParam(method, args);
                    if (List.class.isAssignableFrom(c)||Set.class.isAssignableFrom(c)) {
                        Class<?> listGeneric = getGeneric(method);
                        List<?> listResult;
                        if (method.isAnnotationPresent(Change.class)) {
                            SqlAndObject so = sql_fp.filterSql(sql, args);
                            listResult = sqlCore.getListMethod(listGeneric, method, so.getSqlStr(), so.getObjects());
                        } else {
                            listResult=sqlCore.getListMethod(listGeneric, method, sql, args);
                        }
                        return List.class.isAssignableFrom(c)?listResult:new HashSet<>(listResult);
                    } else {
                        if (method.isAnnotationPresent(Change.class)) {
                            SqlAndObject so = sql_fp.filterSql(sql, args);
                            return sqlCore.getObjectMethod(c, method, so.getSqlStr(), so.getObjects());
                        } else {
                            return sqlCore.getObjectMethod(c, method, sql, args);
                        }
                    }
                }
            }
        }
    }


    /**
     * 处理被@Update注解标注的接口方法
     *
     * @param method 接口方法
     * @param args   参数列表
     * @param sql_fp SQl片段化类
     * @return true/false
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private <T> int update(Method method, Object[] args, SqlFragProce sql_fp) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Update upd = method.getAnnotation(Update.class);
        if (upd.batch()) {
            return sqlCore.updateByCollection((Collection<T>) args[0]);
        }
        String sql = upd.value();
        if ("".equals(sql)) {
            List<String> list = new ArrayList<>();
            String[] array;
            Object pojo = null;
            Parameter[] parameters = method.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                if (parameters[i].isAnnotationPresent(X.class)) {
                    if (List.class.isAssignableFrom(parameters[i].getType())) {
                        list.addAll((List<String>) args[i]);
                    } else if (String.class.isAssignableFrom(parameters[i].getType())) {
                        list.add((String) args[i]);
                    } else {
                        throw new RuntimeException("@Update更新操作中意外的标注类型：" + parameters[i].getType().getName() + "!@X注解只能标注String和List<String>类型的参数.错误位置：" + method);
                    }
                } else {
                    pojo = args[i];
                }
            }
            array = new String[list.size()];
            list.toArray(array);
            if (pojo == null) {
                throw new RuntimeException("@Update更新操作异常：没有找到用于更新操作的实体类对象!错误位置：" + method);
            }
            return sqlCore.update(pojo, array);
        } else {
            return updateSql(method, args, sql_fp, sql);
        }
    }

    /**
     * 处理被@Delete注解标注的接口方法
     *
     * @param method 接口方法
     * @param args   参数列表
     * @param sql_fp SQl片段化类
     * @return true/false
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private <T> int delete(Method method, Object[] args, SqlFragProce sql_fp) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Delete del = method.getAnnotation(Delete.class);
        if (del.byid()) {
            return sqlCore.delete((Class<?>) args[0], args[1]);
        }
        if (del.batch()) {
            return sqlCore.deleteByCollection((Collection<T>) args[0]);
        }
        String sql = del.value();
        if ("".equals(sql)) {
            return sqlCore.delete(args[0]);
        } else {
            return updateSql(method, args, sql_fp, sql);
        }
    }


    /**
     * 处理被@Insert注解标注的接口方法
     *
     * @param method 接口方法
     * @param args   参数列表
     * @param sql_fp SQl片段化类
     * @return true/false
     * @throws NoSuchFieldException
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    private <T> int insert(Method method, Object[] args, SqlFragProce sql_fp) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Insert ins = method.getAnnotation(Insert.class);
        String sql = ins.value();
        if ("".equals(sql)) {
            if (ins.batch()) {
                return sqlCore.insertByCollection((Collection<T>) args[0]);
            } else {
                if (ins.setautoId()) {
                    return sqlCore.insertSetId(args[0]);
                } else {
                    return sqlCore.insert(args[0]);
                }
            }
        } else {
            return updateSql(method, args, sql_fp, sql);
        }
    }

    /**
     * 处理被@Query注解标注的接口方法
     *
     * @param method 接口方法
     * @param args   参数列表
     * @return Object
     */
    private Object join(Method method, Object[] args) {
        Query query = method.getAnnotation(Query.class);
        Parameter[] parameters = method.getParameters();
        ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
        Type[] entry = type.getActualTypeArguments();
        Class<?> cla;
        if (LuckyMapperGeneric != null && ("query".equals(method.getName()) || "selectLimit".equals(method.getName()))) {
            cla = LuckyMapperGeneric;
        } else {
            cla = (Class<?>) entry[0];
        }
        if (query.queryBuilder()) {
            if (parameters.length != 1) {
                throw new RuntimeException("@Query参数数量溢出异常  size:" + parameters.length + "！@Query注解的\"queryBuilder\"模式下的参数只能是唯一，而且类型必须是 com.lucky.jacklamb.query.QueryBuilder！错误位置：" + method);
            }
            if (!QueryBuilder.class.isAssignableFrom(parameters[0].getType())) {
                throw new RuntimeException("@Query参数类型异常  错误类型:" + parameters[0].getType().getName() + "！@Query注解的\"queryBuilder\"模式下的参数只能是唯一，而且类型必须是 com.lucky.jacklamb.query.QueryBuilder！错误位置：" + method);
            }
            return sqlCore.query((QueryBuilder) args[0], cla, query.expression());
        }
        QueryBuilder queryBuilder = new QueryBuilder();
        queryBuilder.setDbname(sqlCore.getDbName());
        setQueryBuilder(query, parameters, method, args, queryBuilder);
        return sqlCore.query(queryBuilder, cla, query.expression());
    }

    /**
     * 处理被没有被注解标注的接口方法
     *
     * @param method             接口方法
     * @param args               参数列表
     * @param sql_fp             SQl片段化类
     * @param luckyMapperGeneric LuckyMapper接口的泛型
     * @return Object
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    private Object notHave(Method method, Object[] args, SqlFragProce sql_fp, Class<?> luckyMapperGeneric) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        Class<?> returnType = method.getReturnType();
        if (sqlMap.containsKey(method.getName())) {
            pageParam(method, args);
            String methodName = method.getName();
            String sqlStr = sqlMap.get(methodName);
            String sqlCopy = sqlStr.toUpperCase();
            if (sqlCopy.contains("#{")) {
                if (method.isAnnotationPresent(AutoId.class)) {
                    Field idField = PojoManage.getIdField(args[0].getClass());
                    Id id = idField.getAnnotation(Id.class);
                    if (id.type() == PrimaryType.AUTO_INT) {
                        sqlCore.setNextId(args[0]);
                    } else if (id.type() == PrimaryType.AUTO_UUID) {
                        idField.setAccessible(true);
                        idField.set(args[0], UUID.randomUUID().toString());
                    }
                }
                SqlAndArray sqlArr = noSqlTo(args[0], sqlStr);
                sqlStr = sqlArr.getSql();
                if (method.getParameterCount() == 3) {
                    List<Object> list = new ArrayList<>(Arrays.asList(sqlArr.getArray()));
                    list.add(args[1]);
                    list.add(args[2]);
                    args = list.toArray();
                } else {
                    args = sqlArr.getArray();
                }
            }
            if (sqlCopy.contains("SELECT")) {
                if ("C:".equalsIgnoreCase(sqlCopy.substring(0, 2))) {
                    sqlStr = sqlStr.substring(2);
                    SqlAndObject so = sql_fp.filterSql(sqlStr, args);
                    if (List.class.isAssignableFrom(returnType)||Set.class.isAssignableFrom(returnType)) {
                        ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                        Type[] entry = type.getActualTypeArguments();
                        Class<?> cla = (Class<?>) entry[0];
                        List<?> listResult = sqlCore.getListMethod(cla, method, so.getSqlStr(), so.getObjects());
                        return List.class.isAssignableFrom(returnType)?listResult:new HashSet<>(listResult);
                    } else {
                        return sqlCore.getObjectMethod(method.getReturnType(), method, so.getSqlStr(), so.getObjects());
                    }
                } else {
                    if (List.class.isAssignableFrom(returnType)||Set.class.isAssignableFrom(returnType)) {
                        ParameterizedType type = (ParameterizedType) method.getGenericReturnType();
                        Type[] entry = type.getActualTypeArguments();
                        Class<?> cla = (Class<?>) entry[0];
                        List<?> listResult = sqlCore.getListMethod(cla, method, sqlStr, args);
                        return List.class.isAssignableFrom(returnType)?listResult:new HashSet<>(listResult);
                    } else {
                        return sqlCore.getObjectMethod(method.getReturnType(), method, sqlStr, args);
                    }
                }
            } else {
                if ("C:".equalsIgnoreCase(sqlCopy.substring(0, 2))) {
                    sqlStr = sqlStr.substring(2);
                    return dynamicUpdateSql(sql_fp, method, sqlStr, args);
                } else {
                    return sqlCore.updateMethod(sqlStr, method, args);
                }
            }
        } else if(luckyMapperGeneric!=null){
            JpaSample jpaSample = new JpaSample(luckyMapperGeneric,sqlCore.getDbName());
            sqlCore.setFullMap(true);
            if(List.class.isAssignableFrom(returnType)||Set.class.isAssignableFrom(returnType)){
                try {
                    List<?> listResult = sqlCore.getList(MethodUtils.getReturnTypeGeneric(method)[0], jpaSample.sampleToSql(method.getName()), args);
                    return List.class.isAssignableFrom(returnType)?listResult:new HashSet<>(listResult);
                } catch (IllegalJPAExpressionException e) {
                    throw new RuntimeException("找不到与Mapper接口方法 "+method+" 相关的SQL配置，尝试使用JPA查询解释器解析该方法的方法名！解析失败，该方法名不符合JPA查询规范...",e);
                }
            }else{
                List<?> result = null;
                try {
                    result = sqlCore.getList(returnType, jpaSample.sampleToSql(method.getName()), args);
                } catch (IllegalJPAExpressionException e) {
                    throw new RuntimeException("找不到与Mapper接口方法 "+method+" 相关的SQL配置，尝试使用JPA查询解释器解析该方法的方法名！解析失败，该方法名不符合JPA查询规范...",e);
                }
                return Assert.isEmptyCollection(result)?null:result.get(0);
            }
        }else{
            throw new RuntimeException("无法代理的Mapper方法："+method+" ,没有为该方法配置相关的SQL操作...");
        }
    }


    @Override
    public Object intercept(Object object, Method method, Object[] params, MethodProxy methodProxy) throws Throwable {
        //Object方法不执行代理
        if(MethodUtils.isObjectMethod(method)){
            return methodProxy.invokeSuper(object,params);
        }
        Class<?> aClass = object.getClass().getInterfaces()[0];
        log.debug("Run ==> " +aClass.getName() + "." + method.getName() + "\n params=" + Arrays.toString(params));
        if(aClass.isAnnotationPresent(FullMapQuery.class)){
            sqlCore.setFullMap(true);
            if(method.isAnnotationPresent(SimpleQuery.class)){
                sqlCore.setFullMap(false);
            }
        }else{
            if(method.isAnnotationPresent(FullMapQuery.class)){
                sqlCore.setFullMap(true);
            }else{
                sqlCore.setFullMap(false);
            }
        }
			/*
			  用户自定义的Mapper如果继承了LuckyMapper<T>,代理selectById,deleteById,count,selectList,createTable,deleteByIdIn,selectByIdIn方法
			 这些方法的执行依赖LuckyMapper接口的泛型类型，所以需要特殊处理
			*/
        boolean isExtendLM = LuckyMapperGeneric != null;
        if (isExtendLM && "selectById".equals(method.getName())) {
            return sqlCore.getOne(LuckyMapperGeneric, params[0]);
        }
        if (isExtendLM && "deleteById".equals(method.getName())) {
            return sqlCore.delete(LuckyMapperGeneric, params[0]);
        }
        if (isExtendLM && "count".equals(method.getName()) && params.length == 0) {
            return sqlCore.count(LuckyMapperGeneric);
        }
        if (isExtendLM && "selectList".equals(method.getName()) && params.length == 0) {
            return sqlCore.getList(LuckyMapperGeneric);
        }
        if (isExtendLM && "createTable".equals(method.getName()) && params.length == 0) {
            sqlCore.createTable(LuckyMapperGeneric);
            return void.class;
        }
        if (isExtendLM && "deleteByIdIn".equals(method.getName()) && params.length == 1) {
            return sqlCore.deleteByIdIn(LuckyMapperGeneric, (List<?>) params[0]);
        }
        if (isExtendLM && "selectByIdIn".equals(method.getName()) && params.length == 1) {
            return sqlCore.getByIdIn(LuckyMapperGeneric, (List<?>) params[0]);
        }
        if(isExtendLM && "runScript".equals(method.getName()) && params.length == 1){
            sqlCore.runScript((Reader) params[0]);
            return void.class;
        }
        if(isExtendLM && "runScriptFullLine".equals(method.getName()) && params.length == 1){
            sqlCore.runScriptFullLine((Reader) params[0]);
            return void.class;
        }
        if(isExtendLM && "limit".equals(method.getName())&& params.length==2){
            Object nullObj = ClassUtils.newObject(LuckyMapperGeneric);
            Field[] fields=ClassUtils.getAllFields(LuckyMapperGeneric);
            for (Field field : fields) {
                FieldUtils.setValue(nullObj,field,null);
            }
            return sqlCore.getPageList(nullObj,(int)params[0],(int)params[1]);
        }

        //用户自定义Mapper接口方法的代理
        SqlFragProce sql_fp = SqlFragProce.getSqlFP();
        if (method.isAnnotationPresent(Select.class)) {
            return select(method, params, sql_fp);
        } else if (method.isAnnotationPresent(Update.class)) {
            return update(method, params, sql_fp);
        } else if (method.isAnnotationPresent(Delete.class)) {
            return delete(method, params, sql_fp);
        } else if (method.isAnnotationPresent(Insert.class)) {
            return insert(method, params, sql_fp);
        } else if (method.isAnnotationPresent(Query.class)) {
            return join(method, params);
        }  else if (method.isAnnotationPresent(QueryTr.class)) {
            return qtr(method, params);
        }else if (method.isAnnotationPresent(Count.class)) {
            return sqlCore.count(params[0]);
        } else {
            return notHave(method, params, sql_fp, LuckyMapperGeneric);
        }
    }

    private Object qtr(Method method, Object[] args){
        QueryTr query = method.getAnnotation(QueryTr.class);
        Class<?> returnType = method.getReturnType();
        Translator tr=(Translator) args[0];
        tr.setDbname(sqlCore.getDbName());
        switch (query.value()){
            case "SELECT" :{
             if(List.class.isAssignableFrom(returnType)) {
                if(LuckyMapperGeneric!=null){
                    tr.setPojoClass(LuckyMapperGeneric);
                    return sqlCore.getList(tr);
                }
                tr.setPojoClass(getGeneric(method));
                return sqlCore.getList(tr);
             } else{
                 if(LuckyMapperGeneric!=null){
                     tr.setPojoClass(LuckyMapperGeneric);
                     return sqlCore.getObject(tr);
                 }
                 tr.setPojoClass(getGeneric(method));
                 return sqlCore.getObject(tr);
             }
            }
            case "DELETE" :{
                if(LuckyMapperGeneric!=null){
                    return sqlCore.delete(LuckyMapperGeneric,tr);
                }else{
                    sqlCore.delete(getGeneric(method),tr);
                }
            }
            case "UPDATE" :{
                if(LuckyMapperGeneric!=null){
                    tr.setPojoClass(LuckyMapperGeneric);
                    return sqlCore.update(tr);
                }else{
                    tr.setPojoClass(getGeneric(method));
                    sqlCore.update(tr);
                }
            }
            default:{
                throw new RuntimeException("错误的@QueryTr属性！");
            }
        }
    }


    /**
     * 根据配置设置QueryBuilder对象
     *
     * @param query        Query注解对象
     * @param parameters   参数类型数组
     * @param args         参数值数组
     * @param queryBuilder QueryBuilder对象
     */
    private void setQueryBuilder(Query query, Parameter[] parameters, Method method, Object[] args, QueryBuilder queryBuilder) {
        /*
         * queryBuilder对象的设置有一定的顺序，addObjects()方法必须优先执行，
         * 所以必须先找到接口中用于查询的对象，之后才能设置查询的细节
         */
        int end = parameters.length;//用于记录非模糊查询参数的索引
        List<Integer> indexs = new ArrayList<>();
        List<Object> objectlist = new ArrayList<>();
        Object[] objectarray;
        if (query.limit()) {//分页模式，优先过滤掉两个分页参数
            if (parameters.length < 3) {
                throw new RuntimeException("@Query参数缺失异常！@Query注解的\"Like\"模式下的参数至少为3个，而且最后两个参数必须为int类型的分页参数(page,rows)！错误位置：" + method.getName());
            }
            indexs.add(end - 1);
            indexs.add(end - 2);
            for (int i = 0; i < end - 2; i++) {
                if (!parameters[i].isAnnotationPresent(Like.class)) {
                    objectlist.add(args[i]);
                    indexs.add(i);
                }
            }
            objectarray = new Object[objectlist.size()];
            objectlist.toArray(objectarray);
            queryBuilder.addObject(objectarray);
            queryBuilder.limit((int) args[end - 2], (int) args[end - 1]);
            setLike(parameters, queryBuilder, method, args, indexs, end - 2);
            setSort(query, queryBuilder);
            setResults(method, query, queryBuilder);
        } else {//非分页模式
            for (int i = 0; i < end; i++) {
                if (!parameters[i].isAnnotationPresent(Like.class)) {
                    objectlist.add(args[i]);
                    indexs.add(i);
                }
            }
            objectarray = new Object[objectlist.size()];
            objectlist.toArray(objectarray);
            queryBuilder.addObject(objectarray);
            setLike(parameters, queryBuilder, method, args, indexs, end);
            setSort(query, queryBuilder);
            setResults(method, query, queryBuilder);
        }

    }

    /**
     * 为queryBuilder对象设置Like参数
     *
     * @param parameters
     * @param queryBuilder
     * @param args
     * @param indexs
     * @param end
     */
    private void setLike(Parameter[] parameters, QueryBuilder queryBuilder, Method method, Object[] args, List<Integer> indexs, int end) {
        List<String> likelist = new ArrayList<>();
        String[] array;
        for (int i = 0; i < end; i++) {
            if (!indexs.contains(i)) {
                if (List.class.isAssignableFrom(parameters[i].getType())) {
                    likelist.addAll((List<String>) args[i]);
                } else if (String.class.isAssignableFrom(parameters[i].getType())) {
                    likelist.add((String) args[i]);
                } else {
                    throw new RuntimeException("@Query模糊查询模式中意外的标注类型：" + parameters[i].getType().getName() + "!@Like注解只能标注String和List<String>类型的参数.错误位置：" + method.getName());
                }
            }
            array = new String[likelist.size()];
            likelist.toArray(array);
            queryBuilder.addLike((String[]) array);
        }
    }

    /**
     * 为queryBuilder对象设置Sort参数
     *
     * @param query
     * @param queryBuilder
     */
    private void setSort(Query query, QueryBuilder queryBuilder) {
        for (String sort : query.sort()) {
            String[] fs = sort.replaceAll(" ", "").split(":");
            int parseInt = Integer.parseInt(fs[1]);
            if (parseInt == 1) {
                queryBuilder.addSort(fs[0], Sort.ASC);
            }
            if (parseInt == -1) {
                queryBuilder.addSort(fs[0], Sort.DESC);
            }
        }
    }

    /**
     * 为queryBuilder对象设置Results参数
     *
     * @param query
     * @param queryBuilder
     */
    private void setResults(Method method, Query query, QueryBuilder queryBuilder) {
        if (query.hResults().length != 0 && query.sResults().length != 0) {
            throw new RuntimeException("@Query注解的\"hResults\"属性和\"sResults\"属性不可以同时使用！错误位置：" + method.getName());
        }
        if (query.sResults().length != 0) {
            queryBuilder.addResult(query.sResults());
        }
        if (query.hResults().length != 0) {
            queryBuilder.hiddenResult(query.hResults());
        }
    }


    private void pageParam(Method method, Object[] args) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(Page.class)) {
                if (i == parameters.length - 1) {
                    args[i] = ((int) args[i] - 1) * (int) args[i - 1];
                    break;
                } else {
                    args[i] = ((int) args[i] - 1) * (int) args[i + 1];
                    break;
                }
            }
        }
    }
}

class SqlAndArray {

    private String sql;

    private Object[] array;

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getArray() {
        return array;
    }

    public void setArray(Object[] array) {
        this.array = array;
    }

    @Override
    public String toString() {
        return "SqlAndArray [sqlActuator=" + sql + ", array=" + Arrays.toString(array) + "]";
    }

}
