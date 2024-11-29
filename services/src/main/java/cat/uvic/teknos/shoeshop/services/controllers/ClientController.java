package cat.uvic.teknos.shoeshop.services.controllers;

import cat.uvic.teknos.shoeshop.models.Address;
import cat.uvic.teknos.shoeshop.models.Client;
import cat.uvic.teknos.shoeshop.models.ShoeStore;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;
import cat.uvic.teknos.shoeshop.services.clients.IOUtils;
import cat.uvic.teknos.shoeshop.services.utils.CustomObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.BufferedReader;
import java.io.InputStreamReader;

import static cat.uvic.teknos.shoeshop.services.clients.IOUtils.readLine;
import static java.lang.System.out;

public class ClientController implements Controller {

    private final ClientRepository clientRepository;
    private final ObjectMapper mapper;

    public ClientController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
        this.mapper = CustomObjectMapper.get();
    }

    @Override
    public String get(int id) {
        Client client = clientRepository.get(id);

        if (client == null) {
            return "{\"error\": \"Client not found\"}";
        }

        try {
            return mapper.writeValueAsString(client);  // Convert client to JSON
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting client to JSON", e);
        }
    }


    @Override
    public String get() {
        var clients = clientRepository.getAll();

        try {
            return mapper.writeValueAsString(clients);  // Convert list of clients to JSON
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting client list to JSON", e);
        }
    }

    @Override
    public void post(String json) {
        try {
            // Substituir Client.class per la implementació concreta (per exemple, JdbcClient)
            cat.uvic.teknos.shoeshop.domain.jdbc.models.Client client =
                    mapper.readValue(json, cat.uvic.teknos.shoeshop.domain.jdbc.models.Client.class);

            // Validar el client deserialitzat
            validateClient(client);

            // Crear o validar Address
            if (client.getAddresses() != null) {
                Address address = client.getAddresses();
                if (address.getLocation() == null || address.getLocation().isEmpty()) {
                    throw new IllegalArgumentException("Address location is required");
                }
            }

            // Crear o validar ShoeStore
            if (client.getShoeStores() != null) {
                ShoeStore shoeStore = client.getShoeStores();
                if (shoeStore.getName() == null || shoeStore.getName().isEmpty()) {
                    throw new IllegalArgumentException("ShoeStore name is required");
                }
                if (shoeStore.getLocation() == null || shoeStore.getLocation().isEmpty()) {
                    throw new IllegalArgumentException("ShoeStore location is required");
                }
                if (shoeStore.getOwner() == null || shoeStore.getOwner().isEmpty()) {
                    throw new IllegalArgumentException("ShoeStore owner is required");
                }
            }

            // Guardar al repositori
            clientRepository.save(client);
            System.out.println("Client created successfully: " + mapper.writeValueAsString(client));

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Validation error: " + e.getMessage(), e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error deserializing JSON: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error during POST: " + e.getMessage(), e);
        }
    }

    @Override
    public void put(int id, String json) {
        try {
            Client existingClient = clientRepository.get(id);
            if (existingClient == null) {
                throw new RuntimeException("Client not found with id " + id);
            }

            Client updates = mapper.readValue(json, Client.class);

            // Actualitzar només els camps proporcionats
            if (updates.getDni() != null) {
                existingClient.setDni(updates.getDni());
            }
            if (updates.getName() != null) {
                existingClient.setName(updates.getName());
            }
            if (updates.getPhone() != null) {
                existingClient.setPhone(updates.getPhone());
            }
            if (updates.getAddresses() != null) {
                existingClient.setAddresses(updates.getAddresses());
            }
            if (updates.getShoeStores() != null) {
                existingClient.setShoeStores(updates.getShoeStores());
            }

            // Guardar els canvis
            clientRepository.save(existingClient);
        } catch (Exception e) {
            throw new RuntimeException("Error during PUT: " + e.getMessage(), e);
        }
    }

    // Funció auxiliar per validar dades
    private void validateClient(Client client) {
        if (client.getDni() == null || client.getDni().isEmpty()) {
            throw new IllegalArgumentException("DNI is required");
        }
        if (client.getName() == null || client.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
    }

    @Override
    public void delete(int id) {
        Client existingClient = clientRepository.get(id);

        if (existingClient != null) {
            clientRepository.delete(existingClient);  // Delete the client
        } else {
            throw new RuntimeException("Client not found");
        }
    }
}
