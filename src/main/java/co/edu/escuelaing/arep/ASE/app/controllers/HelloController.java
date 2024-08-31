package co.edu.escuelaing.arep.ASE.app.controllers;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RequestParam;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public static String hello() {
        return "Hello World!";
    }

    @GetMapping("/index")
    public static String index() {
        return "Greetings from ECI Spring Boot!";
    }
    @GetMapping("/suma")
    public static String suma(@RequestParam(value = "num1") int num1, @RequestParam(value = "num2") int num2){
        int suma= num1 + num2;
        return String.valueOf(suma);
    }
}
