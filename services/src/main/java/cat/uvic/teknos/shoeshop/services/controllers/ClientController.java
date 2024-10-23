package cat.uvic.teknos.shoeshop.services.controllers;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import cat.uvic.teknos.shoeshop.models.Client;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.Optional;

public class ClientController {
    private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper; // ObjectMapper com a variable d'instància

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.objectMapper = new ObjectMapper(); // Inicialització de l'ObjectMapper
    }

    public RawHttpResponse<?> getClient(int clientId) {
        Client client = clientRepository.get(clientId);
        if (client != null) {
            return createResponse(200, "Client details for ID " + clientId + ": " + client.toString());
        } else {
            return createResponse(404, "Client not found");
        }
    }

    public RawHttpResponse<?> getAllClients() {
        Set<Client> clients = clientRepository.getAll();
        if (clients.isEmpty()) {
            return createResponse(204, "No clients available");
        }

        StringBuilder body = new StringBuilder("Clients: ");
        for (Client client : clients) {
            body.append("\nID: ").append(client.getId()).append(", Name: ").append(client.getName());
        }
        return createResponse(200, body.toString());
    }

    public RawHttpResponse<?> createClient(RawHttpRequest request) {
        try {
            List<String> contentTypeList = request.getHeaders().get("Content-Type");
            String contentType = (contentTypeList != null && !contentTypeList.isEmpty()) ? contentTypeList.get(0) : null;

            if (contentType == null || !contentType.equals("application/json")) {
                return createResponse(415, "Unsupported Media Type");
            }

            String body = readRequestBody(request);
            Client newClient = objectMapper.readValue(body, Client.class);

            clientRepository.save(newClient);

            return createResponse(201, "Client created with ID: " + newClient.getId());
        } catch (IOException e) {
            return createResponse(400, "Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return createResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }


    private String readRequestBody(RawHttpRequest request) throws IOException {
        InputStream inputStream = request.getBody().get().asRawStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder bodyBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            bodyBuilder.append(line).append("\n");
        }
        return bodyBuilder.toString().trim();
    }

    public RawHttpResponse<?> updateClient(int clientId, RawHttpRequest request) {
        Client existingClient = clientRepository.get(clientId);
        if (existingClient == null) {
            return createResponse(404, "Client not found");
        }

        try {
            String body = readRequestBody(request);
            existingClient.setName(body);
            clientRepository.save(existingClient);
            return createResponse(200, "Client updated: " + body);
        } catch (Exception e) {
            return createResponse(400, "Invalid request: " + e.getMessage());
        }
    }

    public RawHttpResponse<?> deleteClient(int clientId) {
        Client client = clientRepository.get(clientId);
        if (client == null) {
            return createResponse(404, "Client not found");
        }

        clientRepository.delete(client);
        return createResponse(200, "Client with ID " + clientId + " deleted");
    }

    private RawHttpResponse<?> createResponse(int statusCode, String body) {
        try {
            RawHttp rawHttp = new RawHttp();
            return rawHttp.parseResponse("HTTP/1.1 " + statusCode + " " + getStatusMessage(statusCode) + "\n" +
                    "Content-Type: text/plain\n" +
                    "Content-Length: " + body.length() + "\n\n" +
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
            case 415: return "Unsupported Media Type";
            default: return "Internal Server Error";
        }
    }
}
