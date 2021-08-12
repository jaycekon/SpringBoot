package com.jaycekon.event;

import com.jaycekon.event.model.WaterEvent;
import com.jaycekon.event.publisher.WaterEventPublisherAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


@SpringBootApplication
@Configuration
@RestController
@EnableAsync
public class EventApplication implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(1000);
        executor.setKeepAliveSeconds(300);
        executor.setThreadNamePrefix("Jiuling-Executor-");
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


    @Order(0)
    @Bean
    public ApplicationEventMulticaster applicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
        eventMulticaster.setTaskExecutor(Executors.newFixedThreadPool(10));
        return eventMulticaster;
    }



    @Autowired
    private WaterEventPublisherAware waterEventPublisherAware;

    @RequestMapping("/event")
    public void pushEvent(String action){
        System.out.println("event post start!  thread: " + Thread.currentThread().getName());
        waterEventPublisherAware.post(new WaterEvent(action,10));
        System.out.println("event post finish!  thread: " + Thread.currentThread().getName());

    }


    public static void main(String[] args) {
        System.out.println(5192877704l >> 2);
        SpringApplication.run(EventApplication.class, args);
    }

}
