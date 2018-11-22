package com.jaycekon.mybatis.multi.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author
 * @date 2018/11/15
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface DataSource {
    String value() default "test_1";
}