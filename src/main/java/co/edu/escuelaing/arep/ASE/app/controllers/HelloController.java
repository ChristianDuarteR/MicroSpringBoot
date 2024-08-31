package co.edu.escuelaing.arep.ASE.app.controllers;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RequestParam;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/SpringECI")
    public String index() {
        return "Greetings from SpringECI!";
    }
    @GetMapping("/suma")
    public String suma(@RequestParam(value = "num1") int num1, @RequestParam(value = "num2") int num2){
        int suma= num1 + num2;
        return String.valueOf(suma);
    }
}
