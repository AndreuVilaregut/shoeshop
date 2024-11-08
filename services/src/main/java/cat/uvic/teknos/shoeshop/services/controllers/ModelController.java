package cat.uvic.teknos.shoeshop.services.controllers;

import cat.uvic.teknos.shoeshop.repositories.ModelRepository;
import cat.uvic.teknos.shoeshop.services.utils.Mappers;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import cat.uvic.teknos.shoeshop.models.Model;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

public class ModelController {

    private final ModelRepository modelRepository;
    private final ObjectMapper objectMapper;

    public ModelController(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
        this.objectMapper = Mappers.get();
    }

    public RawHttpResponse<Void> getModel(int modelId) {
        Model model = modelRepository.get(modelId);
        if (model != null) {
            return createResponse(200, "Model details for ID " + modelId + ": " + model.toString());
        } else {
            return createResponse(404, "Model not found");
        }
    }

    public RawHttpResponse<Void> getAllModels() {
        Set<Model> models = modelRepository.getAll();
        if (models.isEmpty()) {
            return createResponse(204, "No models available");
        }

        try {
            String body = objectMapper.writeValueAsString(models);
            return createResponse(200, body);
        } catch (IOException e) {
            return createResponse(500, "Error serializing models: " + e.getMessage());
        }
    }

    public RawHttpResponse<Void> createModel(RawHttpRequest request) {
        try {
            List<String> contentTypeList = request.getHeaders().get("Content-Type");
            String contentType = (contentTypeList != null && !contentTypeList.isEmpty()) ? contentTypeList.get(0) : null;

            if (contentType == null || !contentType.equals("application/json")) {
                return createResponse(415, "Unsupported Media Type");
            }

            String body = readRequestBody(request);
            Model newModel = objectMapper.readValue(body, Model.class);

            if (newModel.getName() == null || newModel.getName().isEmpty()) {
                return createResponse(400, "Model name is required");
            }

            modelRepository.save(newModel);
            return createResponse(201, "Model created with ID: " + newModel.getId());
        } catch (IOException e) {
            return createResponse(400, "Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return createResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }

    public RawHttpResponse<Void> updateModel(int modelId, RawHttpRequest request) {
        Model existingModel = modelRepository.get(modelId);
        if (existingModel == null) {
            return createResponse(404, "Model not found");
        }

        try {
            String body = readRequestBody(request);
            Model updatedModel = objectMapper.readValue(body, Model.class);

            existingModel.setName(updatedModel.getName());
            existingModel.setBrand(updatedModel.getBrand());
            modelRepository.save(existingModel);

            return createResponse(200, "Model updated: " + existingModel.getName());
        } catch (Exception e) {
            return createResponse(400, "Invalid request: " + e.getMessage());
        }
    }

    public RawHttpResponse<Void> deleteModel(int modelId) {
        Model modelToDelete = modelRepository.get(modelId);
        if (modelToDelete == null) {
            return createResponse(404, "Model not found");
        }

        modelRepository.delete(modelToDelete);
        return createResponse(200, "Model with ID " + modelId + " deleted");
    }

    private String readRequestBody(RawHttpRequest request) throws IOException {
        if (request.getBody().isEmpty()) {
            throw new IOException("Request body is missing");
        }

        InputStream inputStream = request.getBody().get().asRawStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder bodyBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            bodyBuilder.append(line).append("\n");
        }
        return bodyBuilder.toString().trim();
    }

    private RawHttpResponse<Void> createResponse(int statusCode, String body) {
        try {
            RawHttp rawHttp = new RawHttp();
            byte[] bodyBytes = body.getBytes();
            return rawHttp.parseResponse("HTTP/1.1 " + statusCode + " " + getStatusMessage(statusCode) + "\n" +
                    "Content-Type: text/plain\n" +
                    "Content-Length: " + bodyBytes.length + "\n\n" +
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
