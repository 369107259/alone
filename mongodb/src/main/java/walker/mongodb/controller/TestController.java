package walker.mongodb.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: huangYong
 * @Date: 2021/6/7 16:32
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "success";
    }
}
