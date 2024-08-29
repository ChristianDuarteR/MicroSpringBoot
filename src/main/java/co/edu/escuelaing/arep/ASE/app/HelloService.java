package co.edu.escuelaing.arep.ASE.app;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;

@RestController
public class HelloService {
    @GetMapping("/hello")
    public static String hello() {
        return "Hello World!";
    }

    @GetMapping("/index")
    public static String index() {
        return "Greetings from Spring Boot!";
    }
    @GetMapping("/suma")
    public static String suma() {
        int suma= 2+3;
        return String.valueOf(suma);
    }
}
