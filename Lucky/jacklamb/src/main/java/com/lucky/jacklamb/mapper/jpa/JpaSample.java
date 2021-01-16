package com.lucky.jacklamb.mapper.jpa;

import com.google.gson.reflect.TypeToken;
import com.lucky.jacklamb.jdbc.potable.PojoManage;
import com.lucky.utils.base.BaseUtils;
import com.lucky.utils.file.Resources;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.regula.Regular;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/6/29 12:48 下午
 *
 * findBy语法解析流程
 * eg: findByNameStartingWithAndAgeAndPriceBetween
 * 1.去掉前缀
 *     NameStartingWithAndAgeAndPriceBetween
 * 2.将find表达式中包含的实体属性全部替换为 "="
 *     =StartingWithAnd=And=Between
 * 3.使用编码表[find-coding.json]将2得到的表达式编码（以SQL运算符的长度降序为顺序）
 *     =@21@01=@01=@05
 * 4.分离SQL运算符
 *    [@21@01,@01,@05]
 * 5.分离出2中的原始SQL运算符
 *    =StartingWithAnd=And=Between --> [StartingWithAnd,And,Between]
 * 6.使用"="替换原始表达式中的[StartingWithAnd,And,Between]部分
 *    NameStartingWithAndAgeAndPriceBetween -->Name=Age=Price
 * 7.分离出参数终结符
 *    Name=Age=Price --> [Name,Age,Price]
 * 8.重新组合参数终结符和SQL运算符
 *   [Name,Age,Price] + [@21@01,@01,@05] = [Name,@21@01,Age,@01,Price,@05]
 * 9.解码运算
 *   [Name,@21@01,Age,@01,Price,@05] -->  ...WHERE name LIKE ?s AND age AND price BETWEEN ? AND ?
 * ----------------------------------------------------------------------------------------------------------------------------------
 *
 * 一.findByNameStartingWithAndAgeAndPriceBetween -> NameStartingWithAndAgeAndPriceBetween
 * 二.NameStartingWithAndAgeAndPriceBetween -> =StartingWithAnd=And=Between
 *     1.   opeList       : StartingWithAnd=And=Between   -> =@21@01=@01=@05 -> [@21@01,@01,@05]
 *     2.1. opeSourceList : =StartingWithAnd=And=Between  -> [StartingWithAnd,And,Between]
 *     2.2. varList       : NameStartingWithAndAgeAndPriceBetween+[StartingWithAnd,And,Between] -> Name=Age=Price= ->[Name,Age,Price]
 * 三.[@21@01,@01,@05] + [Name,Age,Price] -> [Name,@21@01,Age,@01,Price,@05] -> ...WHERE name LIKE ?s AND age AND price BETWEEN ? AND ?
 *
 * ----------------------------------------------------------------------------------------------------------------------------------
 */
public class JpaSample {

    private final String FIND_BY="^((find|get|read)([\\s\\S]*)By)([\\s\\S]*)$";

    private final String REG = "\\@\\d\\d";

    private Class<?> pojoClass;

    /**
     * 运算符转换规则
     */
    private static Map<String, String> operationMap;

    /**
     * 运算符解析规则
     */
    private static Map<String, String> parsingMap;

    /**
     * 所有SQL运算符按照字符长度倒序排序后的集合
     */
    private static List<String> lengthSortSqlOpe;

    /**
     * 当前所有终结符按照字符长度倒序排序后的集合
     */
    private List<String> lengthSortField;

    /**
     * 查询语句的前缀
     */
    private StringBuilder selectSql;

    /**
     * 实体类属性名(首字母大写)与表字段所组成的Map
     */
    private Map<String, String> fieldColumnMap;

    static {
        TypeToken type = new TypeToken<Map<String, String>>(){};
        operationMap= (Map<String, String>) Resources.fromJson(type,"/jacklamb-conf/jpa-coding.json");
        parsingMap=(Map<String, String>) Resources.fromJson(type,"/jacklamb-conf/jpa-decoding.json");
        lengthSortSqlOpe=new ArrayList<>(operationMap.keySet());
        Collections.sort(lengthSortSqlOpe, new SortByLengthComparator());
    }

