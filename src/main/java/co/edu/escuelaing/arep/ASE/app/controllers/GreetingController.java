package co.edu.escuelaing.arep.ASE.app.controllers;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RequestParam;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;
import co.edu.escuelaing.arep.ASE.app.classes.Greeting;
import co.edu.escuelaing.arep.ASE.app.classes.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "Mundo") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @GetMapping("/users")
    public String users() {
        ObjectMapper mapper = new ObjectMapper();

        List<User> users = new ArrayList<>();
        users.add(new User(1, "Christian Duarte"));
        users.add(new User(2, "Jane Doe"));
        users.add(new User(3, "Lucas González"));
        users.add(new User(4, "Emma Martínez"));
        users.add(new User(5, "Sofia Perez"));

        Map<String, Object> response = new HashMap<>();
        response.put("users", users);

        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

}