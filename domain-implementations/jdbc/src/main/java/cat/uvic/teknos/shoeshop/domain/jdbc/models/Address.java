package cat.uvic.teknos.shoeshop.domain.jdbc.models;

import java.io.Serializable;

public class Address implements cat.uvic.teknos.shoeshop.models.Address, Serializable{

    private int id;

    private String location;
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {

        this.id=id;

    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location=location;
    }


}
