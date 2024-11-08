package cat.uvic.teknos.shoeshop.services.controllers;

import cat.uvic.teknos.shoeshop.repositories.ShoeStoreRepository;
import cat.uvic.teknos.shoeshop.services.utils.Mappers;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import cat.uvic.teknos.shoeshop.models.ShoeStore;
import cat.uvic.teknos.shoeshop.repositories.ShoeStoreRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

public class ShoeStoreController {

    private final ShoeStoreRepository shoeStoreRepository;
    private final ObjectMapper objectMapper;

    public ShoeStoreController(ShoeStoreRepository shoeStoreRepository) {
        this.shoeStoreRepository = shoeStoreRepository;
        this.objectMapper = Mappers.get();
    }

    public RawHttpResponse<?> getShoeStore(int shoeStoreId) {
        ShoeStore shoeStore = (ShoeStore) shoeStoreRepository.get(shoeStoreId);
        if (shoeStore != null) {
            return createResponse(200, "ShoeStore details for ID " + shoeStoreId + ": " + shoeStore.toString());
        } else {
            return createResponse(404, "ShoeStore not found");
        }
    }

    public RawHttpResponse<?> getAllShoeStores() {
        Set<ShoeStore> shoeStores = shoeStoreRepository.getAll();
        if (shoeStores.isEmpty()) {
            return createResponse(204, "No shoe stores available");
        }

        StringBuilder body = new StringBuilder("Shoe Stores: ");
        for (cat.uvic.teknos.shoeshop.models.ShoeStore store : shoeStores) {
            body.append("\nID: ").append(store.getId()).append(", Name: ").append(store.getName());
        }
        return createResponse(200, body.toString());
    }

    public RawHttpResponse<?> createShoeStore(RawHttpRequest request) {
        try {
            List<String> contentTypeList = request.getHeaders().get("Content-Type");
            String contentType = (contentTypeList != null && !contentTypeList.isEmpty()) ? contentTypeList.get(0) : null;

            if (contentType == null || !contentType.equals("application/json")) {
                return createResponse(415, "Unsupported Media Type");
            }

            String body = readRequestBody(request);
            ShoeStore newShoeStore = objectMapper.readValue(body, ShoeStore.class);

            if (newShoeStore.getName() == null || newShoeStore.getName().isEmpty()) {
                return createResponse(400, "ShoeStore name is required");
            }

            shoeStoreRepository.save(newShoeStore);
            return createResponse(201, "ShoeStore created with ID: " + newShoeStore.getId());
        } catch (IOException e) {
            return createResponse(400, "Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return createResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }

    public RawHttpResponse<?> updateShoeStore(int shoeStoreId, RawHttpRequest request) {
        ShoeStore existingShoeStore = (ShoeStore) shoeStoreRepository.get(shoeStoreId);
        if (existingShoeStore == null) {
            return createResponse(404, "ShoeStore not found");
        }

        try {
            String body = readRequestBody(request);
            ShoeStore updatedShoeStore = objectMapper.readValue(body, ShoeStore.class);

            existingShoeStore.setName(updatedShoeStore.getName());
            existingShoeStore.setOwner(updatedShoeStore.getOwner());
            existingShoeStore.setLocation(updatedShoeStore.getLocation());
            existingShoeStore.setSuppliers(updatedShoeStore.getSuppliers());
            existingShoeStore.setClients(updatedShoeStore.getClients());
            existingShoeStore.setInventories(updatedShoeStore.getInventories());

            shoeStoreRepository.save(existingShoeStore);
            return createResponse(200, "ShoeStore updated: " + existingShoeStore.toString());
        } catch (IOException e) {
            return createResponse(400, "Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return createResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }

    public RawHttpResponse<?> deleteShoeStore(int shoeStoreId) {
        ShoeStore shoeStoreToDelete = (ShoeStore) shoeStoreRepository.get(shoeStoreId);

        if (shoeStoreToDelete == null) {
            System.out.println("ShoeStore not found with ID " + shoeStoreId);
            return createResponse(404, "ShoeStore not found");
        }

        shoeStoreRepository.delete(shoeStoreToDelete);
        System.out.println("Deleted ShoeStore with ID " + shoeStoreId);
        return createResponse(200, "ShoeStore with ID " + shoeStoreId + " deleted");
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
