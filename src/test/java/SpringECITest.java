import co.edu.escuelaing.arep.ASE.app.SpringECI;
import co.edu.escuelaing.arep.ASE.app.classes.ControllerMethod;
import co.edu.escuelaing.arep.ASE.app.classes.Greeting;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class SpringECITest {

    private SpringECI springECI;

    @BeforeEach
    public void setup() {
        springECI = new SpringECI();
    }

    @Test
    public void testLoadControllers() {
        String basePackage = "co.edu.escuelaing.arep.ASE.app.controllers";
        springECI.loadControllers(basePackage);

        Map<String, ControllerMethod> services = SpringECI.services;
        assertFalse(services.isEmpty(), "Controllers should be loaded");
        assertTrue(services.containsKey("/greeting"), "Greeting controller should be loaded");
    }

    @Test
    public void testHandleApiRequest() throws IOException {
        Socket mockSocket = Mockito.mock(Socket.class);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Mockito.when(mockSocket.getOutputStream()).thenReturn(out);

        springECI.loadControllers("co.edu.escuelaing.arep.ASE.app.controllers");

        springECI.handleApiRequest("/api/greeting", "name=Juan", out);

        String response = out.toString();
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Response should contain HTTP 200 OK");
        assertTrue(response.contains("Hello, Juan"), "Response should contain 'Hello, Juan'");
    }

    @Test
    public void testDetermineContentType() {
        String[] result = springECI.determineContentType(new Greeting(1, "Hola"));
        assertEquals("application/json", result[0], "Content type should be application/json");
        assertTrue(result[1].contains("\"content\":\"Hola\""), "Response body should contain 'Hola'");
    }

    @Test
    public void testSendJSONResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        springECI.sendJSONResponse("{\"content\":\"Hola\"}", "application/json", "200 OK", out);

        String response = out.toString();
        assertTrue(response.contains("HTTP/1.1 200 OK"), "Response should contain HTTP 200 OK");
        assertTrue(response.contains("Content-Type: application/json"), "Response should have JSON content type");
        assertTrue(response.contains("{\"content\":\"Hola\"}"), "Response should contain JSON body");
    }

    @Test
    public void testSendErrorResponse() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        springECI.sendErrorResponse(out, "404 Not Found", "Página no encontrada.");

        String response = out.toString();
        assertTrue(response.contains("HTTP/1.1 404 Not Found"), "Response should contain HTTP 404 Not Found");
        assertTrue(response.contains("Página no encontrada."), "Response should contain error message");
    }
}
