package com.melt.context;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * 지정된 패키지에서 Component 어노테이션이 붙은 클래스들을 찾는 스캐너
 */
public class ComponentScanner {
    /**
     * 패키지를 스캔해서 모든 클래스를 찾아 반환
     * @param packageName 스캔할 패키지명(예; "com.example")
     * @return 찾은 모든 클래스들의 리스트
     */
    public List<Class<?>> scanComponents(String packageName) {
        List<Class<?>> componentClasses = new ArrayList<>();

        try{
            System.out.println("패키지 스캔 시작: " + packageName);

            // 패키지명을 파일 경로로 변환(com.example -> com/example)
            String resourcePath = packageName.replace(".","/");
            System.out.println("리소스 경로: " + resourcePath);

            // 클래스패스에서 해당 패키지 디렉토리 찾기
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            URL resource = classLoader.getResource(resourcePath);

            if(resource == null) {
                System.err.println("패키지를 찾을 수 없습니다: " + packageName);
                return componentClasses;
            }

            File directory = new File(resource.toURI());
            System.out.println("디렉토리 경로: " + directory.getAbsolutePath());

            if(directory.exists() && directory.isDirectory()) {
                // 재귀적으로 하위 디렉토리까지 스캔
                scanDirectory(directory, packageName, componentClasses);
            } else {
                System.out.println("디렉토리가 존재하지 않습니다: " + directory.getPath());
            }

            System.out.println("스캔 완료! 총 " + componentClasses.size() + "개의 클래스 발견!!!");

        } catch (Exception e) {
            System.err.println("Component Scan 실패: " + e.getMessage());
            e.printStackTrace();
        }
        return componentClasses;
    }

    /*
    * 디렉토리를 재귀적으로 스캔하여 .class 파일들을 찾음
    */
    private void scanDirectory(File directory, String packageName, List<Class<?>> componentClasses) {
        File[] files = directory.listFiles();

        if(files == null) {
            return;
        }

        for(File file: files) {
            if(file.isDirectory()) {
                // 하위 디렉토리 재귀 스캔
                String subPackage = packageName + "." + file.getName();
                scanDirectory(file, subPackage, componentClasses);
            } else if (file.getName().endsWith(".class")) {
                // .class 파일 처리
                String className = packageName + "." + file.getName().replace(".class", "");

                try {
                    Class<?> clazz = Class.forName(className);
                    componentClasses.add(clazz);
                    System.out.println("클래스 발견: " + className);
                } catch(ClassNotFoundException e) {
                    System.out.println("클래스 로드 실패: " + className);
                } catch(NoClassDefFoundError e) {
                    System.out.println("클래스 정의 없음: " + className);
                }
            }
        }
    }
}
