package cat.uvic.teknos.shoeshop.services.controllers;

import cat.uvic.teknos.shoeshop.models.Shoe;
import cat.uvic.teknos.shoeshop.repositories.ShoeRepository;
import cat.uvic.teknos.shoeshop.services.utils.CustomObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShoeController implements Controller {

    private final ShoeRepository shoeRepository;  // Canviat a ShoeRepository
    private final ObjectMapper mapper;

    public ShoeController(ShoeRepository shoeRepository) {
        this.shoeRepository = shoeRepository;  // Assignat el repositori directament
        this.mapper = CustomObjectMapper.get();  // ObjectMapper personalitzat
    }

    @Override
    public String get(int id) {
        Shoe shoe = shoeRepository.get(id);  // Crida al repositori sense getShoeRepository()

        if (shoe == null) {
            return "{\"error\": \"Shoe not found\"}";
        }

        try {
            return mapper.writeValueAsString(shoe);  // Convertim l'objecte a JSON
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting shoe to JSON", e);
        }
    }

    @Override
    public String get() {
        var shoes = shoeRepository.getAll();  // Crida al repositori sense getShoeRepository()

        try {
            return mapper.writeValueAsString(shoes);  // Convertim la llista a JSON
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting shoe list to JSON", e);
        }
    }

    @Override
    public void post(String json) {
        try {
            cat.uvic.teknos.shoeshop.domain.jdbc.models.Shoe shoe = mapper.readValue(json, cat.uvic.teknos.shoeshop.domain.jdbc.models.Shoe.class);
            shoeRepository.save(shoe);  // Guardem el model en el repositori
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    @Override
    public void put(int id, String json) {
        Shoe existingShoe = shoeRepository.get(id);  // Obtenim el Shoe existent

        if (existingShoe == null) {
            throw new RuntimeException("Shoe not found");
        }

        try {
            // Deserialitzem el JSON per actualitzar el Shoe
            cat.uvic.teknos.shoeshop.domain.jdbc.models.Shoe shoeUpdated = mapper.readValue(json, cat.uvic.teknos.shoeshop.domain.jdbc.models.Shoe.class);
            shoeUpdated.setId(id);

            // Actualitzem les propietats del Shoe existent
            existingShoe.setPrice(shoeUpdated.getPrice());
            existingShoe.setColor(shoeUpdated.getColor());
            existingShoe.setSize(shoeUpdated.getSize());
            existingShoe.setModels(shoeUpdated.getModels());
            existingShoe.setInventories(shoeUpdated.getInventories());

            shoeRepository.save(existingShoe);  // Guardem les actualitzacions
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
    }

    @Override
    public void delete(int id) {
        Shoe existingShoe = shoeRepository.get(id);  // Obtenim el Shoe existent

        if (existingShoe != null) {
            shoeRepository.delete(existingShoe);  // Eliminem el Shoe
        } else {
            throw new RuntimeException("Shoe not found");
        }
    }
}
