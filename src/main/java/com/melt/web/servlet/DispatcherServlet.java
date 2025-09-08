package com.melt.web.servlet;

import com.melt.web.mapping.HandlerMapping;
import com.melt.web.method.HandlerMethod;
import com.melt.controller.UserController;
import com.melt.controller.TestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

public class DispatcherServlet extends HttpServlet {
    private HandlerMapping handlerMapping;

    @Override
    public void init() throws ServletException {
        System.out.println("🚀 DispatcherServlet 초기화 시작...");

        // HandlerMapping 초기화
        handlerMapping = new HandlerMapping();
        System.out.println("✅ HandlerMapping 객체 생성 완료");

        // Controller들 생성
        UserController userController = new UserController();
        TestController testController = new TestController();
        System.out.println("✅ Controller 객체들 생성 완료");
        System.out.println("  - UserController: " + userController.getClass().getName());
        System.out.println("  - TestController: " + testController.getClass().getName());

        // Controller들 등록
        System.out.println("🔍 Controller 스캔 시작...");
        handlerMapping.scanControllers(Arrays.asList(
                userController,
                testController
        ));
        System.out.println("🔍 Controller 스캔 완료");

        // 등록된 매핑 정보 출력
        handlerMapping.printMappings();

        System.out.println("✅ DispatcherServlet 초기화 완료!");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest("GET", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest("POST", req, resp);
    }

    // 🔥 하드코딩 제거! HandlerMapping 사용!
    private void handleRequest(String method, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String uri = req.getRequestURI();

        System.out.println("📥 요청: " + method + " " + uri);

        resp.setContentType("text/plain; charset=UTF-8");

        // HandlerMapping에서 적절한 핸들러 찾기
        HandlerMethod handler = handlerMapping.getHandler(uri);

        if (handler != null) {
            try {
                System.out.println("🎯 핸들러 실행: " + handler);

                // 실제 Controller 메소드 호출!
                Object result = handler.getMethod().invoke(handler.getController());
                resp.getWriter().write(String.valueOf(result));

            } catch (Exception e) {
                System.err.println("❌ 핸들러 실행 오류: " + e.getMessage());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().write("Internal Server Error: " + e.getMessage());
            }
        } else {
            System.out.println("❌ 핸들러 없음: " + uri);
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("404 Not Found: " + uri);
        }
    }
}