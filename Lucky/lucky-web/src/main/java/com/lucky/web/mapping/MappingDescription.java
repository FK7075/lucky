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

    private String module;
    private String description;
    private String author;
    private String version;
    private String comment;

    public String getModule() {
        return module;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public String getVersion() {
        return version;
    }

    public String getComment() {
        return comment;
    }

    public MappingDescription(Class<?> aClass, Method method){
        aClass=CglibProxy.isAgent(aClass)?aClass.getSuperclass():aClass;
        if(AnnotationUtils.strengthenIsExist(aClass,Description.class)){
            Description description=AnnotationUtils.strengthenGet(aClass, Description.class).get(0);
            String desc = description.desc();
            String author = description.author();
            String version = description.version();
            String comment = description.comment();
            String module = description.module();
            this.description="".equals(desc)?"暂未命名":desc;
            this.author="".equals(author)?"--":author;
            this.version="".equals(version)?"v1.0.0.0":version;
            this.comment="".equals(comment)?"此功能暂无描述！":author;
            this.module="".equals(module)?"System Module":module;
        }else{
            this.description="暂未命名";
            author="--";
            version="v1.0.0.0";
            comment="此功能暂无描述！";
            module="System Module";
        }
        if(AnnotationUtils.strengthenIsExist(method, Description.class)){
            Description description=AnnotationUtils.strengthenGet(method, Description.class).get(0);
            String desc = description.desc();
            String author = description.author();
            String version = description.version();
            String comment = description.comment();
            String module = description.module();
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
            if(!"".equals(module)){
                this.module=module;
            }
        }
    }
}
