package com.melt.web.servlet;

import com.melt.web.context.WebApplicationContext;
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
    private WebApplicationContext webApplicationContext;

    @Override
    public void init() throws ServletException {
        System.out.println("🚀 DispatcherServlet 초기화 시작...");

        try {
            // WebApplicationContext 생성 및 초기화
            webApplicationContext = new WebApplicationContext();
            webApplicationContext.refresh();

            // HandlerMapping 가져오기
            handlerMapping = webApplicationContext.getHandlerMapping();

            // 등록된 매핑 정보 출력
            handlerMapping.printMappings();
        } catch (Exception e) {
            throw new ServletException("DispatcherServlet 초기화 실패", e);
        }
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest("PUT", req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest("DELETE", req, resp);
    }
    
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        handleRequest("PATCH", req, resp);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String method = req.getMethod();

        if ("PATCH".equals(method)) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    // 🔥 단순화된 요청 처리 - HandlerMapping에게 위임
    private void handleRequest(String method, HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String uri = req.getRequestURI();
        String httpMethod = req.getMethod();

        System.out.println("📥 요청: " + httpMethod + " " + uri);

        // HandlerMapping에서 적절한 핸들러 찾기
        HandlerMethod handler = handlerMapping.getHandler(uri, httpMethod);

        if (handler == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("Handler not found for: " + httpMethod + " " + uri);
            return;
        }

        try {
            System.out.println("🎯 핸들러 실행: " + httpMethod + " " + handler);

            // HandlerMapping에게 파라미터 준비 위임
            Object[] methodArgs = handlerMapping.prepareMethodArguments(handler, req, uri);

            // 컨트롤러 메소드 실행
            Object result = handler.getMethod().invoke(handler.getController(), methodArgs);

            // JSON 응답으로 설정
            resp.setContentType("application/json;charset=UTF-8");
            resp.getWriter().write(String.valueOf(result));

        } catch (Exception e) {
            System.err.println("❌ 핸들러 실행 오류: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Internal Server Error: " + e.getMessage());
        }
    }
}