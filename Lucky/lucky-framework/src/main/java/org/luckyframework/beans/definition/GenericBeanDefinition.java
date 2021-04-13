package org.luckyframework.beans.definition;

import com.lucky.utils.type.ResolvableType;
import org.luckyframework.beans.BeanScope;
import org.luckyframework.beans.create.FactoryBean;

/**
 * 通用的Bean定义信息
 * @author fk
 * @version 1.0
 * @date 2021/4/12 0012 10:50
 */
public class GenericBeanDefinition implements BeanDefinition {

    private BeanScope beanScope = BeanScope.SINGLETON;
    private boolean lazyInit = false;
    private FactoryBean factoryBean;
    private String initMethodName;
    private String destroyMethodName;


    @Override
    public void setFactoryBean(FactoryBean factoryBean) {
        this.factoryBean = factoryBean;
    }

    @Override
    public FactoryBean getFactoryBean() {
        return this.factoryBean;
    }

    @Override
    public void setScope(BeanScope scope) {
        this.beanScope = scope;
    }

    @Override
    public boolean isSingleton() {
        return BeanScope.SINGLETON == this.beanScope;
    }

    @Override
    public boolean isPrototype() {
        return BeanScope.PROTOTYPE == this.beanScope;
    }

    @Override
    public boolean isLazyInit() {
        return this.lazyInit;
    }

    @Override
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    @Override
    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }

    @Override
    public String getInitMethodName() {
        return this.initMethodName;
    }

    @Override
    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodName = destroyMethodName;
    }

    @Override
    public String getDestroyMethodName() {
        return this.destroyMethodName;
    }

    @Override
    public BeanDefinition copy() {
        try {
            return (BeanDefinition) clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResolvableType getBeanType() {
        ResolvableType resolvableType = ResolvableType.forInstance(factoryBean);
        return resolvableType.getSuperType().getGeneric(0);
    }
}
