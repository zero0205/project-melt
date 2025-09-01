package com.melt.repository;

import com.melt.annotation.Repository;

@Repository
public class UserRepository {
    public void save(String user) {
        System.out.println("UserRepository: " + user + "저장됨");
    }
}