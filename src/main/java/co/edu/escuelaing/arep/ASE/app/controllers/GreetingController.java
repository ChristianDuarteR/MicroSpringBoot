package co.edu.escuelaing.arep.ASE.app.controllers;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RequestParam;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;
import com.google.gson.Gson;
import netscape.javascript.JSObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "Mundo") String name) {
        return "<html><body><h1>" + String.format(template, name) + "</h1></body></html>";
    }

    @GetMapping("/users")
    public String users() {

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"users\": [");

        jsonBuilder.append("{\"id\": 1, \"name\": \"Christian Duarte\"}, ");
        jsonBuilder.append("{\"id\": 2, \"name\": \"Jane Doe\"}, ");
        jsonBuilder.append("{\"id\": 3, \"name\": \"Lucas González\"}, ");
        jsonBuilder.append("{\"id\": 4, \"name\": \"Emma Martínez\"}, ");
        jsonBuilder.append("{\"id\": 5, \"name\": \"Sofia Perez\"}");

        jsonBuilder.append("]}");
        System.out.println(jsonBuilder);

        return jsonBuilder.toString();
    }

}