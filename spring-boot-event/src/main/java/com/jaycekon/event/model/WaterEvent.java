package com.jaycekon.event.model;

import org.springframework.context.ApplicationEvent;

/**
 * @author jiuling.hwj
 * @version v1 2021/8/12
 */
public class WaterEvent  extends ApplicationEvent {

    private String action;

    private int capacity;

    public WaterEvent(String action,int capacity) {
        super(action);
        this.action = action;
        this.capacity = capacity;
    }


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
