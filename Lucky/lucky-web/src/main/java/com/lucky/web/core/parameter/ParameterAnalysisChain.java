package com.lucky.web.core.parameter;

import com.lucky.framework.proxy.ASMUtil;
import com.lucky.web.core.Model;
import com.lucky.web.mapping.Mapping;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Comparator;
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

    public void addParameterAnalysis(ParameterAnalysis parameterAnalysis){
        parameterAnalysesChain.add(parameterAnalysis);
    }

    public ParameterAnalysisChain(){
        parameterAnalysesChain.add(new HttpParameterAnalysis());//1-ok
        parameterAnalysesChain.add(new CallApiParameterAnalysis());//2-
        parameterAnalysesChain.add(new SerializationParameterAnalysis());//3-ok
        parameterAnalysesChain.add(new AnnotationFileParameterAnalysis());//4-ok
        parameterAnalysesChain.add(new MultipartFileParameterAnalysis());//5-ok
        parameterAnalysesChain.add(new PojoParameterAnalysis());//6-ok
        parameterAnalysesChain.add(new BaseParameterAnalysis());//7-ok

    }

    public void sort(){
        parameterAnalysesChain=parameterAnalysesChain.stream().sorted(Comparator.comparing(ParameterAnalysis::priority)).collect(Collectors.toList());
    }

    /**
     * [策略模式]
     * URL请求参数解析
     * @param model 当前请求的Model对象
     * @param mapping 当前请求的映射
     * @return 执行当前映射方法所需要的的参数
     * @throws Exception
     */
    public Object[] analysis(Model model, Mapping mapping) throws Exception {
        String[] paramNames = ASMUtil.getMethodParamNames(mapping.getMapping());
        Parameter[] parameters = mapping.getParameters();
        Object[] paramObject=new Object[parameters.length];
        for (int i = 0,j=parameters.length; i < j; i++) {
            for (ParameterAnalysis parameterAnalysis : parameterAnalysesChain) {
                if(parameterAnalysis.can(model,mapping.getMapping(),parameters[i],paramNames[i])){
                    paramObject[i]=parameterAnalysis.analysis(model,mapping.getMapping(),parameters[i],paramNames[i]);
                    break;
                }
            }
        }
        return paramObject;
    }
}
