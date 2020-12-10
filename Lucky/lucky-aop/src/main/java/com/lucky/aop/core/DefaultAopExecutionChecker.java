package com.lucky.aop.core;

import com.lucky.aop.exception.PositionExpressionException;
import com.lucky.framework.container.Module;
import com.lucky.framework.uitls.reflect.AnnotationUtils;
import com.lucky.framework.uitls.reflect.ClassUtils;
import com.lucky.framework.uitls.reflect.MethodUtils;
import com.lucky.framework.uitls.regula.Regular;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AOP执行检验器
 * P:{包检验表达式}
 * C:{N[类名检验表达式],I[IOC_ID校验表达式],T[IOC_TYPE校验表达式],A[是否被注解]}
 * M:{N[方法名校验表达式],A[是否被注解],AC[访问修饰符],O[要增强的继承自Object对象的方法]}
 * P:{*}C:{N[HelloController,MyService]}M:{AC[*],N[show,query(int,String)]}
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/29 下午10:59
 */
public class DefaultAopExecutionChecker extends AopExecutionChecker{

    private static final String $P="P:\\{([\\s\\S]*?)\\}";
    private static final String $C="C:\\{([\\s\\S]*?)\\}";
    private static final String $M="M:\\{([\\s\\S]*?)\\}";

    private static final String $N="N\\[([\\s\\S]*?)\\]";
    private static final String $I="I\\[([\\s\\S]*?)\\]";
    private static final String $A="A\\[([\\s\\S]*?)\\]";
    private static final String $T="T\\[([\\s\\S]*?)\\]";
    private static final String $O="O\\[([\\s\\S]*?)\\]";
    private static final String $AC="AC\\[([\\s\\S]*?)\\]";

    private String pe;
    private String ce;
    private String me;

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

    public DefaultAopExecutionChecker(){}

    @Override
    public void setPositionExpression(String positionExpression) {
        super.setPositionExpression(positionExpression);
        if(!formatVerification()){
            throw new PositionExpressionException(aspectMethod,positionExpression);
        }
        init();
    }

    /**
     * 定位表达式的格式校验
     * @return
     */
    private boolean formatVerification(){
        List<String> pl = Regular.getArrayByExpression(positionExpression, $P);
        List<String> cl = Regular.getArrayByExpression(positionExpression, $C);
        List<String> ml = Regular.getArrayByExpression(positionExpression, $M);
        int p=pl.size(),c=cl.size(),m=ml.size();
        if(!(((p==0||p==1)&&(c==0||c==1)&&(m==0||m==1)))){
            return false;
        }
        String ps=p==0?"P:{*}":pl.get(0);
        String cs=c==0?"C:{N[*]}":cl.get(0);
        String ms=m==0?"M:{N[*],AC[0,1,2,4]}":ml.get(0);
        boolean ok=fvc(cs)&&fvm(ms);
        if(ok){
            pe=ps;ce=cs;me=ms;
            return true;
        }
        return false;
    }

    private boolean fvc(String positionExpression){
        int n = Regular.getArrayByExpression(positionExpression, $N).size();
        int i = Regular.getArrayByExpression(positionExpression, $I).size();
        int t = Regular.getArrayByExpression(positionExpression, $T).size();
        int a = Regular.getArrayByExpression(positionExpression, $A).size();
        if(!(((n==0||n==1)&&(i==0||i==1)&&(t==0||t==1)&&(a==0||a==1)))){
            return false;
        }
        return true;
    }

    private boolean fvm(String positionExpression){
        int n = Regular.getArrayByExpression(positionExpression, $N).size();
        int o = Regular.getArrayByExpression(positionExpression, $O).size();
        int ac = Regular.getArrayByExpression(positionExpression, $AC).size();
        int a = Regular.getArrayByExpression(positionExpression, $A).size();
        if(!(((n==0||n==1)&&(o==0||o==1)&&(ac==0||ac==1)&&(a==0||a==1)))){
            return false;
        }
        return true;
    }

