package com.qpang.orderservice.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/orders")
public class OrderController {
    
    @GetMapping("/test")
    public String test(){
        return "order, ok";
    }
}
