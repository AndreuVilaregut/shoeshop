package cat.uvic.teknos.shoeshop.services.controllers;

import cat.uvic.teknos.shoeshop.models.ShoeStore;
import cat.uvic.teknos.shoeshop.repositories.ShoeStoreRepository;
import cat.uvic.teknos.shoeshop.services.utils.CustomObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShoeStoreController implements Controller {

    private final ShoeStoreRepository shoeStoreRepository;  // The repository for ShoeStore
    private final ObjectMapper mapper;

    public ShoeStoreController(ShoeStoreRepository shoeStoreRepository) {
        this.shoeStoreRepository = shoeStoreRepository;  // Direct assignment of the repository
        this.mapper = CustomObjectMapper.get();  // Custom ObjectMapper
    }

    @Override
    public String get(int id) {
        ShoeStore shoeStore = shoeStoreRepository.get(id);  // Get the ShoeStore using the repository

        if (shoeStore == null) {
            return "{\"error\": \"ShoeStore not found\"}";
        }

        try {
            return mapper.writeValueAsString(shoeStore);  // Convert ShoeStore object to JSON
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting ShoeStore to JSON", e);
        }
    }

    @Override
    public String get() {
        var shoeStores = shoeStoreRepository.getAll();  // Get all ShoeStores from the repository

        try {
            return mapper.writeValueAsString(shoeStores);  // Convert list of ShoeStores to JSON
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting ShoeStore list to JSON", e);
        }
    }

    @Override
    public void post(String json) {
        try {
            // Deserialize JSON to a ShoeStore object
            ShoeStore shoeStore = mapper.readValue(json, ShoeStore.class);
            shoeStoreRepository.save(shoeStore);  // Save the ShoeStore model in the repository
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    @Override
    public void put(int id, String json) {
        ShoeStore existingShoeStore = shoeStoreRepository.get(id);  // Get the existing ShoeStore

        if (existingShoeStore == null) {
            throw new RuntimeException("ShoeStore not found");
        }

        try {
            // Deserialize JSON to update the ShoeStore
            ShoeStore shoeStoreUpdated = mapper.readValue(json, ShoeStore.class);
            shoeStoreUpdated.setId(id);

            // Update the properties of the existing ShoeStore
            existingShoeStore.setName(shoeStoreUpdated.getName());
            existingShoeStore.setOwner(shoeStoreUpdated.getOwner());
            existingShoeStore.setLocation(shoeStoreUpdated.getLocation());
            existingShoeStore.setInventories(shoeStoreUpdated.getInventories());
            existingShoeStore.setSuppliers(shoeStoreUpdated.getSuppliers());

            shoeStoreRepository.save(existingShoeStore);  // Save the updated ShoeStore
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    @Override
    public void delete(int id) {
        ShoeStore existingShoeStore = shoeStoreRepository.get(id);  // Get the existing ShoeStore

        if (existingShoeStore != null) {
            shoeStoreRepository.delete(existingShoeStore);  // Delete the ShoeStore
        } else {
            throw new RuntimeException("ShoeStore not found");
        }
    }
}
