package com.jaycekon.mybatis.multi.service;

import com.jaycekon.springbootmybatismulti.SpringBootMybatisMultiApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author huangweijie
 * @date 2018/11/21
 */
public class UserServiceTest extends SpringBootMybatisMultiApplicationTests {


    @Autowired
    private UserService userService;

    @Test
    public void addSchool() {

        userService.inserUser("root2","root2");
    }
}
