package co.edu.escuelaing.arep.ASE.app;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RequestParam;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SpringECI {

    private static Map<String, ControllerMethod> services = new HashMap<>();

    private static final int PORT = 8080;

    public SpringECI() {

    }

    public static void main(String[] args) {
        SpringECI server = new SpringECI();
        String basePackage = "co.edu.escuelaing.arep.ASE.app.controllers";
        server.loadControllers(basePackage);
        server.start();
    }

    /**
     * Carga del disco duro los CONTROLADORES
     * @param basePackage
     */
    private void loadControllers(String basePackage) {
        String path = basePackage.replace('.', '/');
        InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            System.err.println("No se encontró el paquete: " + basePackage);
            return;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        reader.lines().forEach(file -> {
            if (file.endsWith(".class")) {
                String className = basePackage + "." + file.substring(0, file.lastIndexOf('.'));
                try {
                    Class<?> cls = Class.forName(className);
                    if (cls.isAnnotationPresent(RestController.class)) {
                        Object instance = cls.getDeclaredConstructor().newInstance();
                        for (java.lang.reflect.Method method : cls.getDeclaredMethods()) {
                            if (method.isAnnotationPresent(GetMapping.class)) {
                                GetMapping annotation = method.getAnnotation(GetMapping.class);
                                String route = annotation.value();
                                services.put(route, new ControllerMethod(instance, method));
                            } // Aca podrias poner el PUT, POST, DELETE
                        }
                        System.out.println("Controlador " + className + " cargado.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Servidor iniciado en el puerto " + PORT);
            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        OutputStream out = clientSocket.getOutputStream();

        String requestLine = in.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return;
        }

        System.out.println("Solicitud recibida: " + requestLine);
        String[] tokens = requestLine.split(" ");
        if (tokens.length < 2) {
            return;
        }

        String method = tokens[0];
        String pathWithParams = tokens[1];
        String path = pathWithParams.split("\\?")[0];
        String query = "";
        if (pathWithParams.contains("?")) {
            query = pathWithParams.split("\\?")[1];
        }

        if (!path.startsWith("/api/")) {
            serveStatic(out, path);
            return;
        }

        ControllerMethod cm = services.get(path.substring(4));
        if (cm != null) {
            try {
                java.lang.reflect.Method controllerMethod = cm.method;
                java.lang.reflect.Parameter[] parameters = controllerMethod.getParameters();
                Object[] args = new Object[parameters.length];

                Map<String, String> queryParams = parseQuery(query);

                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                        RequestParam rp = parameters[i].getAnnotation(RequestParam.class);
                        String value = queryParams.getOrDefault(rp.value(), rp.defaultValue());
                        args[i] = value;
                    }
                }

                Object response = controllerMethod.invoke(cm.instance, args);
                String responseBody = response.toString();
                String contentType = "text/html";

                if (controllerMethod.getReturnType().equals(String.class)) {
                    contentType = "text/html";
                }

                String httpResponse = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + responseBody.length() + "\r\n" +
                        "\r\n" +
                        responseBody;
                out.write(httpResponse.getBytes());
            } catch (Exception e) {
                String errorResponse = "HTTP/1.1 500 Internal Server Error\r\n\r\n" +
                        "Error en el servidor.";
                out.write(errorResponse.getBytes());
                e.printStackTrace();
            }
        } else {
            String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\n" +
                    "Página no encontrada.";
            out.write(notFoundResponse.getBytes());
        }
    }

    private Map<String, String> parseQuery(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query == null || query.isEmpty()) {
            return queryParams;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            } else if (keyValue.length == 1) {
                queryParams.put(keyValue[0], "");
            }
        }
        return queryParams;
    }


    private void serveStatic(OutputStream out, String path) throws IOException {
        File file = new File("static" + path.substring("/static".length()));
        if (file.exists() && !file.isDirectory()) {
            String contentType = Files.probeContentType(file.toPath());
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            String httpResponse = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + fileBytes.length + "\r\n" +
                    "\r\n";
            out.write(httpResponse.getBytes());
            out.write(fileBytes);
        } else {
            String notFoundResponse = "HTTP/1.1 404 Not Found\r\n\r\n" +
                    "Archivo no encontrado.";
            out.write(notFoundResponse.getBytes());
        }
    }
}