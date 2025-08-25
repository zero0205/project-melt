package com.melt.container;

import com.melt.annotation.Component;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

public class AnnotationContainer {
    private final Map<String, Object> beans = new HashMap<>();

    // 클래스명으로 Bean 등록 시도
    public void registerBean(String className) {
        try {
            // 1. 클래스 정보 로드
            Class<?> clazz = Class.forName(className);

            // 2. @Component 어노테이션 또는 @Component가 붙은 어노테이션 확인
            if (hasComponent(clazz)) {
                // 3. 객체 생성
                Object instance = clazz.getDeclaredConstructor().newInstance();

                // 4. 컨테이너에 저장
                String beanName = clazz.getSimpleName();
                beans.put(beanName, instance);

                System.out.println("✅ " + beanName + " Bean 등록 완료!");
            } else {
                System.out.println("❌ " + className + "에는 @Component가 없어서 등록 안함");
            }
        } catch (Exception e) {
            System.out.println("🚨 등록 실패: " + e.getMessage());
        }
    }

    // Bean 조회
    public Object getBean(String beanName) {
        return beans.get(beanName);
    }

    // 등록된 모든 Bean 목록 출력
    public void printAllBeans() {
        System.out.println("\n=== 등록된 Bean 목록 ===");
        beans.keySet().forEach(name ->
            System.out.println("- " + name)
        );
    }

    // @Component 어노테이션이 직접 또는 메타 어노테이션으로 있는지 확인
    private boolean hasComponent(Class<?> clazz) {
        // 1. 직접 @Component가 있는지 확인
        if (clazz.isAnnotationPresent(Component.class)) {
            return true;
        }

        // 2. 클래스에 붙은 모든 어노테이션을 확인하여 @Component가 메타 어노테이션으로 있는지 확인
        Annotation[] annotations = clazz.getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.isAnnotationPresent(Component.class)) {
                return true;
            }
        }

        return false;
    }
}
