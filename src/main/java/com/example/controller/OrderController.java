package com.example.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @GetMapping("/")
    public String home() {
        return "Order Domain Project is running!";
    }
}