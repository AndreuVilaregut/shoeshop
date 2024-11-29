package cat.uvic.teknos.shoeshop.services;

import cat.uvic.teknos.shoeshop.services.controllers.Controller;
import cat.uvic.teknos.shoeshop.services.exceptions.ResourceNotFoundException;
import cat.uvic.teknos.shoeshop.services.exceptions.ServerErrorException;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class RequestRouterImplementation implements RequestRouter {
    private static final RawHttp rawHttp = new RawHttp();
    private final Map<String, Controller> controllers;

    public RequestRouterImplementation(Map<String, Controller> controllers) {
        this.controllers = controllers;
    }

    @Override
    public RawHttpResponse<?> route(RawHttpRequest request) {
        var path = request.getUri().getPath();
        var method = request.getMethod();
        var pathParts = path.split("/");
        var controllerName = pathParts.length > 1 ? pathParts[1] : null;
        String responseJsonBody = "";

        try {
            switch (controllerName) {
                case "address", "shoe","client","shoestore":
                    responseJsonBody = Manager(request, method, pathParts, responseJsonBody);
                    break;
                default:
                    throw new ResourceNotFoundException("Ruta no trobada");
            }

            return rawHttp.parseResponse("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + responseJsonBody.length() + "\r\n" +
                    "\r\n" + responseJsonBody);
        } catch (ResourceNotFoundException e) {
            responseJsonBody = "{\"error\": \"Not Found: " + e.getMessage() + "\"}";
            return rawHttp.parseResponse("HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + responseJsonBody.length() + "\r\n" +
                    "\r\n" + responseJsonBody);
        } catch (Exception e) {
            responseJsonBody = "{\"error\": \"Internal Server Error\"}";
            return rawHttp.parseResponse("HTTP/1.1 500 Internal Server Error\r\n" +
                    "Content-Type: application/json\r\n" +
                    "Content-Length: " + responseJsonBody.length() + "\r\n" +
                    "\r\n" + responseJsonBody);
        }
    }

    private String Manager(RawHttpRequest request, String method, String[] pathParts, String responseJsonBody) {
        var controller = controllers.get(pathParts[1]);

        try {
            if (method.equals("POST")) {
                var json = request.getBody().get().decodeBodyToString(Charset.defaultCharset());
                controller.post(json);
            } else if (method.equals("GET") && pathParts.length == 2) {
                responseJsonBody = controller.get();
            } else if (method.equals("GET") && pathParts.length == 3) {
                responseJsonBody = controller.get(Integer.parseInt(pathParts[2]));
            } else if (method.equals("DELETE") && pathParts.length == 3) {
                controller.delete(Integer.parseInt(pathParts[2]));
            } else if (method.equals("PUT")) {
                var json = request.getBody().get().decodeBodyToString(Charset.defaultCharset());
                controller.put(Integer.parseInt(pathParts[2]), json);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return responseJsonBody;
    }
}
