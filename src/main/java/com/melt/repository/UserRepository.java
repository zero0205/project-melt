package com.melt.repository;

import com.melt.annotation.Repository;

@Repository
public class UserRepository {
    public void save() {
        System.out.println("사용자 저장 완료!");
    }
}