package com.lucky.utils.jexl;

import com.lucky.utils.base.Assert;
import com.lucky.utils.exception.GetConfigurationInfoException;
import com.lucky.utils.regula.Regular;
import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;

import java.util.List;
import java.util.Map;

/**
 * @author fk
 * @version 1.0
 * @date 2021/1/22 0022 11:26
 */
public class JexlEngineUtil {

    private static final String PREFIX="$LUCKY";
    private final Engine engine=new Engine();
    private final JexlContext context=new MapContext();

    public JexlEngineUtil(Map<String,Object> confMap){
        Assert.notNull(confMap,"confMap is null!");
        context.set(PREFIX,confMap);
    }

    public Object getProperties(Object key) {
        if(key instanceof String){
            String prefix= (String) key;
            List<String> expression = Regular.getArrayByExpression(prefix, Regular.$_$);
            if(Assert.isEmptyCollection(expression)){
                return prefix;
            }
            if(expression.size()==1&&isExpression(prefix)){
                return getValue(prefix);
            }
            for (String $exp : expression) {
                Object value = getValue($exp);
                Assert.notNull(value,"在解析表达式`"+prefix+"`时出现异常：`"+$exp+"`的解析值为NULL");
                if(value instanceof String){
                    prefix=prefix.replace($exp,(String) getProperties(value.toString()));
                }else{
                    prefix=prefix.replace($exp,value.toString());
                }
            }
            return prefix;
        }
        return key;
    }

    private Object getValue(String $prefix){
        String  prefix=$prefix.substring(2,$prefix.length()-1).trim();
        StringBuilder sb=new StringBuilder(PREFIX);
        String[] split = prefix.split("\\.");
        for (String key : split) {
            if(key.contains("-")){
                sb.append(".'").append(key).append("'");
            }else{
                sb.append(".").append(key);
            }

        }
        Object evaluate=null;
        try {
            evaluate = engine.createExpression(sb.toString()).evaluate(context);
        }catch (Exception e){
            throw new GetConfigurationInfoException($prefix,e);
        }

        if(evaluate instanceof String){
            try {
                return getProperties(evaluate.toString());
            } catch (GetConfigurationInfoException e) {
                e.printStackTrace();
            }
        }
        return evaluate;
    }

    public static boolean isExpression(String prefix){
        prefix=prefix.trim();
        return prefix.startsWith("${")&&prefix.endsWith("}");
    }

}
