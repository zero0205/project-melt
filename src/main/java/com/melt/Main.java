package com.melt;

import com.melt.container.AnnotationContainer;
import com.melt.repository.UserRepository;
import com.melt.service.UserService;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 3주차 스프링 구현 테스트 시작!\n");

        AnnotationContainer container = new AnnotationContainer();

        // 각 클래스를 등록 시도해보기
        container.registerBean("com.melt.repository.UserRepository");  // 성공
        container.registerBean("com.melt.service.UserService");     // 성공
        container.registerBean("com.melt.util.RegularClass");    // 실패 (어노테이션 없음)

        // 등록된 Bean 목록 확인
        container.printAllBeans();

        // 실제로 Bean 사용해보기
        System.out.println("\n=== Bean 사용 테스트 ===");
        UserRepository repo = (UserRepository) container.getBean("UserRepository");
        if (repo != null) {
            repo.save();
        }

        UserService service = (UserService) container.getBean("UserService");
        if (service != null) {
            service.processUser();
        }

        System.out.println("\n✅ 3주차 구현 완료!");
    }
}