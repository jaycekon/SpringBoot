package com.jaycekon.sentinel.ahas;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jiuling.hwj
 * @version v1 2021/7/3
 */
@RestController
public class IndexController {

    @RequestMapping("/index")
    public String index(){
        return "hello index";
    }
}
