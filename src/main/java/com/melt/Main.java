package com.melt;

import com.melt.context.ComponentScanner;
import com.melt.context.BeanFactory;
import com.melt.service.UserService;
import com.melt.repository.UserRepository;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== 🚀 4주차 ComponentScanner + BeanFactory 테스트 ===\n");

        try {
            // 1. ComponentScanner로 클래스 스캔
            ComponentScanner scanner = new ComponentScanner();
            List<Class<?>> foundClasses = scanner.scanComponents("com.melt");

            // 2. BeanFactory로 Component만 Bean 생성
            BeanFactory beanFactory = new BeanFactory();
            beanFactory.createAndRegisterBeans(foundClasses);

            // 3. 등록된 Bean 확인
            beanFactory.printAllBeans();

            // 4. Bean 가져와서 테스트
            UserService userService = beanFactory.getBean(UserService.class);
            UserRepository userRepository = beanFactory.getBean(UserRepository.class);

            if (userService != null) {
                System.out.println("✅ UserService Bean 조회 성공!");
            } else {
                System.out.println("❌ UserService Bean 못 찾음");
            }

            if (userRepository != null) {
                System.out.println("✅ UserRepository Bean 조회 성공!");
            } else {
                System.out.println("❌ UserRepository Bean 못 찾음");
            }

        } catch (Exception e) {
            System.err.println("❌ 테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}