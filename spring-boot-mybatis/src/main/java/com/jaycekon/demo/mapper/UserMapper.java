package com.jaycekon.demo.mapper;

import com.jaycekon.demo.MyMapper;
import com.jaycekon.demo.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * 2018/1/9 16:58
 */
@Mapper
@Qualifier(value = "UserMapper")
public interface UserMapper extends MyMapper<User> {

    @Select("select * from user where username=#{username}")
    User selectByName(String username);

    User selectByIdCard(String idCard);
}
