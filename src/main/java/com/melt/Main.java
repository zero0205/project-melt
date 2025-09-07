package com.melt;

import com.melt.web.servlet.DispatcherServlet;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class Main {
    public static void main(String[] args) {
        // Jetty 서버 생성
        Server server = new Server(8080);

        // ServletContext 설정
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        //DispatcherServlet 등록
        context.addServlet(DispatcherServlet.class, "/*");

        server.setHandler(context);

        try {
            System.out.println("=".repeat(50));
            System.out.println("🚀 DispatcherServlet 서버 시작! (Jetty)");
            System.out.println("=".repeat(50));
            System.out.println("📍 테스트 URL:");
            System.out.println("   http://localhost:8080/hello");
            System.out.println("   http://localhost:8080/test");
            System.out.println("   http://localhost:8080/anything");
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