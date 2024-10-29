package cat.uvic.teknos.shoeshop.services;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import cat.uvic.teknos.shoeshop.services.controllers.ClientController;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;

public interface RequestRouter {

    RawHttpResponse<?> execRequest(RawHttpRequest request);

    /*private final RawHttp rawHttp = new RawHttp();
    private final ClientController clientController;

    public RequestRouter(ClientRepository clientRepository) {
        this.clientController = new ClientController(clientRepository);
    }

    public RawHttpResponse<?> route(RawHttpRequest request) {
        String method = request.getMethod().toString();
        String path = request.getUri().getPath();

        try {
            if (method.equals("GET")) {
                if (path.equals("/client")) {
                    return clientController.getAllClients();
                } else if (path.startsWith("/client/")) {
                    int clientId = extractClientId(path);
                    return clientController.getClient(clientId);
                }
            } else if (method.equals("POST")) {
                if (path.equals("/client")) {
                    return clientController.createClient(request);
                }
            } else if (method.equals("PUT")) {
                if (path.startsWith("/client/")) {
                    int clientId = extractClientId(path);
                    return clientController.updateClient(clientId, request);
                }
            } else if (method.equals("DELETE")) {
            if (path.startsWith("/client/")) {
                int clientId = extractClientId(path);
                return clientController.deleteClient(clientId);
            }
        }

            return createResponse(404, "Not Found");
        } catch (NumberFormatException e) {
            return createResponse(400, "Invalid Client ID");
        } catch (Exception e) {

            return createResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }

    private int extractClientId(String path) throws NumberFormatException {
        return Integer.parseInt(path.substring("/client/".length()));
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
            try {
                return rawHttp.parseResponse("HTTP/1.1 500 Internal Server Error\n" +
                        "Content-Type: text/plain\n" +
                        "Content-Length: 21\n" +
                        "\n" +
                        "Internal Server Error");
            } catch (Exception ignored) {
                return null;
            }
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
    }*/
}
