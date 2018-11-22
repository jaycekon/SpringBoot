package com.jaycekon.mybatis.multi.service;

import com.jaycekon.mybatis.multi.model.School;
import com.jaycekon.springbootmybatismulti.SpringBootMybatisMultiApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author jaycekon
 * @date 2018/11/21
 */
public class SchoolServiceTest extends SpringBootMybatisMultiApplicationTests {

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private UserService userService;

    @Test
    public void addUser() {
        userService.inserUser("root33", "root33");
    }

//    @Test
//    public void addSchool() {
//        schoolService.addSchool("ceshi11", "ceshi11");
//    }


}