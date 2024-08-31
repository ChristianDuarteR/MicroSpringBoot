package co.edu.escuelaing.arep.ASE.app;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RequestParam;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;
import co.edu.escuelaing.arep.ASE.app.classes.ControllerMethod;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
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
                            } // Aca pondria el PUT, POST, DELETE SI TUVIERA UNO
                         }
                        System.out.println("Controlador " + className + " cargado.");
                    }
                } catch (Exception e) {
                    System.out.println("Se genero un error Durante la carga de Controladores: "+ e.getCause());
                }
            }
        });
        System.out.println("Todos los Controladores Fueron Cargados con exito\n " + services.toString() + "Listo para funcionar");
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

        // String restMethod = tokens[0]; Posible Implementacion de POST, PUT, DELETE (Por ahora solo GET)
        String pathWithParams = tokens[1];
        String path = pathWithParams.split("\\?")[0];
        String query = "";
        if (pathWithParams.contains("?")) {
            query = pathWithParams.split("\\?")[1];
        }

        if (!path.startsWith("/api/")) {
            serveStatic(out, path);
        }else handleApiRequest(path, query, out);

    }

    private void handleApiRequest(String path, String query, OutputStream out) throws IOException {
        ControllerMethod controllerMethod = services.get(path.substring(4));
        if (controllerMethod != null) {
            try {
                java.lang.reflect.Method method = controllerMethod.getMethod();
                java.lang.reflect.Parameter[] parameters = method.getParameters();
                Map<String, String> queryParams = parseQuery(query);
                Object[] args = resolveArguments(parameters, queryParams);

                Object response = method.invoke(controllerMethod.getInstance(), args);
                String responseBody = response.toString();
                String contentType = "text/html"; // Cambia según el tipo de respuesta
                sendJSONResponse(responseBody, contentType, "200 OK", out);

            } catch (Exception e) {
                sendErrorResponse(out, "500 Internal Server Error", "Error en el servidor.");
                e.printStackTrace();
            }
        } else {
            sendErrorResponse(out, "404 Not Found", "Página no encontrada.");
        }
    }

    private Object[] resolveArguments(java.lang.reflect.Parameter[] parameters, Map<String, String> queryParams) {
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            java.lang.reflect.Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(RequestParam.class)) {
                RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                String paramValue = queryParams.get(requestParam.value());
                Class<?> paramType = parameter.getType();
                if (paramType == int.class) {
                    args[i] = Integer.parseInt(paramValue);
                } else if (paramType == double.class) {
                    args[i] = Double.parseDouble(paramValue);
                } else {
                    args[i] = paramValue;
                }
            }
        }
        return args;
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
        if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js") || path.endsWith(".png")) {
            File file = new File("src/main/resources/static" + path);

            if (file.exists() && !file.isDirectory()) {
                String contentType = getContentType(file.getName());
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
        } else {
            String badRequestResponse = "HTTP/1.1 400 Bad Request\r\n\r\n" +
                    "Solicitud inválida.";
            out.write(badRequestResponse.getBytes());
        }
    }

    private String getContentType(String filePath) {
        if (filePath.endsWith(".html")) {
            return "text/html";
        } else if (filePath.endsWith(".css")) {
            return "text/css";
        } else if (filePath.endsWith(".js")) {
            return "application/javascript";
        } else if (filePath.endsWith(".png")) {
            return "image/png";
        } else if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filePath.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "application/json";
        }
    }

    public void sendJSONResponse(String responseBody,String contentType, String responseCode, OutputStream out ) throws IOException {
        String httpResponse = "HTTP/1.1 "+responseCode+"\r\n" +
                "Content-Type: " + contentType + "\r\n" +
                "Content-Length: " + responseBody.length() + "\r\n" +
                "\r\n" +
                responseBody;
        out.write(httpResponse.getBytes());
    }

    private void sendErrorResponse(OutputStream out, String status, String message) throws IOException {
        String errorResponse = "HTTP/1.1 " + status + "\r\n\r\n" +
                message;
        out.write(errorResponse.getBytes());
    }
}