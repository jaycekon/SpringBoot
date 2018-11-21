package com.jaycekon.mybatis.multi.config;

/**
 * 保存一个线程安全的DatabaseType容器
 * <p>
 * 用于保存数据源类型
 * 2018/1/15 18:57
 */
public class DatabaseContextHolder {
    private static final ThreadLocal<DatabaseType> contextHolder = new ThreadLocal<>();
    public static final DatabaseType DEFAULT_DATASOURCE = DatabaseType.db1;


    public static void setDatabaseType(DatabaseType type) {
        contextHolder.set(type);
    }

    public static DatabaseType getDatabaseType() {
        return contextHolder.get();
    }
}
