package com.jaycekon.apollo.config

import com.alibaba.druid.pool.DruidDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.core.env.Environment
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
open class BeanConfig {

    @Autowired
    private val properties: DataSourceProperties? = null

    @Bean
    open fun dataSource(): DataSource {
        val dataSource = DruidDataSource()
        dataSource.driverClassName = properties!!.driverClassName
        dataSource.url = properties.url
        dataSource.username = properties.username
        dataSource.password = properties.password
        return dataSource
    }

    @Bean
    open fun jdbcTemplate(): JdbcTemplate {
        val jdbcTemplate = JdbcTemplate()
        jdbcTemplate.dataSource = dataSource()
        return jdbcTemplate
    }
}