    public JpaSample(Class<?> pojoClass,String dbname) {
        this.pojoClass=pojoClass;
        selectSql = new StringBuilder("SELECT @:ResultColumn FROM ").append(PojoManage.getTable(pojoClass,dbname));
        fieldColumnMap = new HashMap<>();
        Field[] fields = ClassUtils.getAllFields(pojoClass);
        for (Field field : fields) {
            fieldColumnMap.put(BaseUtils.capitalizeTheFirstLetter(field.getName()), PojoManage.getTableField(dbname,field));
        }
        lengthSortField = new ArrayList<>(fieldColumnMap.keySet());
        Collections.sort(lengthSortField, new SortByLengthComparator());
    }

    /**
     * 获取findBy表达式中的返回列
     * @param jpaSample
     * @return
     */
    public String getSelectResultColumn(String jpaSample){
        jpaSample=jpaSample.substring(0,jpaSample.indexOf("By"));
        if(jpaSample.startsWith("find")||jpaSample.startsWith("read")){
            jpaSample= jpaSample.substring(4);
        }else{
            jpaSample= jpaSample.substring(3);
        }
        if("".equals(jpaSample)||"All".equals(jpaSample)){
            return "*";
        }
        StringBuilder result=new StringBuilder();
        for (String field : lengthSortField) {
            if(jpaSample.contains(field)){
                result.append(fieldColumnMap.get(field)).append(",");
                jpaSample=jpaSample.replaceAll(field,"");
            }
        }
        if(!"".equals(jpaSample)){
            throw new RuntimeException("不符合JPA规范的查询方法命名!无法识别的「\"结果列(ResultColumn)\"」： \"" + jpaSample+"\"");
        }
        String resultStr=result.toString();
        return resultStr.endsWith(",")?resultStr.substring(0,resultStr.length()-1):resultStr;
    }

    /**
     * 将JPA的findBy表达式解析为SQL语句
     *
     * @param jpaSample JPA表达式[findByLastnameAndFirstname
     *                  readByLastnameAndFirstname
     *                  getByLastnameAndFirstname]
     * @return
     */
    public String sampleToSql(String jpaSample) throws IllegalJPAExpressionException {
        /*
            一.findByNameStartingWithAndAgeAndPriceBetween -> NameStartingWithAndAgeAndPriceBetween
            二.NameStartingWithAndAgeAndPriceBetween -> =StartingWithAnd=And=Between
                1.   opeList       : StartingWithAnd=And=Between   -> =@21@01=@01=@05 -> [@21@01,@01,@05]
                2.1. opeSourceList : =StartingWithAnd=And=Between  -> [StartingWithAnd,And,Between]
                2.2. varList       : NameStartingWithAndAgeAndPriceBetween+[StartingWithAnd,And,Between] -> Name=Age=Price= ->[Name,Age,Price]
            三.[@21@01,@01,@05] + [Name,Age,Price] -> [Name,@21@01,Age,@01,Price,@05] -> ...WHERE name LIKE ?s AND age AND price BETWEEN ? AND ?
         */
        if(!Regular.check(jpaSample,FIND_BY)){
            throw new IllegalJPAExpressionException("不符合JPA规范的查询方法命名：" + jpaSample);
        }
        String jpaCopy=jpaSample;
        //去掉findBy前缀
        jpaSample = jpaSample.substring(jpaSample.indexOf("By")+2);
        String copy=jpaSample;

        for (String field : lengthSortField) {
            jpaSample = jpaSample.replaceAll(field, "=");
        }
        String copy1=jpaSample;
        for (String ope : lengthSortSqlOpe) {
            copy1=copy1.replaceAll(ope,operationMap.get(ope));
        }
        List<String> opeList=Arrays.asList(copy1.split("=")).stream().
                filter(a -> a != null && !"".equals(a)).collect(Collectors.toList());

        List<String> opeSourceList=Arrays.asList(jpaSample.split("=")).stream().
                filter(a -> a != null && !"".equals(a)).collect(Collectors.toList());
        List<String> copyOpeList = new ArrayList<>(opeSourceList);
        Collections.sort(copyOpeList, new SortByLengthComparator());
        jpaSample=copy;
        for (String ope : copyOpeList) {
            jpaSample=jpaSample.replaceAll(ope,"=");
        }
        List<String> varList=Arrays.asList(jpaSample.split("="))
                .stream().filter(a -> a != null && !"".equals(a)).collect(Collectors.toList());
        List<String> varOpeSortList = getVarOpeSortList(varList, opeList, jpaSample);
        try {
            joint(varOpeSortList);
            return selectSql.toString().replaceAll("@:ResultColumn",getSelectResultColumn(jpaCopy));
        } catch (SQLException e) {
            throw new RuntimeException("错误的Mapper方法[不符合Jpa规范]==>"+jpaCopy,e);
        }

    }

