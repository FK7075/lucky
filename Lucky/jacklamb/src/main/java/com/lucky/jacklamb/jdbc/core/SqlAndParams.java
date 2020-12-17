package com.lucky.jacklamb.jdbc.core;

import com.lucky.jacklamb.exception.LuckySqlGrammarMistakesException;
import com.lucky.utils.reflect.MethodUtils;
import com.lucky.utils.regula.Regular;

import java.lang.reflect.Method;
import java.util.*;

import static com.lucky.utils.regula.Regular.*;

public class SqlAndParams {

    /**
     * ?s <=> XX LIKE YY%
     */
    private final String START = "?s";
    /**
     * ?e <=> XX LIKE %YY
     */
    private final String END = "?e";
    /**
     * ?c <=> XX LIKE %YY%
     */
    private final String CONTAIN = "?c";
    /**
     * ?C <=> (?,?,?,?)
     */
    private final String In = "?C";
    /**
     * ?D <=> 动态SQL
     */
    private final String DYSQL = "?D";

    String precompileSql;

    Object[] params;

    public SqlAndParams() {
    }

    public SqlAndParams(String haveNumSql, Object[] params) {
        init(null, haveNumSql, params);
    }

    public SqlAndParams(Method method, String haveNumSql, Object[] params) {
        init(method, haveNumSql, params);
    }

    public void init(Method method, String haveNumSql, Object[] params) {
        simplePlaceholderProcess(method, haveNumSql, params);
        complexPlaceholderProcess(method, params);
        sortOut();
    }

    /**
     * 将包含@:X,?num,?Dnum的SQL占位符还原为?和？D,并且重新生成符合该顺序的参数列表
     *
     * @param method     Mapper接口方法
     * @param haveNumSql 待处理的Lucky预编译SQL
     * @param params     原始参数列表
     */
    private void simplePlaceholderProcess(Method method, String haveNumSql, Object[] params) {
        List<String> placeholder = Regular.getArrayByExpression(haveNumSql, Regular.SQL_PLACEHOLDER);
        precompileSql = haveNumSql.replaceAll(Regular.SIMPLE_SQL_PLACEHOLDER, "?");
        List<String> sqlDyNums = Regular.getArrayByExpression(haveNumSql, SQL_DY_NUN);
        sqlDyNums.stream().forEach(s -> precompileSql = precompileSql.replace(s, s.substring(0, 2)));
        this.params = new Object[placeholder.size()];
        if (method == null) {
            if (!Regular.getArrayByExpression(haveNumSql, $SQL).isEmpty()) {
                throw new LuckySqlGrammarMistakesException("当前DB方法不是Mapper接口方法，所以不支持\"@:name\"格式的预编译SQL!");
            }
            for (int i = 0, j = placeholder.size(); i < j; i++) {
                if (Regular.check(placeholder.get(i), NUMSQL)) {
                    int pindex = Integer.parseInt(placeholder.get(i).substring(1));
                    if (pindex < 1 || pindex > params.length) {
                        throw new LuckySqlGrammarMistakesException("不在参数长度限定范围[1," + params.length + "]的SQL参数\"?" + pindex + "\",错误位置:" + method);
                    }
                    this.params[i] = params[pindex - 1];
                } else if (Regular.check(placeholder.get(i), SQL_DY_NUN)) {
                    int pindex = Integer.parseInt(placeholder.get(i).substring(2));
                    if (pindex < 1 || pindex > params.length) {
                        throw new LuckySqlGrammarMistakesException("不在参数长度限定范围[1," + params.length + "]的SQL参数\"" + placeholder.get(i) + "\",错误位置:" + method);
                    }
                    this.params[i] = params[pindex - 1];
                } else {
                    this.params[i] = params[i];
                }
            }
        } else {
            Map<String, Object> methodParamsNV = MethodUtils.getMethodParamsNV(method, params);
            for (int i = 0, j = placeholder.size(); i < j; i++) {
                if (Regular.check(placeholder.get(i), NUMSQL)) {
                    int pindex = Integer.parseInt(placeholder.get(i).substring(1));
                    if (pindex < 1 || pindex > params.length) {
                        throw new LuckySqlGrammarMistakesException("不在参数长度限定范围[1," + params.length + "]的SQL参数\"?" + pindex + "\",错误位置:" + method);
                    }
                    this.params[i] = params[pindex - 1];
                } else if (Regular.check(placeholder.get(i), $SQL)) {
                    String pname = placeholder.get(i).substring(2);
                    if (!methodParamsNV.containsKey(pname)) {
                        throw new LuckySqlGrammarMistakesException("方法参数列表中不存在的SQL参数\"@:" + pname + "\",错误位置:" + method);
                    }
                    this.params[i] = methodParamsNV.get(pname);
                } else if (Regular.check(placeholder.get(i), SQL_DY_NUN)) {
                    int pindex = Integer.parseInt(placeholder.get(i).substring(2));
                    if (pindex < 1 || pindex > params.length) {
                        throw new LuckySqlGrammarMistakesException("不在参数长度限定范围[1," + params.length + "]的SQL参数\"" + placeholder.get(i) + "\",错误位置:" + method);
                    }
                    this.params[i] = params[pindex - 1];
                } else {
                    this.params[i] = params[i];
                }
            }
        }
    }

