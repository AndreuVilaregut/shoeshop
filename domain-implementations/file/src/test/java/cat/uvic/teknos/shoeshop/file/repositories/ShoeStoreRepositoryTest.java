package cat.uvic.teknos.shoeshop.file.repositories;

import cat.uvic.teknos.shoeshop.file.models.ShoeStore;
import org.junit.jupiter.api.Test;

import javax.naming.Name;
import java.util.jar.Attributes;
import static org.junit.jupiter.api.Assertions.*;
class ShoeStoreRepositoryTest {
    @Test
    void save() {
        var path = System.getProperty("user.dir") +
                "/src/main/resources/shoestore.ser/";
        var repository = new ShoeStoreRepository(path);
        var shopTaradell = new ShoeStore();
        shopTaradell.setId(1);
        shopTaradell.setName("Corder Shop");
        shopTaradell.setInventoryId(1);
        shopTaradell.setLocation("Taradell");
        shopTaradell.setOwner("Martí Corder");
        repository.save(shopTaradell);
        assertTrue(shopTaradell.getId() > 0);
        assertNotNull(repository.get(shopTaradell.getId()));
        repository.load();
        assertNotNull(repository.get(shopTaradell.getId()));
    }
    void update() {
        var path = System.getProperty("user.dir") +
                "/src/main/resources/shoestore.ser/";
        var repository = new ShoeStoreRepository(path);
        var shopTaradell = new ShoeStore();
        shopTaradell.setId(1);
        shopTaradell.setName("Corder's Shop");
        repository.save(shopTaradell);
        var updateTeam = repository.get(1);
        assertEquals(1,updateTeam.getId());
    }
}