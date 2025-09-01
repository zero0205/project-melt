package com.melt;

import com.melt.container.AnnotationContainer;
import com.melt.context.ComponentScanner;
import com.melt.repository.UserRepository;
import com.melt.service.UserService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 4주차 ComponentScanner 테스트 시작!\n");

        try {
            // ComponentScanner 생성
            ComponentScanner componentScanner = new ComponentScanner();

            // com.melt 패키지 스캔
            List<Class<?>> foundClasses = componentScanner.scanComponents("com.melt");

            System.out.println("\n발견된 클래스들:");
            for (Class<?> clazz: foundClasses) {
                System.out.println("  - " + clazz.getSimpleName() + " (" + clazz.getName() + ")");
            }

            System.out.println(("\nComponentScanner 테스트 완료!"));
        } catch (Exception e) {
            System.err.println("테스트 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}