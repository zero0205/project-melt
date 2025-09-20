// com/melt/controller/UserController.java
package com.melt.controller;

import com.melt.annotation.RestController;
import com.melt.annotation.GetMapping;
import com.melt.annotation.PostMapping;
import com.melt.annotation.PutMapping;
import com.melt.annotation.DeleteMapping;
import com.melt.annotation.PatchMapping;
import com.melt.annotation.RequestParam;
import com.melt.annotation.PathVariable;
import com.melt.annotation.RequestBody;

@RestController
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

    // 1. 전체 사용자 조회 (RequestParam 테스트)
    @GetMapping("/api/users")
    public String getUsers(@RequestParam(value = "name", required = false) String name,
                          @RequestParam(value = "age", required = false) Integer age) {
        if (name != null && age != null) {
            return "{\"message\": \"Found user: " + name + ", age: " + age + "\"}";
        }
        return "{\"users\": [{\"id\": 1, \"name\": \"John\", \"age\": 25}, {\"id\": 2, \"name\": \"Jane\", \"age\": 30}]}";
    }

    // 2. 특정 사용자 조회 (PathVariable 테스트)
    @GetMapping("/api/users/{id}")
    public String getUserById(@PathVariable("id") Long id) {
        return "{\"user\": {\"id\": " + id + ", \"name\": \"User " + id + "\", \"age\": 25}}";
    }

    // 3. 사용자 생성 (RequestBody 테스트)
    @PostMapping("/api/users")
    public String createUser(@RequestBody String user) {
        return "{\"message\": \"User created successfully\", \"data\": " + user + "}";
    }

    // 4. 사용자 전체 수정 (PUT)
    @PutMapping("/api/users/{id}")
    public String updateUser(@PathVariable("id") Long id, @RequestBody String user) {
        return "{\"message\": \"User " + id + " fully updated\", \"data\": " + user + "}";
    }

    // 5. 사용자 부분 수정 (PATCH)
    @PatchMapping("/api/users/{id}")
    public String patchUser(@PathVariable("id") Long id, @RequestBody String updates) {
        return "{\"message\": \"User " + id + " partially updated\", \"changes\": " + updates + "}";
    }

    // 6. 사용자 삭제 (DELETE)
    @DeleteMapping("/api/users/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        return "{\"message\": \"User " + id + " deleted successfully\"}";
    }

    // 7. 사용자 상태 변경 (PathVariable + RequestParam 조합)
    @PutMapping("/api/users/{id}/status")
    public String updateUserStatus(@PathVariable("id") Long id, @RequestParam("status") String status) {
        return "{\"message\": \"User " + id + " status changed to: " + status + "\"}";
    }
}