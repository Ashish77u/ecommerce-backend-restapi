package com.codewithluci.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("demo/")
public class Demo {


    @GetMapping
    public String index() {
        return "Hello World";
    }

}
