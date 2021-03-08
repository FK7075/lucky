package com.lucky.data.aspect;

import com.lucky.aop.core.AopChain;
import com.lucky.aop.core.InjectionAopPoint;
import com.lucky.aop.core.TargetMethodSignature;
import com.lucky.data.annotation.DS;
import com.lucky.data.annotation.Mapper;
import com.lucky.framework.AutoScanApplicationContext;
import com.lucky.framework.annotation.Component;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCore;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCoreFactory;
import com.lucky.jacklamb.mapper.LuckyMapper;
import com.lucky.utils.base.Assert;
import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解式的动态数据源切换扩展
 * @author fk
 * @version 1.0
 * @date 2021/1/12 0012 9:12
 */
@Component
public class DynamicDataSourcePoint extends InjectionAopPoint {

    public DynamicDataSourcePoint(){
        setPriority(0.5);
    }

    private final ThreadLocal<Map<Field,Object>> thlSourceFieldKVMap=new ThreadLocal();

    private static final Logger log= LoggerFactory.getLogger("c.l.d.aspect.DynamicDataSourcePoint");

    private static final Method[] objectMethod=Object.class.getDeclaredMethods();

    @Override
    public Object  proceed(AopChain chain) throws Throwable {
        TargetMethodSignature targetMethodSignature = tlTargetMethodSignature.get();
        Map<String, Object> nameMap = targetMethodSignature.getNameMap();
        Method method=targetMethodSignature.getCurrMethod();
        Class<?> targetClass=targetMethodSignature.getTargetClass();
        DS ds;
        //当前方法上存在@DS
        if(AnnotationUtils.strengthenIsExist(method,DS.class)){
            ds=AnnotationUtils.strengthenGet(method, DS.class).get(0);
            return run(targetMethodSignature,chain,ds);
        }

        //没有被@DS注解标注的继承自Object的方法不执行代理
        for (Method m : objectMethod) {
            if(m.equals(method)){
                return chain.proceed();
            }
        }

        //当前方法上不存在@DS，但是当前方法的类上存在@DS，同样执行代理
        if(AnnotationUtils.strengthenIsExist(targetClass, DS.class)){
            ds=AnnotationUtils.strengthenGet(targetClass, DS.class).get(0);
            return run(targetMethodSignature,chain,ds);
        }
        //当前方法和类上都不存在@DS，执行原始逻辑
        return chain.proceed();
    }

    private Object run(TargetMethodSignature tms,AopChain chain,DS ds) throws Throwable {
        String dbParam=ds.dbParam();
        String initDBName=ds.value();
        Map<String, Object> nameMap=tms.getNameMap();
        //@DS没有配置value也没有配置dbParam,不执行代理
        if(Assert.isBlankString(dbParam)&&Assert.isBlankString(initDBName)){
            return chain.proceed();
        }
        //@DS上即配置了dbParam又配了value，执行代理，数据源的选取取决于dbParam所对应的参数
        if(!Assert.isBlankString(dbParam)&&!Assert.isBlankString(initDBName)){
            Object dbParamValue = nameMap.get(dbParam);
            String newDBName=(dbParamValue instanceof String)?dbParamValue.toString():initDBName;
            return dataSourceSwitch(chain,tms,newDBName);
        }
        //没有配dbParam，但是有配value，则使用value值对应的数据源做代理
        if(Assert.isBlankString(dbParam)){
            return dataSourceSwitch(chain,tms,initDBName);
        }
        //没有配value，但是配了dbParam,执行代理，数据源的选取取决于dbParam所对应的参数
        Object dbParamValue = nameMap.get(dbParam);
        if(dbParamValue instanceof String){
            return dataSourceSwitch(chain,tms,dbParamValue.toString());
        }
        return chain.proceed();
    }

    @Override
    public boolean pointCutMethod(Class<?> currClass, Method currMethod) {
        return AnnotationUtils.strengthenIsExist(currClass,DS.class)
                || AnnotationUtils.strengthenIsExist(currMethod, DS.class);
    }

    @Override
    public boolean pointCutClass(Class<?> currClass) {
        return AnnotationUtils.strengthenIsExist(currClass,DS.class)
                || !ClassUtils.getMethodByStrengthenAnnotation(currClass, DS.class).isEmpty();
    }

    //数据源切换流程
    public Object dataSourceSwitch(AopChain chain, TargetMethodSignature tms,String ds){
        //数据备份
        backup(tms);
        //切换数据源
        dataSourceSwitch(tms,ds);
        try {
            //执行真实方法
            Object result = chain.proceed();
            return result;
        }catch (Throwable e){
            String ERR="数据源切换成功! 执行原始逻辑时出错！。错误位置：\""+tms.getCurrMethod()+"\"";
            log.error(ERR,e);
            throw new DataSourceSwitchException(e,ERR);
        }finally {
            //数据还原
            recovery(tms);
        }
    }

