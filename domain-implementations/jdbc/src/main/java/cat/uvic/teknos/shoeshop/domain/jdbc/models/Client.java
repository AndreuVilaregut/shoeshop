package cat.uvic.teknos.shoeshop.domain.jdbc.models;
import cat.uvic.teknos.shoeshop.models.Address;
import cat.uvic.teknos.shoeshop.models.ShoeStore;

import java.io.Serializable;
import java.util.Set;

public class Client implements cat.uvic.teknos.shoeshop.models.Client, Serializable{

    private int id;
    private String dni;
    private String name;
    private String phone;
    private Address addresses;
    private ShoeStore shoeStores;
    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {

        this.id=id;

    }

    @Override
    public String getDni() {
        return dni;
    }

    @Override
    public void setDni(String dni) {
        this.dni=dni;
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
    public Address getAddress() {
        return addresses;
    }

    @Override
    public void setAddress(Address addresses) {
        this.addresses=addresses;
    }

    @Override
    public ShoeStore getShoeStores() {
        return shoeStores;
    }

    @Override
    public void setShoeStores(ShoeStore shoeStores) {
        this.shoeStores=shoeStores;
    }
}
