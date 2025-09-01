package com.melt.context;

import com.melt.annotation.Autowired;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * @Autowired 어노테이션을 처리하여 의존성을 자동으로 주입하는 클래스
 */
public class DependencyInjector {

    /**
     * 모든 Bean에 대해 @Autowired 필드를 찾아서 의존성 주입
     */
    public void injectDependencies(BeanFactory beanFactory) {
        System.out.println("\n💉 의존성 주입 시작!");

        // BeanFactory에서 모든 Bean 가져오기
        Map<Class<?>, Object> allBeans = beanFactory.getAllBeans();

        // 각 Bean마다 의존성 주입 처리
        for (Map.Entry<Class<?>, Object> entry : allBeans.entrySet()) {
            Class<?> beanClass = entry.getKey();
            Object beanInstance = entry.getValue();

            System.out.println("🔍 " + beanClass.getSimpleName() + " 의존성 검사 중...");
            injectIntoBean(beanInstance, beanFactory);
        }

        System.out.println("✅ 의존성 주입 완료!\n");
    }

    /**
     * 특정 Bean 객체의 @Autowired 필드들에 의존성 주입
     */
    private void injectIntoBean(Object bean, BeanFactory beanFactory) {
        Class<?> beanClass = bean.getClass();

        // 클래스의 모든 필드(변수) 가져오기
        Field[] fields = beanClass.getDeclaredFields();

        for (Field field : fields) {
            // @Autowired 어노테이션이 붙은 필드만 처리
            if (field.isAnnotationPresent(Autowired.class)) {
                System.out.println("  🎯 @Autowired 발견: " + field.getName() + " (타입: " + field.getType().getSimpleName() + ")");

                try {
                    // 의존성 주입 수행
                    performInjection(bean, field, beanFactory);

                } catch (Exception e) {
                    System.err.println("  ❌ 주입 실패: " + field.getName());
                    System.err.println("     원인: " + e.getMessage());
                }
            }
        }
    }

    /**
     * 실제 의존성 주입 수행
     */
    private void performInjection(Object bean, Field field, BeanFactory beanFactory) throws Exception {
        // 1. 필드가 필요로 하는 타입 확인
        Class<?> fieldType = field.getType();

        // 2. BeanFactory에서 해당 타입의 Bean 찾기
        Object dependency = beanFactory.findBeanByType(fieldType);

        if (dependency != null) {
            // 3. private 필드에 접근 가능하도록 설정
            field.setAccessible(true);

            // 4. 의존성 주입! (핵심 코드)
            field.set(bean, dependency);

            System.out.println("  ✅ 주입 성공: " + field.getName() + " <- " + dependency.getClass().getSimpleName());

        } else {
            System.err.println("  ❌ 의존성 없음: " + fieldType.getSimpleName() + " 타입의 Bean을 찾을 수 없음");
        }
    }

    /**
     * 의존성 주입 결과 검증 (테스트용)
     */
    public void verifyInjections(BeanFactory beanFactory) {
        System.out.println("🔬 의존성 주입 결과 검증:");

        Map<Class<?>, Object> allBeans = beanFactory.getAllBeans();

        for (Object bean : allBeans.values()) {
            System.out.println("\n📦 " + bean.getClass().getSimpleName() + " 검증:");

            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    try {
                        field.setAccessible(true);
                        Object injectedValue = field.get(bean);

                        if (injectedValue != null) {
                            System.out.println("  ✅ " + field.getName() + " = " + injectedValue.getClass().getSimpleName() + " (주입됨)");
                        } else {
                            System.out.println("  ❌ " + field.getName() + " = null (주입 실패)");
                        }

                    } catch (Exception e) {
                        System.out.println("  ⚠️ " + field.getName() + " 검증 실패: " + e.getMessage());
                    }
                }
            }
        }
    }
}