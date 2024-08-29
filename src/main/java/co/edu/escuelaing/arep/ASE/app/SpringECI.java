package co.edu.escuelaing.arep.ASE.app;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SpringECI {

    public SpringECI() throws MalformedURLException {
    }

    public static void main(String[] args) throws ClassNotFoundException, MalformedURLException, InvocationTargetException, IllegalAccessException {
        Class c = Class.forName(args[0]);
        Map<String, Method> services = new HashMap<>();

        if (c.isAnnotationPresent(RestController.class)) {
            Method[] methods = c.getDeclaredMethods();
            for (Method m : methods) {
                if (m.isAnnotationPresent(GetMapping.class)) {
                    String value = m.getAnnotation(GetMapping.class).value();
                    services.put(value, m);
                }
            }
        }

        //CODIGO CHAMBON PARA EJEMPLO

        URL serviceUrl1 = new URL("http://localhost:8080/Api/hello");
        printMethod(serviceUrl1, services);
        URL serviceUrl2 = new URL("http://localhost:8080/Api/index");
        printMethod(serviceUrl2, services);
        URL serviceUrl3 = new URL("http://localhost:8080/Api/suma");
        printMethod(serviceUrl3, services);

    }

    public static void printMethod(URL serviceUrl,Map services) throws InvocationTargetException, IllegalAccessException {
        String path = serviceUrl.getPath();
        System.out.println("Path: "+ path);
        String serviceName = path.substring(4);
        System.out.println("Service name: " + serviceName);
        Method ms = (Method) services.get(serviceName);
        services.get(serviceName);
        System.out.println("Respuesta Servicio: "+ ms.invoke(ms));
    }

}
