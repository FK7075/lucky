package com.lucky.jacklamb.jdbc.core.abstcore;

import com.lucky.jacklamb.querybuilder.Page;
import com.lucky.jacklamb.querybuilder.QueryBuilder;

import java.util.Collection;
import java.util.List;

/**
 * 各个关系型数据库特有的SQl语法对应的对象操作
 * @author DELL
 *
 */
public interface UniqueSqlCore {
	
	/**
	 * 逆向工程生成JavaBean,需要在配置文件中配置classpath(src)的绝对路径和所在包的路径
	 */
	void createJavaBean();
	
	/**
	 * 逆向工程生成JavaBean,需要在配置文件中配置所在包的路径
	 * @param srcPath classpath(src)的绝对路径
	 */
	void createJavaBean(String srcPath);
	
	/**
	 * 逆向工程生成JavaBean,需要在配置文件中配置classpath(src)的绝对路径和所在包的路径
	 * @param tables 指定需要生成JavaBean的表名
	 */
	void createJavaBeanByTable(String... tables);
	
	/**
	 * 逆向工程生成JavaBean,需要在配置文件中配置所在包的路径
	 * @param srcPath classpath(src)的绝对路径
	 * @param tables 指定需要生成JavaBean的表名
	 */
	void createJavaBeanSrc(String srcPath, String... tables);
	
	/**
	 * 启动自动建表机制建表，需要在配置文件中配置需要建表的实体类的包路径
	 */
	void createTable();

	void createTable(Class<?>... tableClasses);


	/**
	 * 分页查询
	 * @param t
	 * 包含查询信息的包装类的对象
	 * @param page
	 * 第一条数据在表中的位置
	 * @param size
	 * 每页的记录数
	 * @return
	 */
	<T> Page<T> getPageList(T t, int page, int size) ;

	/**
	 * 添加数据
	 * @param t 包含添加信息的包装类的对象
	 * @return
	 */
	<T> int insert(T t);

	/**
	 * 添加数据,并为每个对象设置自增主键
	 * @param t
	 * @return
	 */
	<T> int insertSetId(T t);

	/**
	 * 批量保存-数组模式,并为每个对象设置自增主键
	 * @param obj 需要添加到数据库的实体类对象
	 * @return
	 */
	<T> int insertSetIdByArray(Object... obj);

	/**
	 * 批量保存-集合模式
	 * @param collection 要操作的对象所组成的集合
	 * @return false or true
	 */
	<T> int insertByCollection(Collection<T> collection);


	/**
	 * 批量保存-数组模式
	 * @param obj 需要添加到数据库的实体类对象
	 * @return
	 */
	boolean insertByArray(Object... obj);

	void setNextId(Object pojo);

	/**
	 * 对象方式的多表连接操作<br>
	 * 	1.强链接  最前面的两个表之间必须使用强连接<br>
	 * 		tab1-->tab2 [-->]
	 * 		<br>表示tab2表与左边相邻的tab1使用主外键作为连接条件进行连接<br>
	 * 	2.弱连接<br>
	 *      tab1-->tab2--tab3 [--] <br>
	 *      表示tab2表跳过左边相邻的tab1与tab1表使用主外键作为连接条件进行连接<br>
	 *  3.指定连接<br>
	 *  	tab1-->tab2--tab3&lt2&gttab4 [&ltn&gt] <br>
	 *  	表示tab4从左边相邻的位置起，向左跳过2张表与tab1使用主外键作为连接条件进行连接<br>
	 *  	--><==>&lt0&gt  --<==>&lt1&gt<br>
	 *  当expression缺省时，底层会以如下方式自动生成一个expression<br>(queryObjTab1-->queryObjTab2-->...-->queryObjTabn)
	 * @param queryBuilder 查询条件（需要进行连接操作的对象+连接方式+指定返回的列）
	 * @param expression 连接表达式('-->'强连接,'--'弱连接,'&ltn&gt'指定连接)
	 * @param resultClass 用于接受返回值的类的Class
	 * @return
	 */
	<T> List<T> query(QueryBuilder queryBuilder, Class<T> resultClass, String... expression);
	
	/**
	 * 清空缓存
	 */
	void clear();

}
