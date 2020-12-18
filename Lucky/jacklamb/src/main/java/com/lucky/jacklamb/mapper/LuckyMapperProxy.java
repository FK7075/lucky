package com.lucky.jacklamb.mapper;

import com.lucky.jacklamb.exception.NotFoundInterfacesGenericException;
import com.lucky.jacklamb.jdbc.core.abstcore.SqlCore;
import com.lucky.jacklamb.mapper.scan.MapperXmlScan;
import com.lucky.utils.proxy.CglibProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class LuckyMapperProxy {

    private static final Logger log = LoggerFactory.getLogger(LuckyMapperProxy.class);
    private static final Map<String,Map<String,String>> allMapperSql= MapperXmlScan.getAllMapperSql();
    private SqlCore sqlCore;
    private Map<String, String> sqlMap;
    private Class<?> LuckyMapperGeneric;

    public LuckyMapperProxy(SqlCore sql) {
        sqlCore = sql;
        sqlMap=new HashMap<>();
    }

    /**
     * 递归方法：找到Mapper接口继承的LuckyMapper接口中的泛型类型，如果Mapper接口没有继承LuckyMapper则返回null。(结束！)
     * 1.判断Mapper是否为LuckyMapper接口的子接口，如果不是直接返回null。
     * 2.使用Class的getGenericInterfaces()方法得到当前接口的所有ParameterizedType。
     * 3.判断ParameterizedType[]是否含有LuckyMapper,如果已经包含，侧直接返回该ParameterizedType对应的泛型。(结束！)
     * 4.不包含，说明Mapper没有直接继承LuckyMapper,需要使用用此流程操作Mapper接口的直接父接口
     * ----4.1.使用Class的getInterfaces()方法得到Mapper接口所有的直接父接口的Class
     * ----4.2.递归操作这些父接口的Class
     * ----4.3.判断递归结果,如果不为null，则表示当前操作的Class已经直接继承的LuckyMapper接口，返回递归结果。(结束！)
     * @param mapperClass
     * @return
     */
    private Class<?> getLuckyMapperGeneric(Class<?> mapperClass){
        if(LuckyMapper.class.isAssignableFrom(mapperClass)){
            Type[] genericInterfaces = mapperClass.getGenericInterfaces();
            String typeName;
            for (Type anInterface : genericInterfaces) {
                typeName=anInterface.getTypeName();
                typeName=typeName.indexOf("<")!=-1?typeName.substring(0,typeName.indexOf("<")):typeName;
                if(typeName.equals(LuckyMapper.class.getTypeName())){
                    ParameterizedType interfacePtype;
                    try{
                        interfacePtype = (ParameterizedType)anInterface;
                    }catch (ClassCastException e){
                        throw new NotFoundInterfacesGenericException(mapperClass,e);
                    }
                    return (Class<?>) interfacePtype.getActualTypeArguments()[0];
                }
            }
            Class<?>[] interfaces = mapperClass.getInterfaces();
            for (Class<?> anInterface : interfaces) {
                Class<?> result=getLuckyMapperGeneric(anInterface);
                if(result!=null)
                    return result;
            }
            return null;
        }else{
            return null;
        }
    }

    /**
     * 返回接口的代理对象
     *
     * @param mapperClass 接口的Class
     * @return T
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IOException
     */
    public <T> T getMapperProxyObject(Class<T> mapperClass) throws InstantiationException, IllegalAccessException, IOException {
        LuckyMapperGeneric=getLuckyMapperGeneric(mapperClass);
        initXmlSql(mapperClass);
        return CglibProxy.getCglibProxyObject(mapperClass,new LuckyMapperMethodInterceptor(LuckyMapperGeneric,sqlCore,sqlMap));
    }

    private void initXmlSql(Class<?> mapperClass){
        if(mapperClass==LuckyMapper.class)
            return;
        String mapperClassName = mapperClass.getName();
        if(allMapperSql.containsKey(mapperClassName)){
            Map<String, String> map = allMapperSql.get(mapperClassName);
            for(Map.Entry<String,String> en:map.entrySet()){
                if(!sqlMap.containsKey(en.getKey()))
                    sqlMap.put(en.getKey(),en.getValue());
            }
        }
        Class<?>[] interfaces = mapperClass.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            initXmlSql(anInterface);
        }
    }

}
