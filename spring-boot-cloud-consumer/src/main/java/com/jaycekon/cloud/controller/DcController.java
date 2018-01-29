package com.jaycekon.cloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Jaycekon on 2017/9/20.
 */
@RestController
public class DcController {


    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/consumer")
    public String consumer() {
        String url = "http://eureka-client/product?name=jaycekon";
        return new RestTemplate().getForObject(url, String.class);
    }


    @GetMapping("/ribbon")
    public String ribbon() {
        return restTemplate.getForObject("http://eureka-client/product?name=jaycekon", String.class);
    }


}
