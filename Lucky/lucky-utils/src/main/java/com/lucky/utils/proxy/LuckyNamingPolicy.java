package com.lucky.utils.proxy;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/11/28 上午2:17
 */
public class LuckyNamingPolicy extends DefaultNamingPolicy {

    @Override
    protected String getTag() {
        return "CGLIB";
    }

    @Override
    public String getClassName(String prefix, String source, Object key, Predicate names) {
        return super.getClassName(prefix, "ByLucky", key, names);
    }
}
