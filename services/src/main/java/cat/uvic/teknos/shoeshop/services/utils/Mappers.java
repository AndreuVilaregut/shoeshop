package cat.uvic.teknos.shoeshop.services.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class Mappers {
    private static final ObjectMapper mapper;

    static {
        SimpleModule addressTypeMapping = new SimpleModule()
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Inventory.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.Inventory.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Model.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.Model.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Client.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.Client.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Shoe.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.Shoe.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Supplier.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.Supplier.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.ShoeStore.class, cat.uvic.teknos.shoeshop.domain.jdbc.models.ShoeStore.class);

        mapper = new ObjectMapper();
        mapper
                .registerModule(new JavaTimeModule())
                .registerModule(addressTypeMapping);
    }

    public static ObjectMapper get() {
        return mapper;
    }
}
