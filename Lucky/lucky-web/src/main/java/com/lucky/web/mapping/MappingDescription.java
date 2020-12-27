package com.lucky.web.mapping;

import com.lucky.utils.proxy.CglibProxy;
import com.lucky.utils.reflect.AnnotationUtils;
import com.lucky.web.annotation.Description;

import java.lang.reflect.Method;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/27 上午2:49
 */
public class MappingDescription {

    private String description;
    private String author;
    private String version;
    private String comment;

    public MappingDescription(Class<?> aClass,Method method){
        aClass=CglibProxy.isAgent(aClass)?aClass.getSuperclass():aClass;
        if(AnnotationUtils.strengthenIsExist(aClass,Description.class)){
            Description description=AnnotationUtils.strengthenGet(aClass, Description.class).get(0);
            this.description=description.desc();
            author=description.author();
            version=description.version();
            comment=description.comment();
        }else{
            this.description=method.getName();
            author="--";
            version="v1.0.0.0";
            comment="作者还没有写描述！";
        }
        if(AnnotationUtils.strengthenIsExist(method, Description.class)){
            Description description=AnnotationUtils.strengthenGet(method, Description.class).get(0);
            String desc = description.desc();
            String author = description.author();
            String version = description.version();
            String comment = description.comment();
            if(!"".equals(desc)){
                this.description=desc;
            }
            if(!"".equals(author)){
                this.author=author;
            }
            if(!"".equals(version)){
                this.version=version;
            }
            if(!"".equals(comment)){
                this.comment=comment;
            }
        }
    }
}
