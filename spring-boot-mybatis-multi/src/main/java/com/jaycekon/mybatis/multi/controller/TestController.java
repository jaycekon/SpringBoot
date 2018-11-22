package com.jaycekon.mybatis.multi.controller;

import com.jaycekon.mybatis.multi.service.SchoolService;
import com.jaycekon.mybatis.multi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author huangweijie
 * @date 2018/11/22
 */
@RestController
public class TestController {
    @Autowired
    private SchoolService schoolService;

    @Autowired
    private UserService userService;


    @RequestMapping("/test")
    public String test() {
        schoolService.addSchool("ceshi1", "ceshi1");
        userService.inserUser("root2", "root2");
        return "hello";
    }
}
