package com.melt;

import com.melt.context.ApplicationContext;
import com.melt.service.UserService;
import com.melt.repository.UserRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("🎯 4주차 최종 테스트: Component Scan + Bean Factory + DI");
        System.out.println();

        try {
            // ApplicationContext로 Spring 컨테이너 초기화
            ApplicationContext context = new ApplicationContext();
            context.scan("com.melt");

            // Bean 가져와서 테스트
            System.out.println("\n📋 Bean 조회 테스트:");
            UserService userService = context.getBean(UserService.class);
            UserRepository userRepository = context.getBean(UserRepository.class);

            if (userService != null && userRepository != null) {
                System.out.println("✅ 모든 Bean 조회 성공!");

                // 🎯 핵심 테스트: @Autowired가 제대로 작동하는지 확인
                System.out.println("\n💡 @Autowired 동작 테스트:");

                // UserService의 checkDependency() 메소드 호출 (이게 잘 되면 DI 성공!)
                userService.checkDependency();

                // 실제 비즈니스 로직 실행 (UserService가 UserRepository 사용)
                System.out.println("\n🚀 비즈니스 로직 테스트:");
                userService.saveUser("고길동");

            } else {
                System.err.println("❌ Bean 조회 실패");
            }

        } catch (Exception e) {
            System.err.println("❌ 테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}