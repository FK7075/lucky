package com.lucky.web.mapping;

import com.lucky.web.exception.RepeatUrlMappingException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/17 10:03
 */
public class MappingCollection {

    private List<Mapping> list;

    public MappingCollection(){
        list=new ArrayList<>(10);
    }

    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        if(!(o instanceof Mapping)){
            return false;
        }
        Mapping map= (Mapping) o;
        for (Mapping mapping : list) {
            if(mapping.isEquals(map)){
                return true;
            }
        }
        return false;
    }

    public Iterator<Mapping> iterator() {
        return list.iterator();
    }

    public boolean add(Mapping mapping) {
        if(contains(mapping)){
            throw new RepeatUrlMappingException(mapping);
        }
        list.add(mapping);
        return true;
    }

    public void clear() {
        list.clear();
    }
}
