package com.jaycekon.event.listener;

import com.jaycekon.event.model.WaterEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author jiuling.hwj
 * @version v1 2021/8/12
 */
@Component
public class WaterEventListener{

    @Async
    @Order(0)
    @EventListener
    public void onApplicationEvent(WaterEvent waterEvent) {
        try {
            Thread.sleep(5000);
            System.out.println("Listener handle event :" + waterEvent.getAction()+", thread: " + Thread.currentThread().getName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
