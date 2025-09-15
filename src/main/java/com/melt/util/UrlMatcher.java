package com.melt.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlMatcher {

    // /users/{id} 패턴과 /users/123 실제 URL 매칭
    public static boolean matches(String pattern, String url) {
        String regex = pattern.replaceAll("\\{[^}]+\\}", "([^/]+)");
        // /users/{id} → /users/([^/]+) 정규표현식으로 변환
        return url.matches(regex);
    }

    // 실제 URL에서 PathVariable 값 추출
    public static Map<String, String> extractPathVariables(String pattern, String url) {
        Map<String, String> variables = new HashMap<>();

        // {id} 같은 변수명 찾기
        Pattern varPattern = Pattern.compile("\\{([^}]+)\\}");
        Matcher varMatcher = varPattern.matcher(pattern);

        // 실제 값 추출하는 정규표현식
        String regex = pattern.replaceAll("\\{[^}]+\\}", "([^/]+)");
        Pattern urlPattern = Pattern.compile(regex);
        Matcher urlMatcher = urlPattern.matcher(url);

        if (urlMatcher.matches()) {
            int groupIndex = 1;
            varMatcher.reset();
            while (varMatcher.find()) {
                String varName = varMatcher.group(1); // {id}에서 id 추출
                String value = urlMatcher.group(groupIndex++); // 실제 값
                variables.put(varName, value);
            }
        }

        return variables;
    }
}