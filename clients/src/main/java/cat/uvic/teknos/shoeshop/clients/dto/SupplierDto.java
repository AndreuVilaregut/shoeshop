package cat.uvic.teknos.shoeshop.clients.dto;
import cat.uvic.teknos.shoeshop.models.ShoeStore;

import java.io.Serializable;
import java.util.Set;

public class SupplierDto implements cat.uvic.teknos.shoeshop.models.Supplier, Serializable{

    private int id;
    private String name;

    private String phone;

    private Set<ShoeStore> shoeStores;
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {

        this.id=id;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {

        this.name=name;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {

        this.phone=phone;

    }

    @Override
    public Set<ShoeStore> getShoeStores() {
        return shoeStores;
    }

    @Override
    public void setShoeStores(Set<ShoeStore> shoeStores) {
        this.shoeStores=shoeStores;
    }
}
