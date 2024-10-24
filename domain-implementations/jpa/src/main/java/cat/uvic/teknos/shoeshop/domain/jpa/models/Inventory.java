package cat.uvic.teknos.shoeshop.domain.jpa.models;

import jakarta.persistence.*;
import java.util.Set;
import cat.uvic.teknos.shoeshop.models.Shoe;
import cat.uvic.teknos.shoeshop.models.ShoeStore;

@Entity
@Table(name = "INVENTORY")
public class Inventory implements cat.uvic.teknos.shoeshop.models.Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INVENTORY_ID")
    private int id;

    @Column(name = "CAPACITY")
    private int capacity;

    @OneToMany(mappedBy = "inventory", targetEntity = cat.uvic.teknos.shoeshop.domain.jpa.models.Shoe.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Shoe> shoes;

    @ManyToMany(mappedBy = "inventories", targetEntity = cat.uvic.teknos.shoeshop.domain.jpa.models.ShoeStore.class, fetch = FetchType.EAGER)
    private Set<ShoeStore> shoeStores;

    public Inventory() {
    }

    public Inventory(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public Set<ShoeStore> getShoeStores() {
        return shoeStores;
    }

    @Override
    public void setShoeStores(Set<ShoeStore> shoeStores) {
        this.shoeStores = shoeStores;
    }

    @Override
    public Set<Shoe> getShoes() {
        return shoes;
    }

    @Override
    public void setShoes(Set<Shoe> shoes) {
        this.shoes = shoes;
    }
}
