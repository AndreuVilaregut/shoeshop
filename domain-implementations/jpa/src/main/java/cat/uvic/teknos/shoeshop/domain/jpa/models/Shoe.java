package cat.uvic.teknos.shoeshop.domain.jpa.models;

import jakarta.persistence.*;

@Entity
@Table(name = "SHOE")
public class Shoe implements cat.uvic.teknos.shoeshop.models.Shoe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SHOE_ID")
    private int id;

    @Column(name = "PRICE")
    private double price;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "SIZE")
    private String size;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "INVENTORY_ID")
    private Inventory inventory;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "MODEL_ID")
    private Model model;

    public Shoe() {}

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String getSize() {
        return size;
    }

    @Override
    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public Inventory getInventories() {
        return inventory;
    }

    @Override
    public void setInventories(cat.uvic.teknos.shoeshop.models.Inventory inventory) {
        this.inventory = (Inventory) inventory;
    }

    @Override
    public Model getModels() {
        return model;
    }

    @Override
    public void setModels(cat.uvic.teknos.shoeshop.models.Model model) {
        this.model = (Model) model;
    }
}
