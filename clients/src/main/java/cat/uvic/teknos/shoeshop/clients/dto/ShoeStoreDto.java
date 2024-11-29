package cat.uvic.teknos.shoeshop.clients.dto;

import cat.uvic.teknos.shoeshop.models.Client;
import cat.uvic.teknos.shoeshop.models.Inventory;
import cat.uvic.teknos.shoeshop.models.Supplier;

import java.io.Serializable;
import java.util.Set;

public class ShoeStoreDto implements cat.uvic.teknos.shoeshop.models.ShoeStore, Serializable {

    private int id;
    private String name;

    private String owner;

    private String location;

    private Set<Inventory> inventories;

    private Set<Supplier> suppliers;

    private Set<Client> clients;
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
    public String getOwner() {
        return owner;
    }

    @Override
    public void setOwner(String owner) {
        this.owner=owner;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location=location;
    }

    @Override
    public Set<Supplier> getSuppliers() {
        return suppliers;
    }

    @Override
    public void setSuppliers(Set<Supplier> suppliers) {
        this.suppliers=suppliers;
    }

    @Override
    public Set<Client> getClients() {
        return clients;
    }

    @Override
    public void setClients(Set<Client> clients) {
        this.clients=clients;
    }

    @Override
    public Set<Inventory> getInventories() {
        return inventories;
    }

    @Override
    public void setInventories(Set<Inventory> inventories) {
        this.inventories=inventories;
    }
}