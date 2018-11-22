package com.jaycekon.mybatis.multi.service;

import com.jaycekon.mybatis.multi.config.DataSource;
import com.jaycekon.mybatis.multi.mapper.db1.SchoolMapper;
import com.jaycekon.mybatis.multi.mapper.db2.UserMapper;
import com.jaycekon.mybatis.multi.model.School;
import com.jaycekon.mybatis.multi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jaycekon
 * @date 2018/11/21
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SchoolMapper schoolMapper;

    @Transactional("slaveTransactionManager")
    public void inserUser(String username, String password) {
        User user = new User(username, password);
        userMapper.insert(user);
        School school = new School(username, password);
        schoolMapper.insert(school);
    }

    public User selectUser(int id) {
        return userMapper.select(id);
    }
}
