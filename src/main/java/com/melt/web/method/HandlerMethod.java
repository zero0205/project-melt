package com.melt.web.method;

import java.lang.reflect.Method;

public class HandlerMethod {
    private final Object controller;
    private final Method method;

    public HandlerMethod(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }

    public Object getController() {
        return controller;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return controller.getClass().getSimpleName() + "." + method.getName() + "()";
    }
}
