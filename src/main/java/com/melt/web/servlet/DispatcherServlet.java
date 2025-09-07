package com.melt.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DispatcherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest("GET", req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handleRequest("POST", req, resp);
    }

    private void handleRequest(String method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();

        System.out.println("📥 요청: " + method + " " + uri);

        resp.setContentType("text/plain; charset=UTF-8");

        if("/hello".equals(uri)){
            resp.getWriter().write("Hello World from " + method + " method! 🎉");
        } else if("/test".equals(uri)) {
            resp.getWriter().write("Test successful with " + method + " method! ✅");
        } else {
            resp.getWriter().write("Page not found: " + method + " " + uri + " ❌");
        }
    }
}
