package com.jaycekon.event.listener;

import com.jaycekon.event.model.WaterEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @author jiuling.hwj
 * @version v1 2021/8/12
 */
@Component
public class DrinkEventListener {

//    @Async
    @EventListener
    public void onApplicationEvent(WaterEvent waterEvent) {
        try {
            Thread.sleep(5000);
            System.out.println("Listener drink event :" + waterEvent.getAction()+", thread: " + Thread.currentThread().getName());
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
