// com/melt/controller/UserController.java
package com.melt.controller;

import com.melt.annotation.Controller;
import com.melt.annotation.RequestMapping;
import com.melt.annotation.RequestMethod;

@Controller
public class UserController {

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getUsers() {
        return "GET: User List [user1, user2, user3]";
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser() {
        return "POST: User created successfully!";
    }

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public String updateUser() {
        return "PUT: User updated successfully!";
    }

    @RequestMapping(value = "/users", method = RequestMethod.DELETE)
    public String deleteUser() {
        return "DELETE: User deleted successfully!";
    }
}