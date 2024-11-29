package cat.uvic.teknos.shoeshop.services.controllers;

import cat.uvic.teknos.shoeshop.domain.jdbc.models.Address;
import cat.uvic.teknos.shoeshop.repositories.AddressRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Set;

public class AddressController implements Controller {

    private final AddressRepository repository;
    private final ObjectMapper mapper;

    public AddressController(AddressRepository repository) {
        this.repository = repository;
        this.mapper = new ObjectMapper();
    }

    @Override
    public String get(int id) {
        cat.uvic.teknos.shoeshop.models.Address address = repository.get(id);
        try {
            return mapper.writeValueAsString(address);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    @Override
    public String get() {
        Set<cat.uvic.teknos.shoeshop.models.Address> addresses = repository.getAll();
        try {
            return mapper.writeValueAsString(addresses);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    @Override
    public void post(String value) {
        try {
            JsonNode rootNode = mapper.readTree(value);

            // Crear un objecte Address de la classe correcta
            cat.uvic.teknos.shoeshop.models.Address address = new cat.uvic.teknos.shoeshop.domain.jdbc.models.Address();

            if (rootNode.has("location")) {
                address.setLocation(rootNode.get("location").asText());
            }

            repository.save(address);

            // Creem una resposta amb l'adreça creada
            String responseJsonBody = mapper.writeValueAsString(address);

            // Retornem una resposta amb codi 201 Created
            System.out.println("HTTP/1.1 201 Created");
            System.out.println("Content-Type: application/json");
            System.out.println("Content-Length: " + responseJsonBody.length());
            System.out.println("\r\n" + responseJsonBody);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process JSON", e);
        }
    }

    @Override
    public void put(int id, String value) {
        try {
            // Parsear el JSON de entrada
            JsonNode rootNode = mapper.readTree(value);

            // Obtener la dirección existente desde el repositorio por su ID
            cat.uvic.teknos.shoeshop.models.Address address = repository.get(id);

            // Si la dirección no se encuentra, lanzar una excepción
            if (address == null) {
                throw new RuntimeException("Address with ID " + id + " not found");
            }

            // Actualizar el campo location de la dirección
            if (rootNode.has("location")) {
                address.setLocation(rootNode.get("location").asText());
            }

            // Guardar la dirección actualizada en el repositorio
            repository.save(address);

            // Crear el cuerpo de la respuesta en formato JSON con la dirección actualizada
            String responseJsonBody = mapper.writeValueAsString(address);

            // Enviar la respuesta con código 200 OK
            System.out.println("HTTP/1.1 200 OK");
            System.out.println("Content-Type: application/json");
            System.out.println("Content-Length: " + responseJsonBody.length());
            System.out.println("\r\n" + responseJsonBody);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process JSON", e);
        }
    }

    @Override
    public void delete(int id) {
        cat.uvic.teknos.shoeshop.models.Address address = repository.get(id);
        if (address == null) {
            throw new RuntimeException("Address not found");
        }

        repository.delete(address);

        String responseJsonBody = "{\"message\": \"Address deleted successfully\"}";

        System.out.println("HTTP/1.1 200 OK");
        System.out.println("Content-Type: application/json");
        System.out.println("Content-Length: " + responseJsonBody.length());
        System.out.println("\r\n" + responseJsonBody);
    }


}
