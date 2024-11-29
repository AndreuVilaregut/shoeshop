package cat.uvic.teknos.shoeshop.services.controllers;

import cat.uvic.teknos.shoeshop.domain.jdbc.models.Address;
import cat.uvic.teknos.shoeshop.repositories.AddressRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
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
        if (address == null) {
            return "{\"error\": \"Address not found\"}";
        }

        try {
            return mapper.writeValueAsString(address);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    @Override
    public String get() {
        Set<cat.uvic.teknos.shoeshop.models.Address> addresses = repository.getAll();

        // Comprovar si la llista està buida o null
        if (addresses == null || addresses.isEmpty()) {
            return "[]";  // Si no hi ha adreces, retorna una llista buida
        }

        try {
            // Convertir el conjunt a llista
            List<cat.uvic.teknos.shoeshop.models.Address> addressList = new ArrayList<>(addresses);

            // Debug: comprovar què conté la llista abans de serialitzar
            System.out.println("Address list size: " + addressList.size());

            // Convertim la llista a JSON
            String jsonResponse = mapper.writeValueAsString(addressList);

            // Debug: mostrar el JSON abans de retornar-lo
            System.out.println("Generated JSON: " + jsonResponse);

            return jsonResponse;  // Retornem la resposta completada
        } catch (JsonProcessingException e) {
            // Si hi ha un error en la serialització
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    @Override
    public void post(String value) {
        try {
            JsonNode rootNode = mapper.readTree(value);

            // Validar que la ubicació no sigui null o buida
            if (!rootNode.has("location") || rootNode.get("location").asText().isEmpty()) {
                throw new IllegalArgumentException("Location is required");
            }

            // Crear un objecte Address i assignar la ubicació
            Address address = new Address();
            address.setLocation(rootNode.get("location").asText());

            // Guardar l'adreça al repositori
            repository.save(address);

            // Crear una resposta amb l'adreça creada
            String responseJsonBody = mapper.writeValueAsString(address);

            // Retornar resposta amb codi 201 Created
            System.out.println("HTTP/1.1 201 Created");
            System.out.println("Content-Type: application/json");
            System.out.println("Content-Length: " + responseJsonBody.length());
            System.out.println("\r\n" + responseJsonBody);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process JSON", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation error: " + e.getMessage(), e);
        }
    }

    @Override
    public void put(int id, String value) {
        try {
            // Parsear el JSON de entrada
            JsonNode rootNode = mapper.readTree(value);

            // Obtenir l'adreça existent des del repositori per id
            cat.uvic.teknos.shoeshop.models.Address address = repository.get(id);

            // Si l'adreça no es troba, llançar una excepció
            if (address == null) {
                throw new RuntimeException("Address with ID " + id + " not found");
            }

            // Actualitzar la ubicació de l'adreça si es proporciona
            if (rootNode.has("location") && !rootNode.get("location").asText().isEmpty()) {
                address.setLocation(rootNode.get("location").asText());
            }

            // Guardar l'adreça actualitzada al repositori
            repository.save(address);

            // Crear la resposta amb l'adreça actualitzada
            String responseJsonBody = mapper.writeValueAsString(address);

            // Retornar resposta amb codi 200 OK
            System.out.println("HTTP/1.1 200 OK");
            System.out.println("Content-Type: application/json");
            System.out.println("Content-Length: " + responseJsonBody.length());
            System.out.println("\r\n" + responseJsonBody);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to process JSON", e);
        } catch (Exception e) {
            throw new RuntimeException("Error during PUT: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(int id) {
        // Obtenir l'adreça existent des del repositori per id
        cat.uvic.teknos.shoeshop.models.Address address = repository.get(id);

        if (address == null) {
            throw new RuntimeException("Address not found");
        }

        // Eliminar l'adreça del repositori
        repository.delete(address);

        // Crear una resposta amb l'èxit de l'operació
        String responseJsonBody = "{\"message\": \"Address deleted successfully\"}";

        System.out.println("HTTP/1.1 200 OK");
        System.out.println("Content-Type: application/json");
        System.out.println("Content-Length: " + responseJsonBody.length());
        System.out.println("\r\n" + responseJsonBody);
    }
}
