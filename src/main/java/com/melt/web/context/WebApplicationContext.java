package com.melt.web.context;

import com.melt.annotation.Controller;
import com.melt.annotation.RestController;
import com.melt.context.ApplicationContext;
import com.melt.web.mapping.HandlerMapping;

import java.util.List;
import java.util.ArrayList;

public class WebApplicationContext {
    private HandlerMapping handlerMapping;

    public void refresh() throws Exception{
        System.out.println("🔄 WebApplicationContext 초기화 시작...");

        // 1. ApplicationContext 초기화
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.scan("com.melt");    // Component Scan + Bean 등록 + DI

        // 2. 컨트롤러 Bean들 수집 (@Controller + @RestController 모두)
        List<Object> allControllers = new ArrayList<>();

        // 기존 @Controller 스캔
        List<Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);
        allControllers.addAll(controllers);

        // 새로운 @RestController 스캔 (추가)
        List<Object> restControllers = applicationContext.getBeansWithAnnotation(RestController.class);
        allControllers.addAll(restControllers);

        System.out.println("📋 발견된 컨트롤러: @Controller(" + controllers.size() + "), @RestController(" + restControllers.size() + ")");

        // 3. HandlerMapping에 등록
        handlerMapping = new HandlerMapping();
        handlerMapping.scanControllers(allControllers);

        System.out.println("✅ WebApplicationContext 초기화 완료!");
    }

    public HandlerMapping getHandlerMapping() {
        return handlerMapping;
    }
}
