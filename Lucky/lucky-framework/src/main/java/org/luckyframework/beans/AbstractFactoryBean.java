package org.luckyframework.beans;

import com.lucky.utils.base.Assert;
import com.lucky.utils.reflect.ClassUtils;
import org.luckyframework.beans.factory.BeanFactory;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2021/4/12 上午12:54
 */
public abstract class AbstractFactoryBean<T> implements FactoryBean<T> {

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

    protected Object[] getRealArgs() {
        if(realValues == null){
            realValues = new Object[refValues.length];
            int i = 0;
            for (Object refValue : refValues) {
                if(refValue instanceof BeanReference){
                    BeanReference ref = (BeanReference) refValue;
                    if (ref.getAutowire() == Autowire.BY_NAME){
                        realValues[i++] = beanFactory.getBean(ref.getBeanName());
                    }else{
                        realValues[i++] = beanFactory.getBean(ref.getReferenceType(beanFactory));
                    }
                }else{
                    realValues[i++] = refValue;
                }
            }
        }
        return realValues;
    }

    protected Class<?>[] getRealArgsClasses() {
        if(realClasses == null){
            realClasses = new Class<?>[refValues.length];
            int i = 0;
            for (Object refValue : refValues) {
                if(refValue instanceof BeanReference){
                    realClasses[i++] = ((BeanReference)refValue).getReferenceType(beanFactory);
                }else{
                    realClasses[i++] = refValue.getClass();
                }
            }
        }
        return realClasses;
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
                        sb.append("REF_BY_NAME(").append(beanReference.getBeanName()).append("),");
                    }else{
                        sb.append("REF_BY_TYPE(").append(beanReference.getReferenceType(beanFactory)).append("),");
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
