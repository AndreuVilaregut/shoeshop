package cat.uvic.teknos.shoeshop.domain.jpa.models;

import jakarta.persistence.*;

@Entity
@Table(name = "CLIENT")
public class Client implements cat.uvic.teknos.shoeshop.models.Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CLIENT_ID")
    private int id;

    @Column(name = "DNI")
    private String dni;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PHONE")
    private String phone;

    @OneToOne(targetEntity = cat.uvic.teknos.shoeshop.domain.jpa.models.Address.class,fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID")
    private Address address;

    @ManyToOne(targetEntity = cat.uvic.teknos.shoeshop.domain.jpa.models.ShoeStore.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "SHOE_STORE_ID")
    private ShoeStore shoeStore;

    public Client(String dni, String name, String phone) {
        this.dni = dni;
        this.name = name;
        this.phone = phone;
    }

    public Client() {}

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getDni() {
        return dni;
    }

    @Override
    public void setDni(String dni) {
        this.dni = dni;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public cat.uvic.teknos.shoeshop.models.Address getAddresses() {
        return address;
    }

    @Override
    public void setAddresses(cat.uvic.teknos.shoeshop.models.Address address) {
        this.address = (Address) address;
    }

    @Override
    public cat.uvic.teknos.shoeshop.models.ShoeStore getShoeStores() {
        return shoeStore;
    }

    @Override
    public void setShoeStores(cat.uvic.teknos.shoeshop.models.ShoeStore shoeStore) {
        this.shoeStore = (ShoeStore) shoeStore;
    }
}