    //数据源替换，将所真实对象的所有属性(包含所有属性的嵌套属性)的SqlCore替换为拥有新数据源的SqlCore，并将真实对象的引用指向该对象
    private void dataSourceSwitch(TargetMethodSignature tms,String newDBName){
        Map<Field,Object> oldFieldMapperMap=thlSourceFieldKVMap.get();
        Object aspectObject=tms.getAspectObject();
        SqlCore newSqlCore=SqlCoreFactory.createSqlCore(newDBName);
        for(Map.Entry<Field,Object> entry:oldFieldMapperMap.entrySet()) {
            Field field = entry.getKey();
            Class<?> fieldClass = CglibProxy.getOriginalType(entry.getValue().getClass());
            if (SqlCore.class.isAssignableFrom(fieldClass)) {
                FieldUtils.setValue(aspectObject, field, newSqlCore);
                continue;
            }
            if (fieldClass.isAnnotationPresent(Mapper.class)) {
                FieldUtils.setValue(aspectObject, field, newSqlCore.getMapper(fieldClass));
                continue;
            }
            FieldUtils.setValue(aspectObject, entry.getKey(), dbChange(newSqlCore, entry.getValue()));
        }
    }

    //获取一个拥有全新数据源的克隆来替代原来真实对象的原有属性
    private Object dbChange(SqlCore newSqlCore, Object fieldObject){
        Class<?> fClass = fieldObject.getClass();
        Object copyFieldObj = copy(fieldObject);
        Field[] allFields = ClassUtils.getAllFields(CglibProxy.getOriginalType(fClass));
        for (Field field : allFields) {
            Class<?> fieldClass = CglibProxy.getOriginalType(field.getType());

            if(SqlCore.class.isAssignableFrom(fieldClass)){
                FieldUtils.setValue(copyFieldObj,field,newSqlCore);
                continue;
            }

            if(fieldClass.isAnnotationPresent(Mapper.class)){
                FieldUtils.setValue(copyFieldObj,field,newSqlCore.getMapper(fieldClass));
                continue;
            }

            if(fieldClass== LuckyMapper.class){
                Class<?> luckyMapperClass = AutoScanApplicationContext.create().getBeanByField(fClass, fieldClass).getClass();
                luckyMapperClass=CglibProxy.getOriginalType(luckyMapperClass);
                FieldUtils.setValue(copyFieldObj,field,newSqlCore.getMapper(luckyMapperClass));
                continue;
            }

            //存在IOC容器中，但存在无参构造的组件
            if(AutoScanApplicationContext.create().isIOCClass(fieldClass)){
                try {
                    Object fieldValue=FieldUtils.getValue(copyFieldObj,field);
                    FieldUtils.setValue(copyFieldObj,field, dbChange(newSqlCore,fieldValue));
                }catch (Exception ignored){

                }

            }
        }
        return copyFieldObj;
    }

    //拷贝对象得到一个新的对象
    private Object copy(Object oldObj){
        Class<?> objClass = oldObj.getClass();
        Field[] allFields = ClassUtils.getAllFields(objClass);
        Object newObj = ClassUtils.newObject(objClass);
        for (Field field : allFields) {
            if(!Modifier.isFinal(field.getModifiers()))
                FieldUtils.setValue(newObj,field,FieldUtils.getValue(oldObj,field));
        }
        return newObj;
    }

    //代理开始前的数据备份，将真实类的原始属性保存在全局变量中
    private void backup(TargetMethodSignature tms){
        Map<Field,Object> oldFieldMapperMap=new HashMap<>();
        Class<?> targetClass=tms.getTargetClass();
        Object aspectObject=tms.getAspectObject();
        Field[] allFields= ClassUtils.getAllFields(targetClass);
        for (Field field : allFields) {
            Class<?> type = field.getType();
            Object fieldValue = FieldUtils.getValue(aspectObject, field);
            //&&!CglibProxy.isAgent(fieldValue.getClass())
            if((AutoScanApplicationContext.create().isIOCClass(type)
                    || SqlCore.class.isAssignableFrom(type))){
                oldFieldMapperMap.put(field, fieldValue);
            }
        }
        thlSourceFieldKVMap.set(oldFieldMapperMap);
    }

    //代理结束后的恢复
    private void recovery(TargetMethodSignature tms){
        Map<Field,Object> oldFieldMapperMap=thlSourceFieldKVMap.get();
        Object aspectObject=tms.getAspectObject();
        for(Map.Entry<Field,Object> entry:oldFieldMapperMap.entrySet()){
            FieldUtils.setValue(aspectObject,entry.getKey(),entry.getValue());
        }
    }
}
