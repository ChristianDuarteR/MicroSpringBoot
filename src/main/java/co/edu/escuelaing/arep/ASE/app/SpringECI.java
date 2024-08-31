package co.edu.escuelaing.arep.ASE.app;

import co.edu.escuelaing.arep.ASE.app.annotations.GetMapping;
import co.edu.escuelaing.arep.ASE.app.annotations.RequestParam;
import co.edu.escuelaing.arep.ASE.app.annotations.RestController;
import co.edu.escuelaing.arep.ASE.app.classes.ControllerMethod;

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
            return;
        }

        ControllerMethod controllerMethod = services.get(path.substring(4));
        if (controllerMethod != null) {
            try {
                java.lang.reflect.Method method = controllerMethod.getMethod();
                java.lang.reflect.Parameter[] parameters = method.getParameters(); // Parametros
                Map<String, String> queryParams = parseQuery(query); // Argumentos

                Object[] args = new Object[parameters.length];
                Class<?>[] paramTypes = method.getParameterTypes();
                RequestParam rq;
                String paramValue;
                String value;

                for (int i = 0; i < parameters.length; i++) {
                    if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                        rq = parameters[i].getAnnotation(RequestParam.class);
                        value = rq.value();
                        paramValue = queryParams.get(value);
                        if(paramTypes[i] == int.class){
                            args[i] = Integer.parseInt(paramValue);
                        } else if (paramTypes[i] == double.class) {
                            args[i] = Double.parseDouble(paramValue);
                        } else  {
                            args[i] = paramValue;
                        }
                    }
                }

                Object response = method.invoke(controllerMethod.getInstance(), args);
                String responseBody = response.toString();
                String contentType = "text/html";

                if (method.getReturnType().equals(String.class)) {
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

        if (path.contains("html")|| path.contains("css") || path.contains("js") || path.contains("png")) {
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
        } else {
            String badRequestResponse = "HTTP/1.1 400 Bad Request\r\n\r\n" +
                    "Solicitud invalida.";
            out.write(badRequestResponse.getBytes());
        }
    }

}