    /*最后处理，处理预编译Sql中的特殊参数[?s,?e,?c,?C,?D]
        ---------------------------------------------------------------------------
        ?e  : EndingWith    ->LIKE %param      以param结尾
            SELECT * FROM table WHERE name LIKE ?e       Params["Jack"]
                                 |
                                 V
            SELECT * FROM table WHERE name LIKE ?        Params["%Jack"]
        ----------------------------------------------------------------------------
        ?s  : StartingWith  ->LIKE param%      以param开头
        ?c  : Containing    ->LIKE %param%     包含param
        -----------------------------------------------------------------------------
        ?C  : In            ->In [Collection]  条件范围为Collection集合
            SELECT * FROM table WHERE age IN ?C           Params[Collection[1,2,3,4]]
                                 |
                                 V
            SELECT * FROM table WHERE age IN (?,?,?,?)    Params[1,2,3,4]
        -----------------------------------------------------------------------------
        ?D	: DySql         -> DynamicSqlWrapper 动态SQL生成规则
            SELECT * FROM table ?D				   Params[(map)->{if(map.get("name")==null){return new SP("WHERE name>?",map.get("name"))}else{return new SP()}}]
                                 |
                                 V
            SELECT * FROM table  OR
            SELECT * FROM table WHERE name=?              Params["jack"]
            complex
     */

    /**
     * 将带符号的Lucky预编译SQL（?c,?e,?C,?D）参数翻译为普通的预编译SQL，并且重新整理参数列表
     *
     * @param method       Mapper接口方法
     * @param sourceParams 原始参数列表
     */
    public void complexPlaceholderProcess(Method method, Object[] sourceParams) {
        if (precompileSql.contains(In) || precompileSql.contains(START) ||
                precompileSql.contains(END) || precompileSql.contains(CONTAIN) ||
                precompileSql.contains(DYSQL)) {
            Map<Integer, Integer> indexMap = getIndexMap();
            dealithW_D();
            indexMap = getIndexMap();
            dealithW_s(indexMap);
            dealithW_e(indexMap);
            dealithW_c(indexMap);
            precompileSql = precompileSql.replaceAll("\\?l", "?");
            indexMap = getIndexMap();
            dealithW_C(indexMap);
        }
    }

    //处理?s,同dealithW_c
    public void dealithW_s(Map<Integer, Integer> indexMap) {
        List<Integer> escIndex = new ArrayList<>();
        setQuestionMarkIndex(precompileSql, escIndex, START);
        precompileSql = precompileSql.replaceAll("\\?s", "?l");
        for (Integer index : escIndex) {
            int idx = indexMap.get(index);
            params[idx] = params[idx] + "%";
        }
    }

    //处理?e,同dealithW_s
    public void dealithW_e(Map<Integer, Integer> indexMap) {
        List<Integer> escIndex = new ArrayList<>();
        setQuestionMarkIndex(precompileSql, escIndex, END);
        precompileSql = precompileSql.replaceAll("\\?e", "?l");
        for (Integer index : escIndex) {
            int idx = indexMap.get(index);
            params[idx] = "%" + params[idx];
        }
    }

