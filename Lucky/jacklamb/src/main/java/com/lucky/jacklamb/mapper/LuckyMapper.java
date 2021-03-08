package com.lucky.jacklamb.mapper;

import com.lucky.jacklamb.annotation.mapper.*;
import com.lucky.jacklamb.querybuilder.Page;
import com.lucky.jacklamb.querybuilder.QueryBuilder;
import com.lucky.jacklamb.querybuilder.Translator;

import java.io.Reader;
import java.util.Collection;
import java.util.List;

/**
 * 单表操作的Mapper接口模板，可以用来简化Mapper接口开发
 * @param <Entity> 表对应的实体类
 * @author fk-7075
 */
public interface LuckyMapper<Entity> {


    /**
     * 根据ID得到一条记录
     *
     * @param id 主键id
     * @return T
     */
    @Select(byid = true)
    public Entity selectById(Object id);

    /**
     * 根据ID删除一条记录
     *
     * @param id 主键id
     * @return
     */
    @Delete(byid = true)
    public int deleteById(Object id);

    /**
     * 对象删除
     *
     * @param pojo
     * @return
     */
    @Delete
    public int delete(Entity pojo);

    /**
     * 查询操作
     *
     * @param pojo 包含查询信息的pojo对象
     * @return 对应类型的查询结果
     */
    @Select
    public Entity select(Entity pojo);

    /**
     * 查询操作
     *
     * @param pojo 包含查询信息的pojo对象
     * @return 对应类型集合的查询结果
     */
    @Select
    public List<Entity> selectList(Entity pojo);

    /**
     * 查询Class对应表的所有数据
     *
     * @return
     */
    public List<Entity> selectList();

    /**
     * 执行sql脚本(逐行执行)
     * @param sqlScriptReader sql脚本
     */
    public void runScript(Reader sqlScriptReader);

    /**
     *  执行sql脚本(全行执行)
     * @param sqlScript sql脚本
     */
    public void runScriptFullLine(Reader sqlScript);

    /**
     * 更新操作
     *
     * @param pojo 包含更新信息的pojo对象
     * @return
     */
    @Update
    public int update(Entity pojo);

    /**
     * 添加操作，并自动获取自增ID
     *
     * @param pojo 包含添加信息的pojo对象
     * @return
     */
    @Insert(setautoId = true)
    public int insertAutoId(Entity pojo);

    /**
     * 添加操作
     *
     * @param pojo 包含添加信息的pojo对象
     * @return
     */
    @Insert
    public int insert(Entity pojo);

    /**
     * 批量添加操作
     *
     * @param pojos 包含添加信息的List[pojo]集合
     * @return
     */
    @Insert(batch = true)
    public int batchInsert(Collection<Entity> pojos);

    /**
     * 批量更新
     *
     * @param pojos 包含添加信息的List[pojo]集合
     * @return
     */
    @Update(batch = true)
    public int batchUpdate(Collection<Entity> pojos);

    /**
     * 批量删除
     *
     * @param pojos 包含添加信息的List[pojo]集合
     * @return
     */
    @Delete(batch = true)
    public int batchDelete(Collection<Entity> pojos);

    /**
     * 分页操作
     *
     * @param pojo 包含查询信息的pojo对象
     * @param page 页码
     * @param rows 每页显示的条数
     * @return
     */
    @Query(limit = true)
    public List<Entity> selectLimit(Entity pojo, int page, int rows);

    /**
     * 基于全表的分页查询
     * @param page 页码
     * @param rows 每页显示的条数
     * @return
     */
    public Page<Entity> limit(int page, int rows);

    /**
     * QueryBuilder查询模式
     *
     * @param queryBuilder QueryBuilder对象
     * @return
     */
    @Query(queryBuilder = true)
    public List<Entity> query(QueryBuilder queryBuilder);

    /**
     * Count操作
     *
     * @param pojo 包含查询信息的pojo对象
     * @return
     */
    @Count
    public int count(Entity pojo);

    /**
     * 总数统计
     *
     * @return
     */
    public int count();

    /**
     * 创建啊实体对应的数据库表
     */
    public void createTable();

    /**
     * 根据ID批量删除数据
     *
     * @param ids
     * @return
     */
    public int deleteByIdIn(List<?> ids);

    /**
     * 根据ID批量查询数据
     *
     * @param ids
     * @return
     */
    public List<Entity> selectByIdIn(List<?> ids);

    /**
     * Translator方式的查询,返回集合
     *
     * @param tr Translator对象
     * @return 集合
     */
    @QueryTr("SELECT")
    public List<?> select(Translator tr);

    /**
     * Translator方式的查询，返回对象
     *
     * @param tr Translator对象
     * @return 对象
     */
    @QueryTr("SELECT")
    public Object selectOne(Translator tr);

    /**
     * Translator方式的更新
     *
     * @param tr Translator对象
     * @return 受影响的行数
     */
    @QueryTr("UPDATE")
    public int update(Translator tr);

    /**
     * Translator方式的删除
     *
     * @param tr Translator对象
     * @return 受影响的行数
     */
    @QueryTr("DELETE")
    public int delete(Translator tr);
}