    public List<String> getVarOpeSortList(List<String> varList, List<String> opeList, String jpaSample) {
        List<String> varOpeSortList = new ArrayList<>();
        boolean varStatr = jpaSample.startsWith(varList.get(0));
        boolean varEnd = jpaSample.endsWith(varList.get(varList.size() - 1));
        int varSize = varList.size();
        int opeSize = opeList.size();
        if (varStatr && varEnd) {//以终结符开头，以终结符结尾
            for (int i = 0; i < opeSize; i++) {
                varOpeSortList.add(varList.get(i));
                varOpeSortList.add(opeList.get(i));
            }
            varOpeSortList.add(varList.get(varSize - 1));
        } else if (varStatr && !varEnd) {//以终结符开头，以运算符结尾
            for (int i = 0; i < opeSize; i++) {
                varOpeSortList.add(varList.get(i));
                varOpeSortList.add(opeList.get(i));
            }
        } else if (!varStatr && varEnd) {//以运算符开头，以终结符结尾
            for (int i = 0; i < varSize; i++) {
                varOpeSortList.add(opeList.get(i));
                varOpeSortList.add(varList.get(i));
            }
        } else {//以运算符开头，以运算符结尾
            for (int i = 0; i < varSize; i++) {
                varOpeSortList.add(opeList.get(i));
                varOpeSortList.add(varList.get(i));
            }
            varOpeSortList.add(opeList.get(opeSize - 1));
        }
        return varOpeSortList;
    }

    public void joint(List<String> varOpeSortList) throws SQLException {
        if(varOpeSortList.isEmpty())
            return;
        if(!varOpeSortList.get(0).startsWith("@13")){
            selectSql.append(" WHERE ");
        }
        for (int i = 0; i < varOpeSortList.size(); i++) {
            String currStr=varOpeSortList.get(i);
            if(currStr.startsWith("@")){//运算符
                currStr=currStr.replaceAll("@","_@").substring(1);
                String[] opeArray=currStr.split("_");
                for (int j = 0; j < opeArray.length; j++) {
                    if(opeArray[0].equals("@28")&&i!=0){
                        selectSql.append(parsingMap.get(opeArray[j]).replaceAll("@X",fieldColumnMap.get(varOpeSortList.get(i-1))));
                        continue;
                    }
                    selectSql.append(parsingMap.get(opeArray[j]));
                }
            }else {//终结符
                if (varOpeSortList.size() == 1 ||
                        (i == varOpeSortList.size() - 1 && (varOpeSortList.get(i - 1).endsWith("@01") || varOpeSortList.get(i - 1).endsWith("@02")))
                        ||
                        (i != varOpeSortList.size() - 1 && (varOpeSortList.get(i + 1).startsWith("@01") || varOpeSortList.get(i + 1).startsWith("@02") || varOpeSortList.get(i + 1).startsWith("@13")))) {
                    if (fieldColumnMap.containsKey(currStr)) {
                        selectSql.append(fieldColumnMap.get(currStr) + " = ? ");
                    } else {
                        throw new SQLException("无法识别的实体属性：\"" + currStr + "\" ,实体类为:" + pojoClass);
                    }
                    //当前为终结符，下一个为Or And OrderBy时 ==>name=?
                    //当前为终结符,且为最后一个操作符，上一个为Or And时 ==>name=?
                } else if (i != varOpeSortList.size() - 1 && varOpeSortList.get(i + 1).startsWith("@28")) {
                    continue;
                } else {
                    if (fieldColumnMap.containsKey(currStr)) {
                        selectSql.append(fieldColumnMap.get(currStr));
                    } else {
                        throw new SQLException("无法识别的实体属性：\"" + currStr + "\" ,实体类为:" + pojoClass);
                    }
                }
            }
        }
    }
}

class SortByLengthComparator implements Comparator<String> {

    @Override
    public int compare(String var1, String var2) {
        if (var1.length() > var2.length()) {
            return -1;
        } else if (var1.length() == var2.length()) {
            return 0;
        } else {
            return 1;
        }
    }

}

