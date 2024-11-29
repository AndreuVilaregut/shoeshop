package cat.uvic.teknos.shoeshop.services;

import cat.uvic.teknos.shoeshop.repositories.AddressRepository;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;
import cat.uvic.teknos.shoeshop.repositories.RepositoryFactory;
import cat.uvic.teknos.shoeshop.repositories.ShoeRepository;
import cat.uvic.teknos.shoeshop.services.clients.ClientManager;
import cat.uvic.teknos.shoeshop.services.clients.ShoeManager;
import cat.uvic.teknos.shoeshop.services.clients.ShoeStoreManager;
import cat.uvic.teknos.shoeshop.services.controllers.*;
import cat.uvic.teknos.shoeshop.services.clients.AddressManager;
import cat.uvic.teknos.shoeshop.services.controllers.ShoeController;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

import static cat.uvic.teknos.shoeshop.services.clients.IOUtils.readLine;

public class    App {
    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        var properties = new Properties();
        properties.load(App.class.getResourceAsStream("/app.properties"));

        RepositoryFactory repositoryFactory = (RepositoryFactory) Class.forName(properties.getProperty("repositoryFactory")).getConstructor().newInstance();

        var controllers = new HashMap<String, Controller>();
        controllers.put("address", new AddressController((AddressRepository) repositoryFactory.getAddressRepository()));
        controllers.put("shoe", new ShoeController((ShoeRepository) repositoryFactory.getShoeRepository()));
        controllers.put("client", new ClientController((ClientRepository) repositoryFactory.getClientRepository()));


        var requestRouter = new RequestRouterImplementation(controllers);

        Server server = new Server(requestRouter);

        server.start();

        backOffice(requestRouter, server);
    }

    private static void backOffice(RequestRouterImplementation router, Server server) throws IOException {
        showWelcomeMessage();

        var command = "";
        do {
            showMainMenu();
            command = readLine(in);

            switch (command) {
                case "1":
                    manageAddress(router);
                    break;
                case "2":
                    manageShoe(router);
                    break;
                case "3":
                    manageClient(router);
                    break;
                case "4":
                    manageShoeStore(router);
                    break;
                case "5":
                    manageShoe(router);
                    break;
                default:
                    if (!command.equalsIgnoreCase("exit")) {
                        System.out.println("Comanda invàlida");
                    } else {
                        server.stop();
                    }
                    break;
            }

        } while (!command.equalsIgnoreCase("exit"));

        System.out.println("Adeu!");
    }

    private static void manageAddress(RequestRouterImplementation router) throws IOException {
        var addressManager = new AddressManager(new BufferedReader(new InputStreamReader(System.in)), router);
        addressManager.start();
    }
    private static void manageShoe(RequestRouterImplementation router) throws IOException {
        var shoeManager = new ShoeManager(new BufferedReader(new InputStreamReader(System.in)), router);
        shoeManager.start();
    }
    private static void manageClient(RequestRouterImplementation router) throws IOException {
        var clientManager = new ClientManager(new BufferedReader(new InputStreamReader(System.in)), router);
        clientManager.start();
    }
    private static void manageShoeStore(RequestRouterImplementation router) throws IOException {
        var shoeStoreManager = new ShoeStoreManager(new BufferedReader(new InputStreamReader(System.in)), router);
        shoeStoreManager.start();
    }

    private static void showWelcomeMessage() {
        System.out.println("Benvingut al sistema de gestió de botigues de sabates");
        System.out.println("Selecciona una opció del menú o escriu 'exit' per sortir de l'aplicació");
    }

    private static void showMainMenu() {
        System.out.println("Si us plau, introduïu el número corresponent a l'acció:");
        System.out.println("1. Gestionar Adreces de Botiga");
            System.out.println("2. Gestionar Proveïdors (opció per a futura implementació)");
    }
}
