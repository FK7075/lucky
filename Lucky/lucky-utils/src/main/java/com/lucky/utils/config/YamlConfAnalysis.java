package com.lucky.utils.config;

import com.lucky.utils.base.Assert;
import com.lucky.utils.exception.GetConfigurationInfoException;
import com.lucky.utils.regula.Regular;
import org.yaml.snakeyaml.Yaml;

import java.io.Reader;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/16 上午4:42
 */
public class YamlConfAnalysis implements ConfAnalysis{

    private Yaml yaml;
    private LinkedHashMap<String,Object> map;

    public YamlConfAnalysis(Reader yamlReader){
        yaml=new Yaml();
        map= new LinkedHashMap<>();
        Iterator<Object> iterator = yaml.loadAll(yamlReader).iterator();
        while (iterator.hasNext()){
             map = (LinkedHashMap) iterator.next();
        }
        context.set(PREFIX,map);
    }

    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public Object getObject(String prefix) {
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
                prefix=prefix.replace($exp,(String)getObject(value.toString()));
            }else{
                prefix=prefix.replace($exp,value.toString());
            }
        }
        return prefix;
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
                return getObject(evaluate.toString());
            } catch (GetConfigurationInfoException e) {
                e.printStackTrace();
            }
        }
        return evaluate;
    }

}
