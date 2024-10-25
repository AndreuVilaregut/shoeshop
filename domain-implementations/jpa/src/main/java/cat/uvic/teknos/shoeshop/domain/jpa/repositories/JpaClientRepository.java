package cat.uvic.teknos.shoeshop.domain.jpa.repositories;

import cat.uvic.teknos.shoeshop.models.Client;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.HashSet;
import java.util.Set;

public class JpaClientRepository implements ClientRepository {
    private final EntityManagerFactory entityManagerFactory;

    public JpaClientRepository(EntityManagerFactory entityManagerFactory){
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void save(Client model) {
        var entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(model);
        entityManager.getTransaction().commit();
        entityManager.close();
    }
    @Override
    public void delete(Client model) {
        var entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.remove(entityManager.contains(model) ? model : entityManager.merge(model));
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    @Override
    public Client get(Integer id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Client client = entityManager.find(cat.uvic.teknos.shoeshop.domain.jpa.models.Client.class, id);
            return client;
        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            entityManager.close();
        }

    }

    @Override
    public Set<Client> getAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        Set<Client> clients = new HashSet<>(entityManager.createQuery("SELECT c FROM Client c", Client.class).getResultList());
        entityManager.getTransaction().commit();
        entityManager.close();
        return clients;
    }
}

