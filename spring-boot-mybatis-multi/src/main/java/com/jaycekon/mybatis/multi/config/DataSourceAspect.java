package com.jaycekon.mybatis.multi.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 动态处理数据源，根据命名区分
 *
 * 2018/1/17 10:18
 */
@Aspect
@Component
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class DataSourceAspect {
    private static Logger logger = LoggerFactory.getLogger(DataSourceAspect.class);


    @Before("@annotation(com.jaycekon.mybatis.multi.config.DataSource)")
    public void before(JoinPoint point) {
        Class<?> className = point.getTarget().getClass();
        String methodName = point.getSignature().getName();
        Class[] argClass = ((MethodSignature) point.getSignature()).getParameterTypes();
        DatabaseType dataSource = DatabaseContextHolder.DEFAULT_DATASOURCE;
        try {
            Method method = className.getMethod(methodName, argClass);
            // 判断是否存在@DS注解
            if (method.isAnnotationPresent(DataSource.class)) {
                DataSource annotation = method.getAnnotation(DataSource.class);
                // 取出注解中的数据源名
                dataSource = DatabaseType.valueOf(annotation.value());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        DatabaseContextHolder.setDatabaseType(dataSource);
    }
}
