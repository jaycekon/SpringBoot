package com.jaycekon.mybatis.multi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author jaycekon
 * @date 2018/11/21
 */
@Data
@AllArgsConstructor
public class School {
    private int id;
    private String schoolName;
    private String province;


    public School(String schoolName, String province) {
        this.schoolName = schoolName;
        this.province = province;
    }
}
