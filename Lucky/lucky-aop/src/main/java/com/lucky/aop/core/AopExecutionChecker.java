package com.lucky.aop.core;

import com.lucky.framework.container.Module;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * AOP执行检验器
 * P:{包检验表达式}
 * C:{N:[类名检验表达式],I:[IOC_ID校验表达式],T:[IOC_TYPE校验表达式],A:[是否被注解]}
 * M:{N:[方法名校验表达式],A:[是否被注解],AC:[访问修饰符],O:[要增强的继承自Object对象的方法]}
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/29 下午10:59
 */
public class AopExecutionChecker {

    /** 环绕增强的执行节点*/
    private AopPoint point;
    /** 包定位表达式*/
    private String[] packages;
    /** 类定位表达式: IOC_ID*/
    private String[] iocIds;
    /** 类定位表达式: IOC_TYPE*/
    private String[] types;
    /** 类定位表达式: 类名*/
    private String[] classNames;
    /** 类定位表达式: 是否被其中的注解标注*/
    private Class<? extends Annotation>[] classAnnotations;
    /** 方法定位表达式: 访问修饰*/
    private Set<Integer> accesses;
    /** 方法定位表达式: 方法名*/
    private String[] methodNames;
    /** 方法定位表达式: 是否被其中的注解标注*/
    private Class<? extends Annotation>[] methodAnnotations;
    /** 需要增强的继承自Object类的方法*/
    private Set<String> objectMethod;


    /**
     * 类检验
     * @param bean 待检验Module实例
     * @return
     */
    public boolean classExamine(Module bean){
        Class<?> originalType = bean.getOriginalType();
        if(!packageExamine(originalType.getName())){
            return false;
        }
        if(!classInfoExamine(originalType,bean.getId(),bean.getType())){
            return false;
        }
        return true;
    }

    /**
     * 方法检验
     * @param method 待检验的Method
     * @return
     */
    public boolean methodExamine(Method method){
        if(!methodAccessExamine(method)){
            return false;
        }
        if(!(methodNameExamine(method)||methodAccessExamine(method))){
            return false;
        }
        return true;
    }


    //包检验
    private boolean packageExamine(String fullClassName){
        for (String pack : packages) {
            if("*".equals(pack)){
                return true;
            }
            if(fullClassName.startsWith(pack)){
                return true;
            }
        }
        return false;
    }

    //类名、IOC_ID、IOC_TYPE检验
    private boolean classInfoExamine(Class<?> aClass,String iocId,String iocType){
        return examine(classNames,aClass.getSimpleName())||examine(iocIds,iocId)||examine(types,iocType)||classAnnotationExamine(aClass);
    }

    //类是否被注解
    private boolean classAnnotationExamine(Class<?> aClass){
        return AnnotationUtils.isExistOrByArray(aClass,classAnnotations);
    }

    //类是否被注解
    private boolean methodAnnotationExamine(Method method){
        return AnnotationUtils.isExistOrByArray(method,classAnnotations);
    }

    //方法的访问修饰符检验
    private boolean methodAccessExamine(Method method){
        return accesses.contains(method.getModifiers());
    }

    private boolean methodNameExamine(Method method){
        String name=method.getName();
        for (String methodName : methodNames) {
            //带方法参数的写法
            if(methodName.contains("(")&&methodName.endsWith(")")){
                String withParamMethodName = MethodUtils.getWithParamMethodName(method);
                if(methodName.startsWith("!")){
                    if(!methodName.substring(1).equals(withParamMethodName)){
                        return true;
                    }
                }
                if(methodName.equals(withParamMethodName)){
                    return true;
                }
            }

            //方法名要以固定格式结尾
            if(methodName.startsWith("*")){
                if(name.endsWith(methodName.substring(1))){
                    return true;
                }
            }
            //方法名要排除以固定格式结尾
            if(methodName.startsWith("!*")){
                if(!name.endsWith(methodName.substring(2))){
                    return true;
                }
            }

            //方法名要以固定格式开头
            if(methodName.endsWith("*")){
                if(name.startsWith(methodName.substring(0,methodName.length()-1))){
                    return true;
                }
            }
            //方法名要排除以固定格式开头
            if(methodName.endsWith("*")&&methodName.startsWith("!")){
                if(!name.startsWith(methodName.substring(1,methodName.length()-1))){
                    return true;
                }
            }

            //方法名
            if(name.equals(methodName)){
                return true;
            }
            //排除某个方法名
            if(methodName.startsWith("!")){
                if(!name.equals(methodName.substring(1))){
                    return true;
                }
            }
        }
        return false;
    }



    private boolean examine(String[] array,String info){
        for (String str : array) {
            if(str.equals(info)){
                return true;
            }
        }
        return false;
    }

}
