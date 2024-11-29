package cat.uvic.teknos.shoeshop.services.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class CustomObjectMapper {
    private static final ObjectMapper mapper;

    static {
        SimpleModule addressTypeMapping = new SimpleModule()
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Address.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.Address.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Inventory.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.Inventory.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Model.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.Model.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.ShoeStore.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.ShoeStore.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Shoe.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.Shoe.class);

        mapper = new ObjectMapper();
        mapper
                .registerModule(new JavaTimeModule()) // For Java 8 date/time types
                .registerModule(addressTypeMapping);  // Register custom module for abstract type mappings
    }

    public static ObjectMapper get() {
        return mapper;
    }
}
