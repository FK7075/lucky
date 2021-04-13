package org.luckyframework.beans.create;

import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.ClassUtils;
import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.Autowire;
import org.luckyframework.beans.BeanReference;
import org.luckyframework.beans.ConstructorValue;
import org.luckyframework.beans.factory.BeanFactory;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/4/12 上午12:54
 */
public abstract class AbstractFactoryBean implements MightNeedBeanFactoryFactoryBean {

    /** BeanFactory */
    protected BeanFactory beanFactory;
    /** 是否需要BeanFactory */
    protected boolean needBeanFactory = false;
    /** 参数的类型 */
    protected Class<?>[] realClasses;
    /** 参数的真实值 */
    protected Object[] realValues;
    /** 参数的依赖值 */
    protected Object[] refValues;

    public AbstractFactoryBean(ConstructorValue[] constructorValues){
        Assert.notNull(constructorValues,"ConstructorValue is null");
        this.refValues = new Object[constructorValues.length];
        int i = 0;
        for (ConstructorValue arg : constructorValues) {
            refValues[i++] = arg.getValue();
        }
        this.needBeanFactory = true;
    }

    public AbstractFactoryBean(){
        this.realClasses = new Class<?>[0];
        this.realValues = new Object[0];
    }

    public AbstractFactoryBean(Object[] realValues){
        Assert.notNull(realValues,"The parameter of the constructor is null");
        this.realValues = realValues;
        this.realClasses = ClassUtils.array2Class(realValues);
    }

    @Override
    public boolean needBeanFactory() {
        return this.needBeanFactory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    protected Object getObjectByBeanFactory(Object refValue){
        if(refValue instanceof BeanReference){
            BeanReference ref = (BeanReference) refValue;
            if (ref.getAutowire() == Autowire.BY_NAME){
                return beanFactory.getBean(ref.getBeanName());
            }else{
                return beanFactory.getBean(ref.getReferenceType(beanFactory));
            }
        }
        if(refValue instanceof FactoryBean){
            return ((FactoryBean)refValue).getBean();
        }
        return refValue;
    }

    protected Class<?> getTypeByBeanFactory(Object refValue){
        if(refValue instanceof BeanReference){
            return ((BeanReference)refValue).getReferenceType(beanFactory);
        }
        if(refValue instanceof FactoryBean){
            ResolvableType resolvableType = ResolvableType.forInstance(refValue);
            return resolvableType.getSuperType().resolveGeneric(0);
        }
        return refValue.getClass();

    }

    protected Object[] getRealArgs() {
        if(realValues == null){
            realValues = getRealArgs(refValues);
        }
        return realValues;
    }

    protected Class<?>[] getRealArgsClasses() {
        if(realClasses == null){
            realClasses = getRealArgsClasses(refValues);
        }
        return realClasses;
    }

    protected Class<?>[] getRealArgsClasses(Object[] refValues){
        Class<?>[] realClasses = new Class<?>[refValues.length];
        int i = 0;
        for (Object refValue : refValues) {
            realClasses[i++] = getTypeByBeanFactory(refValue);
        }
        return realClasses;
    }

    protected Object[] getRealArgs(Object[] refValues){
        Object[] realValues = new Object[refValues.length];
        int i = 0;
        for (Object refValue : refValues) {
            realValues[i++] = getObjectByBeanFactory(refValue);
        }
        return realValues;
    }

    protected String argsToString(){
        StringBuilder sb = new StringBuilder("(");
        if(realValues != null){
            for (Object value : realValues) {
                sb.append(value).append(",");
            }
        }else{
            for (Object ref : refValues) {
                if(ref instanceof BeanReference){
                    BeanReference beanReference = (BeanReference) ref;
                    if(Autowire.BY_NAME == beanReference.getAutowire()){
                        sb.append("REF<").append(beanReference.getBeanName()).append(">,");
                    }else{
                        sb.append("REF<").append(beanReference.getReferenceType(beanFactory)).append(">,");
                    }
                }else {
                    sb.append(ref).append(",");
                }
            }
        }
        if(sb.toString().endsWith(",")){
            return sb.substring(0,sb.length()-1)+")";
        }
        return sb.append(")").toString();
    }
}
