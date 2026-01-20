package com.maxi.analyser.controller;

import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    public String createOrder() {
        return "Order created";
    }
}