    /*处理?c
        indexMap:每个?在SQL中的位置(KEY)与(=>)?对应参数在参数数组中的位置(VALUE)所组成的Map<Integer,Integer>
        1.得到所有?c在SQl中出现的位置所组成的集合(escIndex)
        2.使用?l替换掉所有的?c
        3.遍历(escIndex),使用(indexMap)将escIndex翻译为对应参数数组的位置(idx)
        4.使用(idx)拿到并修改原参数 ==>params[idx]="%"+params[idx]+"%";
     */
    public void dealithW_c(Map<Integer, Integer> indexMap) {
        List<Integer> escIndex = new ArrayList<>();
        setQuestionMarkIndex(precompileSql, escIndex, CONTAIN);
        precompileSql = precompileSql.replaceAll("\\?c", "?l");
        for (Integer index : escIndex) {
            int idx = indexMap.get(index);
            params[idx] = "%" + params[idx] + "%";
        }
    }

    /*
        解析动态sql(?D)
     */
    public void dealithW_D() {
        while (true) {
            if (!precompileSql.contains("?D")) {
                break;
            }
            Map<Integer, Integer> indexMap = getIndexMap();
            List<Integer> escIndex = new ArrayList<>();
            setQuestionMarkIndex(precompileSql, escIndex, DYSQL);
            DynamicSqlWrapper dySqlWar;
            int idx = 0;
            try {
                idx = indexMap.get(escIndex.get(0));
                dySqlWar = (DynamicSqlWrapper) params[idx];
            } catch (Exception e) {
                throw new RuntimeException("SQL操作符 \"?D\" 对应的参数类型必须为" + DynamicSqlWrapper.class + "！错误的类型:" + params[idx].getClass(), e);
            }
            SplicingRules sp = new SplicingRules();
            dySqlWar.dySql(sp);
            precompileSql = precompileSql.replaceFirst("\\?D", sp.getpSql());
            int dySize = sp.getParams().size();
            Object[] newParams = new Object[params.length + dySize - 1];
            for (int i = 0, j = newParams.length; i < j; i++) {
                if (i < idx) {
                    newParams[i] = params[i];
                } else if (i >= idx && i < (idx + dySize)) {
                    newParams[i] = sp.getParams().get(i - idx);
                } else {
                    newParams[i] = params[i - dySize + 1];
                }
            }
            params = newParams;
        }
    }

