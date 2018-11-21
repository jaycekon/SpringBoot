package com.jaycekon.mybatis.multi.service;

import com.jaycekon.mybatis.multi.config.DataSource;
import com.jaycekon.mybatis.multi.mapper.UserMapper;
import com.jaycekon.mybatis.multi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author huangweijie
 * @date 2018/11/21
 */
@Service
@DataSource("db2")
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void inserUser(String username, String password) {
        User user = new User(username, password);
        userMapper.insert(user);
    }
}
