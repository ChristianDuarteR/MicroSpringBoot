package co.edu.escuelaing.arep.ASE.app;

import com.sun.jdi.Method;

public class ControllerMethod {
    Object instance;
    java.lang.reflect.Method method;

    ControllerMethod(Object instance, java.lang.reflect.Method method) {
        this.instance = instance;
        this.method = method;
    }
}