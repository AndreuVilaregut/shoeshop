package cat.uvic.teknos.shoeshop.services.controllers;

import cat.uvic.teknos.shoeshop.services.utils.Mappers;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import cat.uvic.teknos.shoeshop.models.Client;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import cat.uvic.teknos.shoeshop.models.Client;
import cat.uvic.teknos.shoeshop.models.ModelFactory;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;
import cat.uvic.teknos.shoeshop.repositories.RepositoryFactory;
import cat.uvic.teknos.shoeshop.services.utils.Mappers;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.Optional;

public class ClientController implements Controller {

    private final RepositoryFactory repositoryFactory;
    private final ModelFactory modelFactory;
    private final ObjectMapper mapper;

    public ClientController(RepositoryFactory repositoryFactory, ModelFactory modelFactory) {
        this.repositoryFactory = repositoryFactory;
        this.modelFactory = modelFactory;
        this.mapper = new ObjectMapper();
    }

    @Override
    public String get(int id) {
        Client client = repositoryFactory.getClientRepository().get(id);

        if (client == null) {
            return "{\"error\": \"Client not found\"}";
        }
        try {
            return mapper.writeValueAsString(client);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting client to JSON", e);
        }
    }

    @Override
    public String get() {
        var clients = repositoryFactory.getClientRepository().getAll();
        try {
            return mapper.writeValueAsString(clients);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting clients list to JSON", e);
        }
    }

    @Override
    public void post(String json) {
        ClientRepository repository = repositoryFactory.getClientRepository();

        ObjectMapper mapper = Mappers.get();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try{
            cat.uvic.teknos.shoeshop.domain.jdbc.models.Client client = mapper.readValue(json, cat.uvic.teknos.shoeshop.domain.jdbc.models.Client.class);
            repository.save(client);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(int id, String json) {
        ClientRepository repository = repositoryFactory.getClientRepository();
        Client existingClient = repository.get(id);

        if (existingClient == null) {
            throw new RuntimeException("Client not found");
        }

        ObjectMapper mapper = Mappers.get();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {

            Client clientUpdated = mapper.readValue(json, cat.uvic.teknos.shoeshop.domain.jdbc.models.Client.class);
            clientUpdated.setId(id);

            existingClient.setName(clientUpdated.getName());
            existingClient.setDni(clientUpdated.getDni());
            existingClient.setPhone(clientUpdated.getPhone());
            existingClient.setAddresses(clientUpdated.getAddresses());
            existingClient.setShoeStores(clientUpdated.getShoeStores());

            repository.save(existingClient);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        Client existingClient = repositoryFactory.getClientRepository().get(id);

        if (existingClient != null) {
            repositoryFactory.getClientRepository().delete(existingClient);
        } else {
            throw new RuntimeException("Client not found");
        }

    }



    /*private final ClientRepository clientRepository;
    private final ObjectMapper objectMapper; // ObjectMapper como variable de instancia

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.objectMapper = Mappers.get(); // Inicializa el ObjectMapper desde Mappers
    }

    public RawHttpResponse<?> getClient(int clientId) {
        Client client = clientRepository.get(clientId);
        if (client != null) {

            //json
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
            Client newClient = objectMapper.readValue(body, cat.uvic.teknos.shoeshop.domain.jpa.models.Client.class);

            if (newClient.getName() == null || newClient.getName().isEmpty()) {
                return createResponse(400, "Client name is required");
            }

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
        Client clientToDelete = clientRepository.get(clientId);

        if (clientToDelete == null) {
            System.out.println("Client not found with ID " + clientId);
            return createResponse(404, "Client not found");
        }

        clientRepository.delete(clientToDelete);
        System.out.println("Deleted client with ID " + clientId);
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
    }*/
}
