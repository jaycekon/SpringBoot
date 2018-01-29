package com.jaycekon.cloud.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Jaycekon on 2017/9/20.
 */

@RestController
public class DcController {



    @Value("${server.port}")
    String port;

    @RequestMapping("/product")
    public String product(@RequestParam String name) {
        return "hi "+name+",i am from port:" +port;
    }

}
