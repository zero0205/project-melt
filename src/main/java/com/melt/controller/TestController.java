package com.melt.controller;

import com.melt.annotation.Controller;
import com.melt.annotation.RequestMapping;

@Controller
public class TestController {

    @RequestMapping("/test")
    public String test() {
        return "Test Controller Works! ✅";
    }

    @RequestMapping("/health")
    public String health() {
        return "Server is healthy! 💚";
    }
}