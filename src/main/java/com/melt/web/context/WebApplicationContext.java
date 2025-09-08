package com.melt.web.context;

import com.melt.annotation.Controller;
import com.melt.context.ApplicationContext;
import com.melt.web.mapping.HandlerMapping;

import java.util.List;

public class WebApplicationContext {
    private HandlerMapping handlerMapping;

    public void refresh() throws Exception{
        System.out.println("🔄 WebApplicationContext 초기화 시작...");

        // 1. ApplicationContext 초기화
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.scan("com.melt");    // Component Scan + Bean 등록 + DI

        // 2. @Controller 어노테이션이 붙은 Bean들만 필터링
        List<Object> controllers = applicationContext.getBeansWithAnnotation(Controller.class);

        // 3. HandlerMapping에 등록
        handlerMapping = new HandlerMapping();
        handlerMapping.scanControllers(controllers);

        System.out.println("✅ WebApplicationContext 초기화 완료!");
    }

    public HandlerMapping getHandlerMapping() {
        return handlerMapping;
    }
}
