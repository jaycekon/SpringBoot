package com.jaycekon.demo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Created by weijie_huang on 2017/9/7.
 */
public interface MyMapper<T> extends Mapper<T>, MySqlMapper<T> {
}
