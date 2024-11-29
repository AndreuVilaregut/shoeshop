package cat.uvic.teknos.shoeshop.clients.dto;

import cat.uvic.teknos.shoeshop.models.Shoe;
import cat.uvic.teknos.shoeshop.models.ShoeStore;

import java.io.Serializable;
import java.util.Set;

public class InventoryDto implements cat.uvic.teknos.shoeshop.models.Inventory, Serializable{

    private int id;
    private  int capacity;

    private Set<ShoeStore> shoeStores;

    private Set<Shoe> shoes;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id=id;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity=capacity;
    }

    @Override
    public Set<ShoeStore> getShoeStores() {
        return shoeStores;
    }

    @Override
    public void setShoeStores(Set<ShoeStore> shoeStores) {
        this.shoeStores=shoeStores;
    }

    @Override
    public Set<Shoe> getShoes() {
        return shoes;
    }

    @Override
    public void setShoes(Set<Shoe> shoes) {
        this.shoes=shoes;
    }


}
