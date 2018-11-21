package com.jaycekon.demo.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.lang.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用DatabaseContextHolder获取当前线程的DatabaseType
 *
 * 2018/1/15 18:56
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    static final Map<DatabaseType, List<String>> METHOD_TYPE_MAP = new HashMap<>();


    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        DatabaseType type = DatabaseContextHolder.getDatabaseType();
        logger.info("====================dataSource ==========" + type);
        return type;
    }

    void setMethodType(DatabaseType type, String content) {
        List<String> list = Arrays.asList(content.split(","));
        METHOD_TYPE_MAP.put(type, list);
    }
}
