package com.jaycekon.mybatis.multi.service;

import com.jaycekon.mybatis.multi.model.User;
import com.jaycekon.springbootmybatismulti.SpringBootMybatisMultiApplicationTests;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author jaycekon
 * @date 2018/11/21
 */
public class UserServiceTest extends SpringBootMybatisMultiApplicationTests {


    @Autowired
    private UserService userService;

    @Test
    public void addSchool() {

        userService.inserUser("root2", "root2");
    }

    @Test
    public void selectUser() {
        User user = userService.selectUser(4);
        System.out.println("ending!");

    }
}
