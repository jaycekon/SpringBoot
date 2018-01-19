package com.jaycekon.demo.config;

import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

/**
 * 通过DataSourceBuilder.create() 快速创建DataSource
 *
 * 2018/1/19 14:15
 */
//@Configuration
//@MapperScan("com.jaycekon.demo.mapper")
//@EnableTransactionManagement
public class SpringJDBCDataSource {

    /**
     * 通过Spring JDBC 快速创建 DataSource
     * 参数格式
     * spring.datasource.master.jdbcurl=jdbc:mysql://localhost:3306/charles_blog
     * spring.datasource.master.username=root
     * spring.datasource.master.password=root
     * spring.datasource.master.driver-class-name=com.mysql.jdbc.Driver
     *
     * @return DataSource
     */
//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.master")
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}
