package com.lucky.web.core.parameter.enhance;

import com.lucky.utils.proxy.ASMUtil;
import com.lucky.utils.reflect.ParameterUtils;
import com.lucky.web.core.parameter.analysis.ParameterAnalysis;
import com.lucky.web.mapping.UrlMapping;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0.0
 * @date 2020/12/18 上午12:56
 */
public class ParameterEnhanceChain {

    private List<ParameterEnhance> parameterEnhanceChain;

    public ParameterEnhanceChain() {
        parameterEnhanceChain=new ArrayList<>(5);
        parameterEnhanceChain.add(new ParameterCheckEnhance());
        parameterEnhanceChain.add(new ParameterMD5EncryptEnhance());
        parameterEnhanceChain.add(new ParameterEscapeEnhance());
    }

    public void sort(){
        parameterEnhanceChain=parameterEnhanceChain
                .stream()
                .sorted(Comparator.comparing(ParameterEnhance::priority))
                .collect(Collectors.toList());
    }

    public List<ParameterEnhance> getParameterEnhanceChain() {
        return parameterEnhanceChain;
    }

    public void setParameterEnhanceChain(List<ParameterEnhance> parameterEnhanceChain) {
        this.parameterEnhanceChain = parameterEnhanceChain;
    }

    public void addParameterEnhance(ParameterEnhance parameterEnhance){
        this.parameterEnhanceChain.add(parameterEnhance);
    }

    public Object enhance(Parameter parameter, Type genericType, Object runParam, String paramName){
        for (ParameterEnhance parameterEnhance : parameterEnhanceChain) {
            runParam=parameterEnhance.enhance(parameter, genericType, runParam, paramName);
        }
        return runParam;
    }
}
