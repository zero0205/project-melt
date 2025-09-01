package com.melt.context;

import java.util.List;

/**
 * Spring 컨테이너의 핵심 - 모든 기능을 통합 관리
 */
public class ApplicationContext {
    private final ComponentScanner scanner;
    private final BeanFactory beanFactory;
    private final DependencyInjector injector;

    public ApplicationContext() {
        this.scanner = new ComponentScanner();
        this.beanFactory = new BeanFactory();
        this.injector = new DependencyInjector();
    }

    /**
     * Spring 컨테이너 초기화 - 스캔부터 DI까지 한번에!
     */
    public void scan(String basePackage) {
        System.out.println("🚀 ApplicationContext 초기화 시작!");
        System.out.println("📦 Base Package: " + basePackage);
        System.out.println("=" + "=".repeat(50));

        try {
            // 1단계: Component Scan
            System.out.println("\n1️⃣ Component Scan 단계");
            List<Class<?>> classes = scanner.scanComponents(basePackage);

            // 2단계: Bean 생성
            System.out.println("\n2️⃣ Bean Factory 단계");
            beanFactory.createAndRegisterBeans(classes);

            // 3단계: 의존성 주입
            System.out.println("\n3️⃣ Dependency Injection 단계");
            injector.injectDependencies(beanFactory);

            // 4단계: 검증
            System.out.println("\n4️⃣ 검증 단계");
            beanFactory.printAllBeans();
            injector.verifyInjections(beanFactory);

            System.out.println("\n🎉 ApplicationContext 초기화 완료!");
            System.out.println("=" + "=".repeat(50));

        } catch (Exception e) {
            System.err.println("❌ ApplicationContext 초기화 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Bean 가져오기 - 타입으로 조회
     */
    public <T> T getBean(Class<T> type) {
        return beanFactory.getBean(type);
    }

    /**
     * Bean 가져오기 - 이름으로 조회
     */
    public Object getBean(String name) {
        return beanFactory.getBean(name);
    }

    /**
     * Bean 존재 여부 확인
     */
    public boolean containsBean(Class<?> type) {
        return beanFactory.findBeanByType(type) != null;
    }

    /**
     * 등록된 모든 Bean 정보 출력
     */
    public void printBeanInfo() {
        beanFactory.printAllBeans();
    }
}