// com/melt/controller/UserController.java
package com.melt.controller;

import com.melt.annotation.Controller;
import com.melt.annotation.RequestMapping;

@Controller
public class UserController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello from UserController! 🎉";
    }

    @RequestMapping("/users")
    public String getUsers() {
        return "User List: [user1, user2, user3] 👥";
    }

    @RequestMapping("/users/count")
    public String getUserCount() {
        return "Total Users: 42 📊";
    }
}