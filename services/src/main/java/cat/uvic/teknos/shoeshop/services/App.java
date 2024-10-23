package cat.uvic.teknos.shoeshop.services;

import cat.uvic.teknos.shoeshop.domain.jpa.repositories.JpaClientRepository;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("your-persistence-unit-name");
        JpaClientRepository clientRepository = new JpaClientRepository(entityManagerFactory);

        Server server = new Server(8080, clientRepository);
        server.start();
    }
}
