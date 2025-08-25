package com.melt;

import java.util.HashMap;

public class Container{
    private HashMap<String, Object> objects = new HashMap<>();

    public void registerBean(String name, Object obj) {
        objects.put(name, obj);
    }

    public Object getBean(String name) {
        Object obj = objects.get(name);
        if(obj == null){
            throw new RuntimeException("Bean not found or wrong type: " + name);
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(String name, Class<T> type) {
        Object obj = objects.get(name);
        if(type.isInstance(obj)){
            return (T)obj;
        }
        throw new RuntimeException("Bean not found or wrong type: " + name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> type) {
        for(Object obj : objects.values()){
            if(type.isInstance(obj)){
                return (T)obj;
            }
        }
        throw new RuntimeException("Bean not found or wrong type: " + type);
    }
}
