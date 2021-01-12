package com.lucky.data.services;

import com.lucky.framework.annotation.Autowired;
import com.lucky.jacklamb.mapper.LuckyMapper;
import com.lucky.jacklamb.querybuilder.Page;
import com.lucky.jacklamb.querybuilder.QueryBuilder;
import com.lucky.jacklamb.querybuilder.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/2 10:04
 */
public class BaseService<M extends LuckyMapper<E>,E> {

    protected static final Logger log = LoggerFactory.getLogger(BaseService.class);

    @Autowired
    protected M luckyMapper;

    /**
     * 根据ID得到一条记录
     *
     * @param id 主键id
     * @return E
     */
    public E selectById(Object id){
        return luckyMapper.selectById(id);
    }

    /**
     * 根据ID删除一条记录
     *
     * @param id 主键id
     * @return
     */
    public int deleteById(Object id){
        return luckyMapper.deleteById(id);
    }

    /**
     * 对象删除
     *
     * @param pojo
     * @return
     */
    public int delete(E pojo){
        return luckyMapper.delete(pojo);
    }

    /**
     * 查询操作
     *
     * @param pojo 包含查询信息的pojo对象
     * @return 对应类型的查询结果
     */
    public E select(E pojo){
        return luckyMapper.select(pojo);
    }

    /**
     * 查询操作
     *
     * @param pojo 包含查询信息的pojo对象
     * @return 对应类型集合的查询结果
     */
    public List<E> selectList(E pojo){
        return luckyMapper.selectList(pojo);
    }

    /**
     * 查询Class对应表的所有数据
     *
     * @return
     */
    public List<E> selectList(){
        return luckyMapper.selectList();
    }

    /**
     * 更新操作
     *
     * @param pojo 包含更新信息的pojo对象
     * @return
     */
    public int update(E pojo){
        return luckyMapper.update(pojo);
    }

    /**
     * 添加操作，并自动获取自增ID
     *
     * @param pojo 包含添加信息的pojo对象
     * @return
     */
    public int insertAutoId(E pojo){
       return luckyMapper.insertAutoId(pojo);
    }

    /**
     * 添加操作
     *
     * @param pojo 包含添加信息的pojo对象
     * @return
     */
    public int insert(E pojo){
        return luckyMapper.insertAutoId(pojo);
    }

    /**
     * 批量添加操作
     *
     * @param pojos 包含添加信息的List[pojo]集合
     * @return
     */
    public int batchInsert(Collection<E> pojos){
        return luckyMapper.batchInsert(pojos);
    }

    /**
     * 批量更新
     *
     * @param pojos 包含添加信息的List[pojo]集合
     * @return
     */
    public int batchUpdate(Collection<E> pojos){
        return luckyMapper.batchUpdate(pojos);
    }

    /**
     * 批量删除
     *
     * @param pojos 包含添加信息的List[pojo]集合
     * @return
     */
    public int batchDelete(Collection<E> pojos){
        return luckyMapper.batchDelete(pojos);
    }

    /**
     * 分页操作
     *
     * @param pojo 包含查询信息的pojo对象
     * @param page 页码
     * @param rows 每页显示的条数
     * @return
     */
    public Page<E> selectLimit(E pojo, int page, int rows){
        Page<E> pg=new Page<>();
        List<E> list = luckyMapper.selectLimit(pojo, page, rows);
        pg.setData(list);
        pg.setCurrPage(page);
        pg.setRows(rows);
        int count = count(pojo);
        int totalPage=count%rows==0?count/rows:count/rows+1;
        pg.setTotalNum(count);
        pg.setTotalPage(totalPage);
        return pg;
    }

    /**
     * 基于全表的分页查询
     * @param page 页码
     * @param rows 每页显示的条数
     * @return
     */
    public Page<E> limit(int page, int rows){
        return luckyMapper.limit(page, rows);
    }

    /**
     * QueryBuilder查询模式
     *
     * @param queryBuilder QueryBuilder对象
     * @return
     */
    public List<E> query(QueryBuilder queryBuilder){
        return luckyMapper.query(queryBuilder);
    }

    /**
     * Count操作
     *
     * @param pojo 包含查询信息的pojo对象
     * @return
     */
    public int count(E pojo){
        return luckyMapper.count(pojo);
    }

    /**
     * 总数统计
     *
     * @return
     */
    public int count(){
        return luckyMapper.count();
    }

    /**
     * 根据ID批量删除数据
     *
     * @param ids
     * @return
     */
    public int deleteByIdIn(List<?> ids){
        return luckyMapper.deleteByIdIn(ids);
    }

    /**
     * 根据ID批量查询数据
     *
     * @param ids
     * @return
     */
    public List<E> selectByIdIn(List<?> ids){
        return luckyMapper.selectByIdIn(ids);
    }

    /**
     * Translator方式的查询,返回集合
     *
     * @param tr Translator对象
     * @return 集合
     */
    public List<E> select(Translator tr){
        return (List<E>) luckyMapper.select(tr);
    }

    /**
     * Translator方式的查·询，返回对象
     *
     * @param tr Translator对象
     * @return 对象
     */
    public E selectOne(Translator tr){
        return (E) luckyMapper.selectOne(tr);
    }

    /**
     * Translator方式的更新
     *
     * @param tr Translator对象
     * @return 受影响的行数
     */
    public int update(Translator tr){
        return luckyMapper.update(tr);
    }

    /**
     * Translator方式的删除
     *
     * @param tr Translator对象
     * @return 受影响的行数
     */
    public int delete(Translator tr){
        return luckyMapper.delete(tr);
    }

}
