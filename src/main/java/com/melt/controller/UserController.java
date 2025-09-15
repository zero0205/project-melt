// com/melt/controller/UserController.java
package com.melt.controller;

import com.melt.annotation.Controller;
import com.melt.annotation.RequestMapping;
import com.melt.annotation.RequestMethod;
import com.melt.annotation.RequestParam;
import com.melt.annotation.PathVariable;
import com.melt.annotation.RequestBody;

@Controller
public class UserController {

    // 간단한 User DTO 클래스 (내부 클래스로 정의)
    public static class User {
        private String name;
        private int age;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        @Override
        public String toString() {
            return "User{name='" + name + "', age=" + age + "}";
        }
    }

    // 1. RequestParam 테스트
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getUsers(@RequestParam("name") String name, @RequestParam("age") int age) {
        return "{\"message\": \"Found user: " + name + ", age: " + age + "\"}";
    }

    // 2. PathVariable 테스트
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public String getUserById(@PathVariable("id") Long id) {
        return "{\"message\": \"User ID: " + id + "\"}";
    }

    // 3. RequestBody 테스트
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser(@RequestBody String user) {
        return "{\"message\": \"Created user: " + user + "\"}";
    }

    // 4. 복합 테스트 (PathVariable + RequestParam)
    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public String updateUser(@PathVariable("id") Long id, @RequestParam("status") String status) {
        return "{\"message\": \"Updated user " + id + " to " + status + "\"}";
    }

    // 5. 기본 DELETE 테스트
    @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
    public String deleteUser(@PathVariable("id") Long id) {
        return "{\"message\": \"Deleted user " + id + "\"}";
    }
}