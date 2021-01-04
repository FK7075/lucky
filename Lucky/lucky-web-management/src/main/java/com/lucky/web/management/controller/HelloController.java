package com.lucky.web.management.controller;

import com.lucky.web.annotation.Controller;
import com.lucky.web.annotation.GetMapping;
import com.lucky.web.controller.JarExpandDetailsController;

import java.util.List;

/**
 * @author fk
 * @version 1.0
 * @date 2020/12/29 0029 11:26
 */
@Controller("webmanagement")
public class HelloController extends JarExpandDetailsController {

    @GetMapping("allServers")
    public String hello(){
        List<JarExpandDetailsController.MappingDto> mappingDtos = jarExpandInfo();
        model.addAttribute("jars",mappingDtos);
        return "index";
    }
}
