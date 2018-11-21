package com.jaycekon.mybatis.multi.model;

import lombok.Data;

/**
 * @author huangweijie
 * @date 2018/11/21
 */
@Data
public class School {
    private int id;
    private String schoolName;
    private String province;


    public School(String schoolName, String province) {
        this.schoolName = schoolName;
        this.province = province;
    }
}
