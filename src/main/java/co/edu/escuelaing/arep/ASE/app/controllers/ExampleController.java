package co.edu.escuelaing.arep.ASE.app.controllers;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;

@RestController
public class ExampleController {
    @GetMapping("/bye")
    public static String adios(){
        return "Good Bye";
    }
    @GetMapping("/cedula")
    public static String cedula(){
        return "1000224420";
    }
}
