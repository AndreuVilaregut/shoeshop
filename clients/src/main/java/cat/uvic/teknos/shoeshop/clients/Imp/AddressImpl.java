package cat.uvic.teknos.shoeshop.clients.Imp;

import cat.uvic.teknos.shoeshop.models.Address;

public class AddressImpl implements Address {
    private int id;
    private String location;

    // Getters i setters
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }
}
