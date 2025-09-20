package com.melt.web.mapping;

import com.melt.annotation.Controller;
import com.melt.annotation.RestController;
import com.melt.annotation.RequestMapping;
import com.melt.annotation.RequestMethod;
import com.melt.annotation.GetMapping;
import com.melt.annotation.PostMapping;
import com.melt.annotation.PutMapping;
import com.melt.annotation.DeleteMapping;
import com.melt.annotation.PatchMapping;
import com.melt.annotation.RequestParam;
import com.melt.annotation.PathVariable;
import com.melt.annotation.RequestBody;
import com.melt.web.method.HandlerMethod;
import com.melt.util.UrlMatcher;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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

        // 1) 기존 @Controller 처리 (기존 코드 유지)
        if (clazz.isAnnotationPresent(Controller.class)){
            registerRequestMappingMethods(controller, clazz);
        }

        // 2) 새로운 @RestController 처리 (신규 추가)
        if (clazz.isAnnotationPresent(RestController.class)){
            registerRestControllerMethods(controller, clazz);
        }
    }

    // 기존 @RequestMapping 처리 로직 (변경 없음)
    private void registerRequestMappingMethods(Object controller, Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(RequestMapping.class)){
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                String url = mapping.value();
                RequestMethod httpMethod = mapping.method();

                String mappingKey = httpMethod.name() + ":" + url;
                HandlerMethod handlerMethod = new HandlerMethod(controller, method);
                mappings.put(mappingKey, handlerMethod);

                System.out.println("✅ @RequestMapping 등록: " + url + " -> " + handlerMethod);
            }
        }
    }

    // 새로운 @GetMapping, @PostMapping 등 처리 로직
    private void registerRestControllerMethods(Object controller, Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String mappingKey = null;
            String url = null;

            // @GetMapping 처리
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                url = mapping.value();
                mappingKey = "GET:" + url;
            }
            // @PostMapping 처리
            else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = method.getAnnotation(PostMapping.class);
                url = mapping.value();
                mappingKey = "POST:" + url;
            }
            // @PutMapping 처리
            else if (method.isAnnotationPresent(PutMapping.class)) {
                PutMapping mapping = method.getAnnotation(PutMapping.class);
                url = mapping.value();
                mappingKey = "PUT:" + url;
            }
            // @DeleteMapping 처리
            else if (method.isAnnotationPresent(DeleteMapping.class)) {
                DeleteMapping mapping = method.getAnnotation(DeleteMapping.class);
                url = mapping.value();
                mappingKey = "DELETE:" + url;
            }
            // @PatchMapping 처리
            else if (method.isAnnotationPresent(PatchMapping.class)) {
                PatchMapping mapping = method.getAnnotation(PatchMapping.class);
                url = mapping.value();
                mappingKey = "PATCH:" + url;
            }

            if (mappingKey != null) {
                HandlerMethod handlerMethod = new HandlerMethod(controller, method);
                mappings.put(mappingKey, handlerMethod);
                System.out.println("✅ REST API 매핑 등록: " + url + " -> " + handlerMethod);
            }
        }
    }

    public HandlerMethod getHandler(String url, String httpMethod) {
        // 1. 정확한 매칭 먼저 시도
        String mappingKey = httpMethod + ":" + url;
        HandlerMethod handler = mappings.get(mappingKey);
        if (handler != null) {
            return handler;
        }

        // 2. PathVariable이 있는 패턴 매칭
        for (Map.Entry<String, HandlerMethod> entry : mappings.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(httpMethod + ":")) {
                String pattern = key.substring(httpMethod.length() + 1);
                if (UrlMatcher.matches(pattern, url)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    public HandlerMethod getHandler(String url) {
        return getHandler(url, "GET");
    }


    // 🔥 핵심 로직: 메소드 파라미터 자동 준비
    public Object[] prepareMethodArguments(HandlerMethod handler,
                                          HttpServletRequest req, String uri) throws Exception {

        Method method = handler.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        // PathVariable 값들 미리 추출
        String urlPattern = getUrlPattern(handler);
        Map<String, String> pathVars = UrlMatcher.extractPathVariables(urlPattern, uri);

        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];

            if (param.isAnnotationPresent(RequestParam.class)) {
                // @RequestParam 처리
                RequestParam annotation = param.getAnnotation(RequestParam.class);
                String paramName = annotation.value();
                String paramValue = req.getParameter(paramName);

                args[i] = convertToType(paramValue, param.getType());

            } else if (param.isAnnotationPresent(PathVariable.class)) {
                // @PathVariable 처리
                PathVariable annotation = param.getAnnotation(PathVariable.class);
                String varName = annotation.value();
                String varValue = pathVars.get(varName);

                args[i] = convertToType(varValue, param.getType());

            } else if (param.isAnnotationPresent(RequestBody.class)) {
                // @RequestBody 처리 (JSON)
                String jsonBody = readRequestBody(req);
                args[i] = parseJson(jsonBody, param.getType());
            }
        }

        return args;
    }

    // HandlerMethod에서 URL 패턴 추출
    private String getUrlPattern(HandlerMethod handler) {
        Method method = handler.getMethod();
        if (method.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping mapping = method.getAnnotation(RequestMapping.class);
            return mapping.value();
        }
        return "";
    }

    // 타입 변환 유틸리티
    private Object convertToType(String value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType == String.class) return value;
        if (targetType == Integer.class || targetType == int.class)
            return Integer.parseInt(value);
        if (targetType == Long.class || targetType == long.class)
            return Long.parseLong(value);
        if (targetType == Boolean.class || targetType == boolean.class)
            return Boolean.parseBoolean(value);

        return value; // 기본값
    }

    // Request Body 읽기
    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    // 간단한 JSON 파싱 (실제로는 Jackson 사용)
    private Object parseJson(String json, Class<?> targetType) {
        // 매우 간단한 User 클래스용 JSON 파싱
        if (targetType.getSimpleName().equals("User")) {
            // 실제로는 User 객체를 생성해야 하지만 여기서는 JSON 문자열 반환
            return json;
        }
        return json; // 기본적으로 JSON 문자열 반환
    }

    // 등록된 모든 매핑 정보 출력 (디버깅용)
    public void printMappings() {
        System.out.println("\n📋 등록된 핸들러 매핑:");
        mappings.forEach((url, handler) ->
                System.out.println("  " + url + " -> " + handler));
        System.out.println();
    }
}
