package com.jaycekon.mybatis.multi.mapper.db1;

import com.jaycekon.mybatis.multi.model.School;

/**
 * @author
 * @date 2018/11/21
 */
public interface SchoolMapper {

    void insert(School school);

    School select(int id);
}
