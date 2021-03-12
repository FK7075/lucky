package com.lucky.v2.aop.advisor;

import java.util.List;

/**
 * Advisor注册接口
 * @author fk
 * @version 1.0
 * @date 2021/3/12 0012 11:27
 */
public interface AdvisorRegistry {

    //注册Advisor
    void registAdvisor(Advisor ad);

    //获取Advisor
    List<Advisor> getAdvisors();
}
