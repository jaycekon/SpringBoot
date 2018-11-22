package com.jaycekon.mybatis.multi.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;


/**
 * 2018/1/15 18:46
 */
@Configuration
@MapperScan(value = "com.jaycekon.mybatis.multi.mapper.db2", sqlSessionFactoryRef = "db2SqlSessionFactory")
@EnableTransactionManagement
public class DB2DataSourceConfig {

    private static final String MAPPER_LOCATION = "mybatis.mapper-locations.db2";

    @Autowired
    private Environment env;


    @Bean(name = "slaveDataSource")
    @Qualifier("slaveDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.db2")
    public DataSource slaveDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "db2SqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("slaveDataSource") DataSource myTestDb2DataSource,
                                               org.apache.ibatis.session.Configuration config) throws Exception {
        SqlSessionFactoryBean fb = new SqlSessionFactoryBean();
        fb.setConfiguration(config);
        fb.setDataSource(myTestDb2DataSource);
//        fb.setVfs(SpringBootVFS.class);
        fb.setTypeAliasesPackage(env.getProperty("mybatis.type-aliases-package"));
        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty(MAPPER_LOCATION)));
        return fb.getObject();
    }

    @Bean("slaveTransactionManager")
    public DataSourceTransactionManager transactionManager(@Qualifier("slaveDataSource") DataSource myTestDb2DataSource) {
        return new DataSourceTransactionManager(myTestDb2DataSource);
    }
}
