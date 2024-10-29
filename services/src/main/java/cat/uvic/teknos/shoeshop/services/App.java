package cat.uvic.teknos.shoeshop.services;

import cat.uvic.teknos.shoeshop.domain.jpa.repositories.JpaClientRepository;
import cat.uvic.teknos.shoeshop.models.ModelFactory;
import cat.uvic.teknos.shoeshop.repositories.RepositoryFactory;
import cat.uvic.teknos.shoeshop.services.controllers.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class App {
    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        var properties = new Properties();
        properties.load(App.class.getResourceAsStream("/app.properties"));

        RepositoryFactory repositoryFactory = (RepositoryFactory) Class.forName(properties.getProperty("repositoryFactory")).getConstructor().newInstance();
        ModelFactory modelFactory = (ModelFactory) Class.forName(properties.getProperty("modelFactory")).getConstructor().newInstance();

        var controllers = new HashMap<String, Controller>();
        controllers.put("client", new ClientController(repositoryFactory, modelFactory));

        var requestRouter = new cat.uvic.teknos.shoeshop.services.RequestRouterImpl(controllers);

        new Server(requestRouter).start();

        /*EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("shoeshopjpa");
        JpaClientRepository clientRepository = new JpaClientRepository(entityManagerFactory);

        Server server = new Server(9998, clientRepository);
        server.start();*/

    }
}
