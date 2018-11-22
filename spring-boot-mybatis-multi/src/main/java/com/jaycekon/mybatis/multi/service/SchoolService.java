package com.jaycekon.mybatis.multi.service;

import com.jaycekon.mybatis.multi.config.DataSource;
import com.jaycekon.mybatis.multi.mapper.db1.SchoolMapper;
import com.jaycekon.mybatis.multi.model.School;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jaycekon
 * @date 2018/11/21
 */
@Service
public class SchoolService {
    @Autowired
    private SchoolMapper schoolMapper;

    @Transactional("masterTransactionManager")
    public void addSchool(String name, String province) {
        School school = new School(name, province);
        schoolMapper.insert(school);
    }

    public School selectSchool(int id) {
        return schoolMapper.select(id);
    }

}
