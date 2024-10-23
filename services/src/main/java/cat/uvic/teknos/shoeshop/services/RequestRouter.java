package cat.uvic.teknos.shoeshop.services;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import cat.uvic.teknos.shoeshop.services.controllers.ClientController;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;

public class RequestRouter {
    private final RawHttp rawHttp = new RawHttp();
    private final ClientController clientController;

    public RequestRouter(ClientRepository clientRepository) {
        this.clientController = new ClientController(clientRepository);
    }

    public RawHttpResponse<?> route(RawHttpRequest request) {
        String method = request.getMethod().toString();
        String path = request.getUri().getPath();

        if (method.equals("GET")) {
            if (path.equals("/client")) {
                return clientController.getAllClients();
            } else if (path.startsWith("/client/")) {
                int clientId = Integer.parseInt(path.substring("/client/".length()));
                return clientController.getClient(clientId);
            }
        } else if (method.equals("POST")) {
            if (path.equals("/client")) {
                return clientController.createClient(request);
            }
        } else if (method.equals("PUT")) {
            if (path.startsWith("/client/")) {
                int clientId = Integer.parseInt(path.substring("/client/".length()));
                return clientController.updateClient(clientId, request);
            }
        } else if (method.equals("DELETE")) {
            if (path.startsWith("/client/")) {
                int clientId = Integer.parseInt(path.substring("/client/".length()));
                return clientController.deleteClient(clientId);
            }
        }

        return createResponse(404, "Not Found");
    }

    private RawHttpResponse<?> createResponse(int statusCode, String body) {
        try {
            return rawHttp.parseResponse("HTTP/1.1 " + statusCode + " " + getStatusMessage(statusCode) + "\n" +
                    "Content-Type: text/plain\n" +
                    "Content-Length: " + body.length() + "\n" +
                    "\n" +
                    body);
        } catch (Exception e) {
            System.err.println("Error creating response: " + e.getMessage());
            return null;
        }
    }

    private String getStatusMessage(int statusCode) {
        switch (statusCode) {
            case 200: return "OK";
            case 201: return "Created";
            case 204: return "No Content";
            case 400: return "Bad Request";
            case 404: return "Not Found";
            default: return "Internal Server Error";
        }
    }

}