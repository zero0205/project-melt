package com.melt.context;

import com.melt.annotation.Component;
import com.melt.annotation.Controller;
import com.melt.annotation.RestController;
import com.melt.annotation.Service;
import com.melt.annotation.Repository;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bean 생성과 관리를 담당하는 팩토리
 */
public class BeanFactory {

    // 타입별로 Bean 저장 (UserRepository.class -> UserRepository 인스턴스)
    private final Map<Class<?>, Object> beansByType = new HashMap<>();

    // 이름별로 Bean 저장 (userRepository -> UserRepository 인스턴스)
    private final Map<String, Object> beansByName = new HashMap<>();

    /**
     * 스캔된 클래스들 중에서 Component 어노테이션이 붙은 것만 Bean으로 생성
     */
    public void createAndRegisterBeans(List<Class<?>> candidateClasses) {
        System.out.println("\n🏭 Bean Factory 시작!");

        for (Class<?> clazz : candidateClasses) {
            // 🔍 Component 어노테이션 체크 (필터링!)
            if (isComponent(clazz)) {
                try {
                    // Bean 인스턴스 생성
                    Object bean = clazz.getDeclaredConstructor().newInstance();

                    // 저장
                    beansByType.put(clazz, bean);
                    beansByName.put(getBeanName(clazz), bean);

                    System.out.println("✅ Bean 생성: " + clazz.getSimpleName());

                } catch (Exception e) {
                    System.err.println("❌ Bean 생성 실패: " + clazz.getSimpleName());
                    System.err.println("   원인: " + e.getMessage());
                }
            } else {
                // Component가 아닌 클래스들 (어노테이션, 일반 클래스 등)
                System.out.println("⏭️  Component 아님: " + clazz.getSimpleName());
            }
        }

        System.out.println("🎯 총 " + beansByType.size() + "개의 Bean 생성 완료!\n");
    }

    /**
     * Component 어노테이션 체크 - 여기서 필터링!
     */
    private boolean isComponent(Class<?> clazz) {
        // 🚫 어노테이션 자체는 Bean이 될 수 없음
        if (clazz.isAnnotation()) {
            return false;
        }

        // 🚫 인터페이스도 Bean이 될 수 없음 (일반적으로)
        if (clazz.isInterface()) {
            return false;
        }

        // 🚫 추상 클래스도 Bean이 될 수 없음
        if (java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }

        // ✅ Component 어노테이션이 붙은 클래스만
        return clazz.isAnnotationPresent(Component.class) ||
                clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Repository.class) ||
                clazz.isAnnotationPresent(Controller.class) ||
                clazz.isAnnotationPresent(RestController.class);
    }

    /**
     * Bean 이름 생성 (클래스명의 첫 글자를 소문자로)
     */
    private String getBeanName(Class<?> clazz) {
        String simpleName = clazz.getSimpleName();
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1);
    }

    /**
     * 타입으로 Bean 찾기
     */
    public Object findBeanByType(Class<?> requiredType) {
        // 정확한 타입 매치 먼저 확인
        Object exactMatch = beansByType.get(requiredType);
        if (exactMatch != null) {
            return exactMatch;
        }

        // 상속/구현 관계 확인 (인터페이스 구현체 찾기)
        for (Map.Entry<Class<?>, Object> entry : beansByType.entrySet()) {
            if (requiredType.isAssignableFrom(entry.getKey())) {
                return entry.getValue();
            }
        }

        return null; // 못 찾으면 null
    }

    /**
     * 타입으로 Bean 가져오기 (제네릭 버전)
     */
    public <T> T getBean(Class<T> type) {
        Object bean = findBeanByType(type);
        if (bean != null) {
            return type.cast(bean);
        }
        return null;
    }

    /**
     * 이름으로 Bean 가져오기
     */
    public Object getBean(String name) {
        return beansByName.get(name);
    }

    /**
     * 모든 Bean 반환 (DI할 때 사용)
     */
    public Map<Class<?>, Object> getAllBeans() {
        return new HashMap<>(beansByType);
    }

    /**
     * 등록된 Bean 목록 출력 (디버깅용)
     */
    public void printAllBeans() {
        System.out.println("📋 등록된 Bean 목록:");
        for (Map.Entry<String, Object> entry : beansByName.entrySet()) {
            System.out.println("  - " + entry.getKey() + " -> " + entry.getValue().getClass().getSimpleName());
        }
    }

    public List<Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        List<Object> result = new ArrayList<>();

        // beansByName 사용 (일관성 유지)
        for (Map.Entry<String, Object> entry : beansByName.entrySet()) {
            Object bean = entry.getValue();
            if (bean.getClass().isAnnotationPresent(annotationType)) {
                result.add(bean);
                System.out.println("🎯 " + annotationType.getSimpleName() + " 발견: " + bean.getClass().getSimpleName());
            }
        }

        return result;
    }
}