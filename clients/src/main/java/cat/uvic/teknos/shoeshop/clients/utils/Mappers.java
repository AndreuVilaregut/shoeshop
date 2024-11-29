package cat.uvic.teknos.shoeshop.clients.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cat.uvic.teknos.shoeshop.models.Model;

public class Mappers {
    private static final ObjectMapper mapper;

    static  {
        SimpleModule addressTypeMapping = new SimpleModule()
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Address.class,  cat.uvic.teknos.shoeshop.clients.dto.AddressDto.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Inventory.class, cat.uvic.teknos.shoeshop.clients.dto.InventoryDto.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Model.class, cat.uvic.teknos.shoeshop.clients.dto.ModelDto.class)  // Mapeja Model a ModelImpl
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Client.class,  cat.uvic.teknos.shoeshop.clients.dto.ClientDto.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Shoe.class, cat.uvic.teknos.shoeshop.clients.dto.ShoeDto.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.Supplier.class,  cat.uvic.teknos.shoeshop.clients.dto.SupplierDto.class)
                .addAbstractTypeMapping(cat.uvic.teknos.shoeshop.models.ShoeStore.class,  cat.uvic.teknos.shoeshop.clients.dto.ShoeStoreDto.class);

        // Creem un mòdul que inclou l'anterior i altres configuracions
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule())
                .registerModule(addressTypeMapping);
    }

    public static ObjectMapper get() {
        return mapper;
    }
}
