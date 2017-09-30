package com.jaycekon.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Jaycekon on 2017/9/20.
 */

@RestController
public class DcController {
    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping("/dc")
    public String dc(){
        String services = "Services: " + discoveryClient.getServices();
        System.out.println(services);
        return services;
    }
}
