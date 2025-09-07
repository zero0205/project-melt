package com.melt.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DispatcherServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();
        resp.setContentType("text/plain; charset=UTF-8");

        if("/hello".equals(uri)){
            resp.getWriter().write("Hello World!!!");
        } else {
            resp.getWriter().write("Page not found!!!");
        }
    }
}
