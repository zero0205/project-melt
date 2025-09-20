package com.melt;

import com.melt.web.servlet.DispatcherServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Main {
    public static void main(String[] args) {
        // Jetty 서버 생성
        Server server = new Server(8080);

        // ServletContext 설정
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        //DispatcherServlet 등록
        ServletHolder servletHolder = new ServletHolder(new DispatcherServlet());
        servletHolder.setInitOrder(1);
        context.addServlet(servletHolder, "/*");
        server.setHandler(context);

        try {
            System.out.println("=".repeat(50));
            System.out.println("🚀 DispatcherServlet 서버 시작! (Jetty)");
            System.out.println("=".repeat(50));
            System.out.println("📍 REST API 테스트 URL:");
            System.out.println("   GET  http://localhost:8080/api/users");
            System.out.println("   GET  http://localhost:8080/api/users/123");
            System.out.println("   POST http://localhost:8080/api/users");
            System.out.println("   PUT  http://localhost:8080/api/users/123");
            System.out.println("   DELETE http://localhost:8080/api/users/123");
            System.out.println("=".repeat(50));
            System.out.println("⏹️  서버 중지: Ctrl+C");
            System.out.println("=".repeat(50));

            server.start();
            server.join();

        } catch (Exception e) {
            System.err.println("❌ 서버 시작 실패: " + e.getMessage());
            e.printStackTrace();
        }

    }
}