package cat.uvic.teknos.shoeshop.clients.dto;

import cat.uvic.teknos.shoeshop.models.Inventory;
import cat.uvic.teknos.shoeshop.models.Model;

import java.io.Serializable;

public class ShoeDto implements cat.uvic.teknos.shoeshop.models.Shoe, Serializable {

    private int id;


    private double price;

    private String color;

    private String size;

    private Model models;

    private Inventory inventories;
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {

        this.id=id;

    }


    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {

        this.price=price;

    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void setColor(String color) {
        this.color=color;
    }

    @Override
    public String getSize() {
        return size;
    }

    @Override
    public void setSize(String size) {

        this.size=size;

    }

    @Override
    public Inventory getInventories() {
        return inventories;
    }

    @Override
    public void setInventories(Inventory inventories) {
        this.inventories=inventories;
    }

    @Override
    public Model getModels() {
        return models;
    }

    @Override
    public void setModels(Model models) {
        this.models=models;
    }
}
