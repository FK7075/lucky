package com.lucky.framework;

import com.lucky.framework.annotation.Autowired;
import com.lucky.framework.confanalysis.YamlConfAnalysis;
import com.lucky.framework.junit.LuckyRunner;
import com.lucky.framework.serializable.implement.GsonSerializationScheme;
import com.lucky.framework.uitls.file.Resources;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;

/**
 * @author fk7075
 * @version 1.0
 * @date 2020/11/16 8:47
 */
@RunWith(LuckyRunner.class)
public class TestApp {

    @Autowired
    GsonSerializationScheme gson;

    @Test
    public void yamlTest(){
//        System.out.println(gson);
        YamlConfAnalysis yml=new YamlConfAnalysis(Resources.getReader("/app.yml"));
        Map<String, Object> map = yml.getMap();
        Map<String,Object> data = (Map<String, Object>) map.get("data");
        for (String s : map.keySet()) {
            System.out.println(s+"="+map.get(s));
        }
    }
}
