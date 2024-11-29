package cat.uvic.teknos.shoeshop.models;


import java.util.Set;

public interface Client {

    int getId();
    void setId(int id);

    String getDni();
    void setDni(String dni);

    String getName();
    void setName(String name);

    String getPhone();
    void setPhone(String phone);

    Address getAddresses();
    void setAddresses(Address addresses);

    ShoeStore getShoeStores();
    void setShoeStores(ShoeStore shoeStores);

}
