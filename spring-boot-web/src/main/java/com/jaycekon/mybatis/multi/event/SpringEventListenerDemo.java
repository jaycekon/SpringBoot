package com.jaycekon.mybatis.multi.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Spring 事件监听器
 * Created by weijie_huang on 2017/11/7.
 */
public class SpringEventListenerDemo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyApplicationListener.class);

        //增加事件监听器
//        context.addApplicationListener(new MyApplicationListener());
//        context.register(MyApplicationListener.class);
        //上下文启动
//        context.refresh();

        context.publishEvent(new MyApplicationEvent("hello world0 \n"));
        context.publishEvent(new MyApplicationEvent("hello world1 \n"));
        context.publishEvent(new MyApplicationEvent("hello world2 \n"));
        context.publishEvent(new MyApplicationEvent("hello world3 \n"));
    }

    @Component
    private static class MyApplicationListener implements ApplicationListener<MyApplicationEvent> {

        @Override
        public void onApplicationEvent(MyApplicationEvent event) {
            System.out.printf("Application evet source: %s", event.getSource());
        }
    }


    private static class MyApplicationEvent extends ApplicationEvent {

        /**
         * Create a new ApplicationEvent.
         *
         * @param source the object on which the event initially occurred (never {@code null})
         */
        public MyApplicationEvent(Object source) {
            super(source);
        }
    }
}
