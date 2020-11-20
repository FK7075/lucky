package com.lucky.web.core.parameter;

import com.lucky.framework.uitls.reflect.ASMUtil;
import com.lucky.web.core.Model;
import com.lucky.web.mapping.Mapping;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/20 16:27
 */
public class ParameterAnalysisChain {

    private List<ParameterAnalysis> parameterAnalysesChain=new ArrayList<>();

    public List<ParameterAnalysis> getParameterAnalysesChain() {
        return parameterAnalysesChain;
    }

    public void setParameterAnalysesChain(List<ParameterAnalysis> parameterAnalysesChain) {
        this.parameterAnalysesChain = parameterAnalysesChain;
    }

    public void sort(){
        parameterAnalysesChain=parameterAnalysesChain.stream().sorted().collect(Collectors.toList());
    }

    public Object[] analysis(Model model, Mapping mapping) throws Exception {
        String[] paramNames = ASMUtil.getMethodParamNames(mapping.getMapping());
        Parameter[] parameters = mapping.getParameters();
        Object[] paramObject=new Object[parameters.length];
        for (int i = 0,j=parameters.length; i < j; i++) {
            for (ParameterAnalysis parameterAnalysis : parameterAnalysesChain) {
                if(parameterAnalysis.can(model,mapping.getMapping(),parameters[i],paramNames[i])){
                    paramObject[i]=parameterAnalysis.analysis(model,mapping.getMapping(),parameters[i],paramNames[i]);
                }
            }
            paramObject[i]=null;
        }
        return paramObject;
    }
}
