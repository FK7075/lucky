package com.lucky.jacklamb.jdbc.core.abstcore;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;

/**
 * 关系型数据库通用的对象操作
 *
 * @author fk-7075
 */
public interface GeneralObjectCore {

    void runScript(Reader sqlScript) throws IOException;

    void runScriptFullLine(Reader sqlScript) throws IOException;

    /**
     * ID查询
     *
     * @param c  包装类的Class
     * @param id
     * @return
     */
    <T> T getOne(Class<T> c, Object id);

    /**
     * 对象方式获得单个对象
     *
     * @param t
     * @return
     */
    <T> T getObject(T t);

    /**
     * id删除
     *
     * @param clazz 所操作类
     * @param id    id值
     * @return
     */
    int delete(Class<?> clazz, Object id);

    /**
     * 批量ID删除
     *
     * @param clazz 要操作表对应类的Class
     * @param ids   要删除的id所组成的集合
     * @return
     */
    int deleteByIdIn(Class<?> clazz, Object... ids);

    /**
     * 批量ID查询
     *
     * @param clazz 要操作表对应类的Class
     * @param ids   要删除的id所组成的集合
     * @return
     */
    <T> List<T> getByIdIn(Class<T> clazz, Object... ids);

    /**
     * 对象查询
     *
     * @param t 对象
     * @return
     */
    <T> List<T> getList(T t);

    /**
     * 得到该Class对应表的所有数据
     *
     * @param t
     * @return
     */
    <T> List<T> getList(Class<T> t);


    /**
     * 统计总数
     *
     * @param t
     * @return
     */
    <T> int count(T t);

    /**
     * 数据统计
     *
     * @param t
     * @return
     */
    <T> int count(Class<T> t);


    /**
     * 删除数据
     *
     * @param t 包含删除信息的包装类的对象
     * @return
     */
    <T> int delete(T t);

    /**
     * 修改数据
     * @param t 包含修改信息的包装类的对象
     * @param conditions WHERE后的条件字段
     * @param <T>
     * @return
     */
    <T> int updateRow(T t, String... conditions);

    /**
     * 批量删除-数组模式
     *
     * @param obj 包含删除信息的对象数组
     * @return
     */
    int deleteByArray(Object... obj);


    /**
     * 批量更新-数组模式
     *
     * @param obj 包含更新信息的对象数组
     * @return
     */
    int updateByArray(Object... obj);

    /**
     * 批量删除-集合模式
     *
     * @param collection 要操作的对象所组成的集合
     * @return false or true
     */
    <T> int deleteByCollection(Collection<T> collection);


    /**
     * 批量更新-集合模式
     *
     * @param collection 要操作的对象所组成的集合
     * @return false or true
     */
    <T> int updateByCollection(Collection<T> collection);


    /**
     * 添加操作
     *
     * @param pojo 实体类对象
     * @return
     */
    <T> int insert(T pojo);

}
