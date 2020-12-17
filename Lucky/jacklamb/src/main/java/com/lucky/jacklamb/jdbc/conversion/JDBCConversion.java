package com.lucky.jacklamb.jdbc.conversion;

import com.lucky.jacklamb.annotation.jpa.ManyToMany;
import com.lucky.jacklamb.annotation.jpa.ManyToOne;
import com.lucky.jacklamb.annotation.jpa.OneToMany;
import com.lucky.jacklamb.annotation.jpa.OneToOne;
import com.lucky.jacklamb.jdbc.core.SqlOperation;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.jacklamb.jdbc.core.sql.CreateSql;
import com.lucky.jacklamb.querybuilder.QFilter;
import com.lucky.jacklamb.typechange.JavaConversion;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/8/13 11:34
 */
public abstract class JDBCConversion {


    public static <E> E conversion(String dbname,Map<String,Object> queryResult,Class<E> entityClass,boolean isFullMap,Connection...conn){
        if(ClassUtils.isBasic(entityClass)){
            for(Map.Entry<String,Object> en:queryResult.entrySet()){
                return (E) JavaConversion.strToBasic(en.getValue().toString(),entityClass);
            }
            return null;
        }
        E result = ClassUtils.newObject(entityClass);
        Field[] allFields = ClassUtils.getAllFields(entityClass);
        String entityFieldName;
        for (Field entityField : allFields) {
            if(PojoManage.isNoPackage(entityField,dbname)){
                continue;
            }
            Class<?> fieldClass=entityField.getType();
            if(FieldUtils.isBasicSimpleType(entityField)){
                entityFieldName=PojoManage.getTableField(dbname,entityField).toUpperCase();
                if(queryResult.containsKey(entityFieldName)){
                    Object fieldValue = queryResult.get(entityFieldName);
                    if(fieldValue==null){
                        continue;
                    }
                    if(fieldClass==fieldValue.getClass()){
                        FieldUtils.setValue(result,entityField,fieldValue);
                    }else{
                        FieldUtils.setValue(result,entityField,JavaConversion.strToBasic(fieldValue.toString(),fieldClass));
                    }
                }
            }else{
                if(isFullMap){
                    specialTreatment(dbname, queryResult, entityClass, result, entityField,isFullMap,conn);
                }else{
                    if(PojoManage.isJpaAnnField(entityField,dbname)){
                        continue;
                    }
                    //非全映射 或者 未处理且不是过滤属性
                    Object fieldObject=conversion(dbname,queryResult,entityField.getType(),isFullMap,conn);
                    FieldUtils.setValue(result,entityField,fieldObject);
                }
            }
        }
        return result;
    }

    public static <E> List<E> conversion(String dbname, List<Map<String,Object>> queryResult, Class<E> entityClass,boolean isFullMap,Connection...conn){
        List<E> result=new ArrayList<>();
        for (Map<String, Object> entry : queryResult) {
            result.add(conversion(dbname,entry,entityClass,isFullMap, conn));
        }
        return result;
    }

    public static <E> E conversion(Map<String,Object> queryResult,Class<E> entityClass,Map<String,String> resultMap){
        E result = ClassUtils.newObject(entityClass);
        Field[] allFields = ClassUtils.getAllFields(entityClass);
        for (Field field : allFields) {

        }
        return result;
    }

