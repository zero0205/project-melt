package com.melt.web.mapping;

import com.melt.annotation.Controller;
import com.melt.annotation.RequestMapping;
import com.melt.web.method.HandlerMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerMapping {
    private final Map<String, HandlerMethod> mappings = new HashMap<>();

    // 컨트롤러들을 스캔해서 매핑 정보 등록
    public void scanControllers(List<Object> controllers) {
        System.out.println("📋 스캔할 Controller 개수: " + controllers.size());

        for (Object controller : controllers) {
            System.out.println("🔍 Controller 처리 중: " + controller.getClass().getSimpleName());
            registerController(controller);
        }

        System.out.println("📋 최종 등록된 매핑 개수: " + mappings.size());
    }

    // 개별 컨트롤러 등록
    public void registerController(Object controller) {
        Class<?> clazz = controller.getClass();

        if (clazz.isAnnotationPresent(Controller.class)){
            System.out.println("🔍 Controller 스캔: " + clazz.getSimpleName());

            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                    String url = mapping.value();

                    HandlerMethod handlerMethod = new HandlerMethod(controller, method);
                    mappings.put(url, handlerMethod);

                    System.out.println("✅ 매핑 등록: " + url + " -> " + handlerMethod);
                }
            }
        }
    }

    // URL로 핸들러 찾기
    public HandlerMethod getHandler(String url) {
        return mappings.get(url);
    }

    // 등록된 모든 매핑 정보 출력 (디버깅용)
    public void printMappings() {
        System.out.println("\n📋 등록된 핸들러 매핑:");
        mappings.forEach((url, handler) ->
                System.out.println("  " + url + " -> " + handler));
        System.out.println();
    }
}