    /*处理?C  (IN操作类似的范围限定操作)
        indexMap:?在SQL中的位置(KEY)与(=>)?对应参数在参数数组中的位置(VALUE)
         1.得到所有?C在SQl中出现的位置所组成的集合(escIndex)
         2.如果(escIndex)中没有元素则结束，否则进入第三步
         3.遍历(escIndex),将使用(indexMap)将escIndex翻译为对应参数数组的位置(idx)
         4.使用(idx)拿到原参数，并将其强转为Collection类型，如果出现异常会抛出一个RuntimeException
         5.将Collection转化为数组(collArray),转化后将(idx)和(collArray)添加到Map(inCollectionMap)中 ==> inCollectionMap.put(idx,collArray)
         6.得到一个包含(collArray)长度个数的? ==>  (?,?,?,?) ,然后使用这个替换掉原SQL中的第一个?CONTAIN
         7.生产新的参数数组Object[] newParams；
            新数组长度=原数组长度+原数组中所有集合参数的元素个数和-集合参数的个数
            newParamsSize=params.length+START(inCollectionMap.value.length)-inCollectionMap.size
         8.使用原数组和inCollectionMap数组填充新数组
         9.将新数组赋值给原数组

         ------------------------------------------------------------------------------------------------------------------------------
          SQL : SELECT * FROM table WHERE f0=? AND f1=? AND f2 IN ?C AND f3=? OR f4=? AND f5 IN ?CONTAIN f6 NOT IN ?CONTAIN
          Params               [0,1,2C,3,4,5C,6C]    l=7

          inCollectionMap                            lm=3
                2 -> [a,b,c,d]                       l2=4
                5 -> [x,y,z]                         l5=3
                6 -> [t,q,m,j]                       l6=4
          Object[] newParams = new Object[l+l2+l5+l6-lm]
                                 |
                                 V
          NEW-SQL    : SELECT * FROM table WHERE f0=? AND f1=? AND f2 IN (?,?,?,?) AND f3=? OR f4=? AND f5 IN (?,?,?) f6 NOT IN (?,?,?,?)
          NEW-Params :        [0,1,a,b,c,d,3,4,x,y,z,t,q,m,j]
     */
    public void dealithW_C(Map<Integer, Integer> indexMap) {
        List<Integer> escIndex = new ArrayList<>();
        setQuestionMarkIndex(precompileSql, escIndex, In);
        if (!escIndex.isEmpty()) {
            Map<Integer, Object[]> inCollectionMap = new HashMap<>();
            Collection collections;
            int newArraySize = 0;
            Object[] collArray;
            for (Integer index : escIndex) {
                int idx = indexMap.get(index);
                try {
                    collections = (Collection) params[idx];
                } catch (Exception e) {
                    throw new RuntimeException("SQL操作符 \"?C\" 对应的参数类型必须为Collection的子类！错误的类型:" + params[idx].getClass(), e);
                }
                int collSize = collections.size();
                newArraySize += collSize;
                collArray = new Object[collSize];
                precompileSql = precompileSql.replaceFirst("\\?C", getMark(collSize));
                int i = 0;
                for (Object coll : collections) {
                    collArray[i] = coll;
                    i++;
                }
                inCollectionMap.put(idx, collArray);
            }
            newArraySize = params.length + newArraySize - inCollectionMap.size();
            Object[] newParams = new Object[newArraySize];
            int p = 0;
            for (int i = 0; i < params.length; i++) {
                if (inCollectionMap.containsKey(i)) {
                    for (Object obj : inCollectionMap.get(i)) {
                        newParams[p] = obj;
                        p++;
                    }
                } else {
                    newParams[p] = params[i];
                    p++;
                }
            }
            params = newParams;
        }
    }

    public Map<Integer, Integer> getIndexMap() {
        List<Integer> indexs = new ArrayList<>();
        setQuestionMarkIndex(precompileSql, indexs, "?");
        Map<Integer, Integer> indexMap = new HashMap<>();
        for (int i = 0; i < indexs.size(); i++) {
            indexMap.put(indexs.get(i), i);
        }
        return indexMap;
    }

    public String getMark(int num) {
        StringBuilder s = new StringBuilder(" (");
        for (int i = 0; i < num; i++) {
            s.append("?,");
        }
        return s.substring(0, s.length() - 1) + ") ";
    }

    //得到每个？在SQL中的位置，组成一个集合
    public void setQuestionMarkIndex(String sql, List<Integer> indexs, String target) {
        if (!sql.contains(target)) {
            return;
        }
        String sqlCopy = sql;
        indexs.add(sqlCopy.indexOf(target));
        if ("?".equals(target)) {
            sqlCopy = sqlCopy.replaceFirst("\\?", "@");
        } else if (START.equals(target)) {
            sqlCopy = sqlCopy.replaceFirst("\\?s", "@L");
        } else if (END.equals(target)) {
            sqlCopy = sqlCopy.replaceFirst("\\?e", "@L");
        } else if (CONTAIN.equals(target)) {
            sqlCopy = sqlCopy.replaceFirst("\\?c", "@L");
        } else if (In.equals(target)) {
            sqlCopy = sqlCopy.replaceFirst("\\?C", "@L");
        } else if (DYSQL.equals(target)) {
            sqlCopy = sqlCopy.replaceFirst("\\?D", "@L");
        } else {
            throw new RuntimeException("错误的参数：" + target + ",正确的参数为[?,?s,?e,?c,?C,?D]");
        }
        setQuestionMarkIndex(sqlCopy, indexs, target);
    }

    private void sortOut() {
        int count = Regular.getArrayByExpression(precompileSql, "\\?").size();
        if (count == params.length)
            return;
        Object[] newParams = new Object[count];
        for (int i = 0; i < count; i++) {
            newParams[i] = params[i];
        }
        params = newParams;
    }
}
