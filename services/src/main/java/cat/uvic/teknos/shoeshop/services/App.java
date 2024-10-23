package cat.uvic.teknos.shoeshop.services;

import cat.uvic.teknos.shoeshop.domain.jpa.repositories.JpaClientRepository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("shoeshopjpa");
        JpaClientRepository clientRepository = new JpaClientRepository(entityManagerFactory);

        Server server = new Server(9998, clientRepository);
        server.start();
    }
}
