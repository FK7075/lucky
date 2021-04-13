package org.luckyframework.beans.annotation;

import com.lucky.utils.config.YamlConfAnalysis;
import com.lucky.utils.fileload.Resource;
import com.lucky.utils.fileload.resourceimpl.PathMatchingResourcePatternResolver;
import org.luckyframework.exception.LuckyIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PropertySource注解的解析器
 * @author fk
 * @version 1.0
 * @date 2021/3/25 0025 17:40
 */
public class PropertySourceReader {

    private final Logger log = LoggerFactory.getLogger(PropertySourceReader.class);
    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    private final String[] locationPatterns;
    private final String encoding;
    private final boolean ignoreResourceNotFound;

    public PropertySourceReader(PropertySource propertySource){
        this(propertySource.value(), propertySource.encoding(), propertySource.ignoreResourceNotFound());
    }

    public PropertySourceReader(String...locationPattern){
        this(locationPattern,"UTF-8",false);
    }

    public PropertySourceReader(String[] locationPatterns, String encoding, boolean ignoreResourceNotFound){
        this.locationPatterns=locationPatterns;
        this.encoding=encoding;
        this.ignoreResourceNotFound=ignoreResourceNotFound;
    }

    public Map<String,Object> getResourceData(){
        Map<String,Object> data =new ConcurrentHashMap<>(100);
        for (String pattern : locationPatterns) {
            try {
                Resource[] resources = resolver.getResources(pattern);
                for (Resource resource : resources) {
                    String upperCasePath = resource.getURL().toString().toUpperCase();
                    if(upperCasePath.endsWith(".YAML")||upperCasePath.endsWith(".YML")){
                        BufferedReader br =new BufferedReader(new InputStreamReader(resource.getInputStream(),encoding));
                        YamlConfAnalysis yaml =new YamlConfAnalysis(br);
                        data.putAll(yaml.getMap());
                        continue;
                    }
                    if(upperCasePath.endsWith(".PROPERTIES")){
                        Properties p =new Properties();
                        BufferedReader br =new BufferedReader(new InputStreamReader(resource.getInputStream(),encoding));
                        p.load(br);
                        for (Map.Entry<Object,Object> e : p.entrySet()){
                            data.put(e.getKey().toString(),e.getValue());
                        }
                    }
                }
            }catch (IOException e){
                if(ignoreResourceNotFound){
                    log.warn("An exception occurred while loading resource '"+pattern+"'");
                }else{
                    throw new LuckyIOException("An exception occurred while loading the configuration file '["+pattern+"]'",e);
                }
            }
        }
        return data;
    }
}
