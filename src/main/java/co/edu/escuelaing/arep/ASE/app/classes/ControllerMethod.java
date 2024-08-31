package co.edu.escuelaing.arep.ASE.app.classes;

import java.lang.reflect.Method;

public class ControllerMethod {
    private Object instance;
    private  java.lang.reflect.Method method;

    public ControllerMethod(Object instance, java.lang.reflect.Method method) {
        this.instance = instance;
        this.method = method;
    }

    public Object getInstance() {
        return instance;
    }

    public Method getMethod() {
        return method;
    }
}