package com.jaycekon.mybatis.multi.service;

import com.jaycekon.mybatis.multi.config.DataSource;
import com.jaycekon.mybatis.multi.mapper.SchoolMapper;
import com.jaycekon.mybatis.multi.model.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jaycekon
 * @date 2018/11/21
 */
@Service
@DataSource("db1")
public class SchoolService {
    @Autowired
    private SchoolMapper schoolMapper;

    public void addSchool(String name, String province) {
        School school = new School(name, province);
        schoolMapper.insert(school);
    }

}
