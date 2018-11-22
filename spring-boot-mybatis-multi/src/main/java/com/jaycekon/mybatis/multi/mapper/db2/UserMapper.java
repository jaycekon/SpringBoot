package com.jaycekon.mybatis.multi.mapper.db2;

import com.jaycekon.mybatis.multi.model.User;

/**
 * @author
 * @date 2018/11/21
 */
public interface UserMapper {

    void insert(User user);

    User select(int id);
}
