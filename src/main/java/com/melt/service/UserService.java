package com.melt.service;

import com.melt.annotation.Autowired;
import com.melt.annotation.Service;
import com.melt.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public void saveUser(String user) {
        System.out.println("UserService: 사용자 저장 요청");
        userRepository.save(user);
    }

    // DI 잘 됐는지 확인용
    public void checkDependency() {
        System.out.println("UserRepository 주입 상태: " + (userRepository != null));
    }
}
