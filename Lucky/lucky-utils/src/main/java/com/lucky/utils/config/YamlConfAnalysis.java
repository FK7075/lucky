package com.lucky.utils.config;

import com.lucky.utils.jexl.JexlEngineUtil;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
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
    private final JexlEngineUtil jexlEngineUtil;

    public YamlConfAnalysis(Reader yamlReader){
        yaml=new Yaml();
        map= new LinkedHashMap<>();
        Iterator<Object> iterator = yaml.loadAll(yamlReader).iterator();
        while (iterator.hasNext()){
             map = (LinkedHashMap) iterator.next();
        }
        jexlEngineUtil=new JexlEngineUtil(map);
    }

    public YamlConfAnalysis(List<BufferedReader> yamlReaders){
        yaml=new Yaml();
        map= new LinkedHashMap<>();
        for (BufferedReader yamlReader : yamlReaders) {
            Iterator<Object> iterator = yaml.loadAll(yamlReader).iterator();
            while (iterator.hasNext()){
                Map<String,Object> currMap = (LinkedHashMap) iterator.next();
                for(Map.Entry<String,Object> entry:currMap.entrySet()){
                    map.put(entry.getKey(),entry.getValue());
                }
            }
        }
        jexlEngineUtil=new JexlEngineUtil(map);
    }

    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public Object getObject(Object key) {
        return jexlEngineUtil.getProperties(key);
    }

    public Object getProperties(String prefix){
        return getObject("${"+prefix+"}");
    }
}
