package cat.uvic.teknos.shoeshop.clients.dto;

import java.io.Serializable;

public class AddressDto implements cat.uvic.teknos.shoeshop.models.Address, Serializable{

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