    /**
     * 特殊列的处理
     * @param dbname 数据源
     * @param queryResult 结果集
     * @param entityClass 包装类类型
     * @param result 未完成的包装结果(该参数为entityClass对应类的对象)
     * @param field 当前要包装的特殊属性（被@OneToOne、@OneToMany、@ManyToOne、@ManyToMany标注的属性）
     * @param conn 数据库连接
     */
    private static void specialTreatment(String dbname,Map<String,Object> queryResult,Class<?> entityClass,Object result,Field field,boolean isFullMap,Connection...conn){

        /*
            一对多属性的设置
            1.被@OneToMany注解标注的属性必须为List<T>或者Set<T>
            2.得到泛型T的类型，T为子表的实体，根据T以及dbname便可以知道子表的表名
            3.得到@OneToMany注解的joinColumn()属性，即子表的外键名
            4.根据子表的表名和外键名即可以查询出所有的关联记录(对象)
            5.遍历所有子表对象，检查所有被@ManyToOne并且类型与父表类型相对应的属性
            6.将父表对象设置给子表对象的这些属性
            7.将子表对象设置给父表对象
         */
        Class<?> fieldClass=field.getType();
        String sqlTemp="SELECT %s FROM `%s` WHERE `%s`=?";
        if(field.isAnnotationPresent(OneToMany.class)){
            String idField=PojoManage.getIdString(entityClass,dbname);
            if(queryResult.containsKey(idField.toUpperCase())){
                String joinColumn=field.getAnnotation(OneToMany.class).joinColumn();
                Class<?> fieldGenericType = FieldUtils.getGenericType(field)[0];
                SqlOperation sqlOperation = new SqlOperation(conn[0],dbname,isFullMap);
                String sql=String.format(sqlTemp
                        ,new QFilter(fieldGenericType,dbname).lines()
                        ,PojoManage.getTable(fieldGenericType, dbname)
                        ,joinColumn);
                Object sqlValue=queryResult.get(idField.toUpperCase());
                String completeSql=CreateSql.getCompleteSql(sql,sqlValue);
                sqlOperation.setFullMap(false);
                List<?> list = sqlOperation.autoPackageToList(fieldGenericType,sql,sqlValue);
                if(list==null||list.isEmpty()){
                    if(List.class.isAssignableFrom(fieldClass)){
                        FieldUtils.setValue(result,field,list);
                    }else if(Set.class.isAssignableFrom(fieldClass)){
                        FieldUtils.setValue(result,field,new HashSet<>(list));
                    }
                }else{
                    List<Field> annFields = ClassUtils.getFieldByAnnotation(fieldGenericType, ManyToOne.class).stream()
                            .filter(f->entityClass==f.getType()).collect(Collectors.toList());
                    for (Object obj : list) {
                        for (Field annField : annFields) {
                            FieldUtils.setValue(obj,annField,result);
                        }
                    }
                    if(List.class.isAssignableFrom(fieldClass)){
                        FieldUtils.setValue(result,field,list);
                    }else if(Set.class.isAssignableFrom(fieldClass)){
                        FieldUtils.setValue(result,field,new HashSet<>(list));
                    }
                }
            }
            return ;
        }

        /*
            多对一属性设置
            1.该属性应被@ManyToObe注解标注
            2.得到该属性对应的表名以及ID属性
            3.根据表名、ID属性、ID属性值得到该条记录对应的对象
            4.将该对象设置给当前属性
         */
        if(field.isAnnotationPresent(ManyToOne.class)){
            String oneTableIdName=field.getAnnotation(ManyToOne.class).column();
            if(queryResult.containsKey(oneTableIdName.toUpperCase())){
                String sql=String.format(sqlTemp
                        ,new QFilter(fieldClass,dbname).lines()
                        ,PojoManage.getTable(fieldClass,dbname)
                        ,PojoManage.getIdString(fieldClass,dbname));
                Object sqlValue=queryResult.get(oneTableIdName.toUpperCase());
                String completeSql= CreateSql.getCompleteSql(sql,sqlValue);
                SqlOperation sqlOperation = new SqlOperation(conn[0],dbname,isFullMap);
                List<?> list = sqlOperation.autoPackageToList(fieldClass,sql,sqlValue);
                if(list!=null&&!list.isEmpty()){
                    Object manyObj=list.get(0);
                    FieldUtils.setValue(result,field,manyObj);
                }
            }
            return;
        }

        /*
            一对一属性设置
            1.该属性应被@OneToOne注解标注
            2.可以根据干属性的到关联表的表名和关联字段名
            3.查询出关联记录，将本表对象设置给查询对象的对应属性，然后将设置后的查询对象设置给本表对象的对应属性
         */
        if(field.isAnnotationPresent(OneToOne.class)){
            String thisTableId= PojoManage.getIdString(entityClass,dbname);
            if(queryResult.containsKey(thisTableId.toUpperCase())){
                String toTableName=PojoManage.getTable(fieldClass,dbname);
                String joinColumn=field.getAnnotation(OneToOne.class).joinColumn();
                SqlOperation sqlOperation = new SqlOperation(conn[0],dbname,isFullMap);
                sqlOperation.setFullMap(false);
                List<?> list = sqlOperation.autoPackageToList(
                          fieldClass
                        , String.format(sqlTemp
                                , new QFilter(fieldClass,dbname).lines()
                                , toTableName
                                , joinColumn)
                        , queryResult.get(thisTableId.toUpperCase()));
                if(list!=null&&!list.isEmpty()){
                    Object obj=list.get(0);
                    List<Field> annFields = ClassUtils.getFieldByAnnotation(fieldClass, OneToOne.class)
                            .stream().filter(f -> entityClass == f.getType()).collect(Collectors.toList());
                    for (Field annField : annFields) {
                        FieldUtils.setValue(obj,annField,result);
                    }
                    FieldUtils.setValue(result,field,obj);
                }
            }
            return ;
        }

        if(field.isAnnotationPresent(ManyToMany.class)){
            String thisIdName=PojoManage.getIdString(entityClass,dbname);
            if(queryResult.containsKey(thisIdName.toUpperCase())){
                ManyToMany manyToMany=field.getAnnotation(ManyToMany.class);
                String joinTable=manyToMany.joinTable();
                String joinColumnThis=manyToMany.joinColumnThis();
                String joinColumnTo=manyToMany.joinColumnTo();
                SqlOperation sqlOperation = new SqlOperation(conn[0],dbname,isFullMap);
                Class<?> fieldGenericType = FieldUtils.getGenericType(field)[0];
                Field toTableIdField = PojoManage.getIdField(fieldGenericType);
                List<?> toIds = sqlOperation.autoPackageToList(
                          String.class
                        , String.format(sqlTemp, "`"+joinColumnTo+"`", joinTable, joinColumnThis)
                        , queryResult.get(thisIdName.toUpperCase()))
                        .stream()
                        .map(str->JavaConversion.strToBasic(str,toTableIdField.getType()))
                        .collect(Collectors.toList());
                if(!toIds.isEmpty()){
                    String querySql="SELECT %s FROM `%s` WHERE `%s` IN (%s)";
                    sqlOperation.setFullMap(false);
                    List<?> list = sqlOperation.autoPackageToList(
                            fieldGenericType
                            , String.format(querySql
                                    , new QFilter(fieldGenericType,dbname).lines()
                                    , PojoManage.getTable(fieldGenericType, dbname)
                                    , PojoManage.getIdString(fieldGenericType, dbname)
                                    , BaseUtils.strCopy("?", toIds.size(), ","))
                            , toIds.toArray());
                    if(list.isEmpty()){
                        if(List.class.isAssignableFrom(fieldClass)){
                            FieldUtils.setValue(result,field,list);
                        }else if(Set.class.isAssignableFrom(fieldClass)){
                            FieldUtils.setValue(result,field,new HashSet<>(list));
                        }
                    }else{
                        List<Field> annFieldsTo = ClassUtils.getFieldByAnnotation(fieldGenericType, ManyToMany.class)
                                .stream().filter(f->FieldUtils.getGenericType(f)[0]==entityClass).collect(Collectors.toList());
                        for (Object toObj : list) {
                            for (Field annf : annFieldsTo) {
                                ManyToMany manyTo=annf.getAnnotation(ManyToMany.class);
                                String joinTableTo=manyTo.joinTable();
                                String joinColumnToTo=manyTo.joinColumnTo();
                                String joinColumnThisTo=manyTo.joinColumnThis();
                                sqlOperation.setFullMap(true);
                                List<?> toIdsTo = sqlOperation.autoPackageToList(
                                        String.class
                                        , String.format(sqlTemp
                                                , "`" + joinColumnToTo + "`"
                                                , joinTableTo
                                                , joinColumnThisTo)
                                        , FieldUtils.getValue(toObj, PojoManage.getIdField(fieldGenericType)))
                                        .stream()
                                        .map(str -> JavaConversion.strToBasic(str, PojoManage.getIdField(entityClass).getType()))
                                        .collect(Collectors.toList());
                                if(!toIdsTo.isEmpty()){
                                    sqlOperation.setFullMap(false);
                                    List<?> listTo = sqlOperation.autoPackageToList(
                                            entityClass
                                            , String.format(querySql
                                                    , new QFilter(entityClass,dbname).lines()
                                                    , PojoManage.getTable(entityClass, dbname)
                                                    , PojoManage.getIdString(entityClass, dbname)
                                                    , BaseUtils.strCopy("?", toIdsTo.size(), ","))
                                            , fieldGenericType
                                            , toIdsTo.toArray());
                                    if(List.class.isAssignableFrom(annf.getType())){
                                        FieldUtils.setValue(toObj,annf,listTo);
                                    }else if(Set.class.isAssignableFrom(annf.getType())){
                                        FieldUtils.setValue(toObj,annf,new HashSet<>(listTo));
                                    }
                                }
                            }
                        }
                        if(List.class.isAssignableFrom(fieldClass)){
                            FieldUtils.setValue(result,field,list);
                        }else if(Set.class.isAssignableFrom(fieldClass)){
                            FieldUtils.setValue(result,field,new HashSet<>(list));
                        }
                    }
                }
            }
            return ;
        }
    }

}
