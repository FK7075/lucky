package com.lucky.jacklamb.annotation.mapper;

import java.lang.annotation.*;

/**
 * 
 * 对象方式的万能查询(单表查询、多表的连接查询、分页、排序、模糊、指定返回列和隐藏返回列...)<br>
 * queryBuilder() 是否使用queryBuilder模式(默认不使用)
 * join() 连接方式<br><br>
 * limit() 是否开启分页查询，开启后会默认参数列表中的最后两个int参数作为分页条件<br>
 * sort() 设置排序格式为：   {"field:1","field2:-1"},1为升序-1为降序<br>
 * sResults:(showResults)设置所要查询的字段<br>
 * hResults:(hiddenResults)设置要隐藏的字段<br>注：sResults、hResults不可同时是使用
 * 模糊查询：模糊查询的字段参数需要手动传入，参数类型限定为String和List[Strirng],并且使用时需要@Like的标记
 * expression() 连接表达式，解释如下<br>
 * 	1.强链接  最前面的两个表之间必须使用强连接<br>
 * 		tab1-->tab2 [-->]   
 * 		<br>表示tab2表与左边相邻的tab1使用主外键作为连接条件进行连接<br>
 * 	2.弱连接<br>
 *      tab1-->tab2--tab3 [--] <br>
 *      表示tab3表跳过左边相邻的tab2与tab1表使用主外键作为连接条件进行连接<br>
 *  3.指定连接<br>
 *  	tab1-->tab2--tab3&lt2&gttab4 [&ltn&gt] <br>
 *  	表示tab4从左边相邻的位置起，向左跳过2张表与tab1使用主外键作为连接条件进行连接<br>
 *  	--><==>&lt0&gt  --<==>&lt1&gt<br>
 *  当expression缺省时，底层会以如下方式自动生成一个expression<br>(queryObjTab1-->queryObjTab2-->...-->queryObjTabn)
 * @author fk-7075
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Query {
	boolean queryBuilder() default false;
	String expression() default "";
	boolean limit() default false;
	String[] sort() default {};
	String[] sResults() default {};
	String[] hResults() default {};
}
