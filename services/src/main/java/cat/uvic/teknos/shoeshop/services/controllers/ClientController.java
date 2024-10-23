package cat.uvic.teknos.shoeshop.services.controllers;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import cat.uvic.teknos.shoeshop.models.Client;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;
import cat.uvic.teknos.shoeshop.services.RequestRouter;

import java.util.Set;

public class ClientController {
    private final ClientRepository clientRepository;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
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
            String body = request.getBody().toString();
            Client newClient = new cat.uvic.teknos.shoeshop.file.models.Client();
            newClient.setName(body);
            clientRepository.save(newClient);
            return createResponse(201, "Client created with ID: " + newClient.getId());
        } catch (Exception e) {
            return createResponse(400, "Invalid request: " + e.getMessage());
        }
    }

    public RawHttpResponse<?> updateClient(int clientId, RawHttpRequest request) {
        Client existingClient = clientRepository.get(clientId);
        if (existingClient == null) {
            return createResponse(404, "Client not found");
        }

        try {
            String body = request.getBody().toString();
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
            return rawHttp.parseResponse("HTTP/1.1 " + statusCode + " " + body + "\n" +
                    "Content-Type: text/plain\n" +
                    "Content-Length: " + body.length() + "\n\n" +
                    body);
        } catch (Exception e) {
            System.err.println("Error creating response: " + e.getMessage());
            return null;
        }
    }
}