    /**
     * 定位表达式解析
     */
    public void init(){
        packages=pe.substring(3,pe.length()-1).split(",");
        List<String> cnl = Regular.getArrayByExpression(ce, $N);
        if(cnl.size()==0){
            classNames=new String[0];
        }else {
            String cns = cnl.get(0);
            classNames=cns.substring(2,cns.length()-1).split(",");
        }

        List<String> cil = Regular.getArrayByExpression(ce, $I);
        if(cil.size()==0){
            iocIds=new String[0];
        }else{
            String cis = cil.get(0);
            iocIds=cis.substring(2,cis.length()-1).split(",");
        }

        List<String> ctl = Regular.getArrayByExpression(ce, $T);
        if(ctl.size()==0){
            types=new String[0];
        }else{
            String cts = ctl.get(0);
            types=cts.substring(2,cts.length()-1).split(",");
        }

        List<String> cal = Regular.getArrayByExpression(ce, $A);
        if(cal.size()==0){
            classAnnotations=new Class[0];
        }else{
            String cas = cal.get(0);
            String[] split = cas.substring(2, cas.length() - 1).split(",");
            classAnnotations=new Class[split.length];
            for (int i = 0,j= split.length; i <j; i++) {
                classAnnotations[i]= (Class<? extends Annotation>) ClassUtils.getClass(split[i]);
            }
        }

        List<String> mnl = Regular.getArrayByExpression(me, $N);
        if(mnl.size()==0){
            methodNames=new String[0];
        }else{
            String mns = mnl.get(0);
            methodNames=mns.substring(2,mns.length()-1).split(",");
        }

        List<String> macl = Regular.getArrayByExpression(me, $AC);
        if(macl.size()==0){
            accesses=new HashSet<>();
            accesses.add(0);accesses.add(1);accesses.add(2);accesses.add(4);
        }else{
            String macs = macl.get(0);
            accesses=new HashSet<>();
            String[] access = macs.substring(3, macs.length() - 1).split(",");
            for (String s : access) {
                try {
                    accesses.add(Integer.parseInt(s));
                }catch (NumberFormatException e){
                    throw new RuntimeException("错误的访问修饰符配置：`"+macs+"` ,位置："+aspectMethod,e);
                }

            }
        }

        List<String> mal = Regular.getArrayByExpression(me, $A);
        if(mal.size()==0){
            methodAnnotations=new Class[0];
        }else{
            String mas = mal.get(0);
            String[] mann = mas.substring(2, mas.length() - 1).split(",");
            methodAnnotations=new Class[mann.length];
            for (int i = 0,j=mann.length; i < j; i++) {
                methodAnnotations[i]= (Class<? extends Annotation>) ClassUtils.getClass(mann[i]);
            }

        }

        List<String> mol = Regular.getArrayByExpression(me, $O);
        if(mol.size()==0){
            objectMethod=new HashSet<>();
        }else{
            String mos = mol.get(0);
            objectMethod=new HashSet<>();
            String[] objs = mos.substring(2, mos.length() - 1).split(",");
            for (String obj : objs) {
                objectMethod.add(obj);
            }
        }
    }

    /**
     * 类检验
     * @param bean 待检验Module实例
     * @return
     */
    public boolean classExamine(Module bean){
        Class<?> originalType = bean.getOriginalType();
        String name = originalType.getName();
        name=name.contains(".")?name.substring(0,name.lastIndexOf(".")):"";
        if(!packageExamine(name)){
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
        if(!methodNameExamine(method)){
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
            if(pack.startsWith("*")||pack.startsWith("!*")){
                if(pack.startsWith("!")){
                    if(!(fullClassName.endsWith(pack.substring(2)))){
                        return true;
                    }
                }
                if(fullClassName.endsWith(pack.substring(1))){
                    return true;
                }
            }
            if(pack.endsWith("*")){
                if(pack.startsWith("!")){
                    if(!(fullClassName.startsWith(pack.substring(1,pack.length()-1)))){
                        return true;
                    }
                }
                if(fullClassName.startsWith(pack.substring(0,pack.length()-1))){
                    return true;
                }
            }
            if(fullClassName.equals(pack)){
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

    //方法名检验
    private boolean methodNameExamine(Method method){
        String name=method.getName();
        for (String methodName : methodNames) {
            if("*".equals(methodName)){
                return true;
            }
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
            if("*".equals(str)){
                return true;
            }
            if(str.startsWith("*")||str.startsWith("!*")){
                if(str.startsWith("!")){
                    if(!(info.endsWith(str.substring(2)))){
                        return true;
                    }
                }
                if(info.endsWith(str.substring(1))){
                    return true;
                }
            }
            if(str.endsWith("*")){
                if(str.startsWith("!")){
                    if(!(info.startsWith(str.substring(1,info.length()-1)))){
                        return true;
                    }
                }
                if(info.startsWith(str.substring(0,info.length()-1))){
                    return true;
                }
            }
            if(str.equals(info)){
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String e="M:{AC[1u,2,3,3],N[show,query(int String)],A[com.lucky.framework.annotation.After,com.lucky.framework.annotation.Before]}";
        DefaultAopExecutionChecker a =new DefaultAopExecutionChecker();
        a.setPositionExpression(e);

    }

}
