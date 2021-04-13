package org.luckyframework.environment;

/**
 * 环境变量存储器
 * @author fk
 * @version 1.0
 * @date 2021/3/24 0024 11:16
 */
public interface Environment {

    String getSystemEvn(String key);

    String getSystemProperty(String key);

    Object getDefaultConfigFileProperty(String key);

    Object getExternalConfigFileProperty(String key);

    Object parsing(Object expression);

    default Object getProperty(String key){
        Object property = getExternalConfigFileProperty(key);
        if(property == null){
            property = getDefaultConfigFileProperty(key);
            if(property == null){
                property = getSystemProperty(key);
                if(property == null){
                    property = getSystemEvn(key);
                }
            }
        }
        return property;
    }
}
