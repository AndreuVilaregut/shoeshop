package cat.uvic.teknos.shoeshop.clients;

import cat.uvic.teknos.shoeshop.clients.manager.AddressManager;
import cat.uvic.teknos.shoeshop.clients.manager.ClientManager;
import cat.uvic.teknos.shoeshop.clients.manager.ShoeManager;
import cat.uvic.teknos.shoeshop.clients.manager.ShoeStoreManager;
import cat.uvic.teknos.shoeshop.clients.utils.RestClient;
import cat.uvic.teknos.shoeshop.clients.exceptions.RequestException;
import cat.uvic.teknos.shoeshop.clients.utils.RestClientImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Properties;

public class App {

    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final PrintStream out = System.out;
    private static RestClientImpl restClient;

    static {
        try {
            Properties properties = new Properties();
            var propertiesStream = App.class.getResourceAsStream("/app.properties");
            if (propertiesStream == null) {
                throw new IOException("Error: No s'ha trobat el fitxer de configuració 'app.properties'.");
            }
            properties.load(propertiesStream);

            // Carregar configuració del servidor
            String host = properties.getProperty("server.host", "localhost");
            int port = Integer.parseInt(properties.getProperty("server.port", "9998"));

            // Crear la instància del client REST
            restClient = new RestClientImpl(host, port) {};  // Classe anònima per a la instanciació

        } catch (IOException e) {
            throw new RuntimeException("Error al carregar la configuració: " + e.getMessage(), e);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error en el valor del port a la configuració: " + e.getMessage(), e);
        }
    }

    public static void main(String[] args) throws IOException, RequestException {
        showWelcomeMessage();

        String command;
        do {
            showMainMenu();
            command = IOUtils.readLine(in);  // Llegir la línia d'entrada des de la consola

            switch (command) {
                case "1" -> new ClientManager(restClient, in).start();
                case "2" -> new ShoeManager(restClient, in).start();
                case "3" -> new AddressManager(restClient, in).start();
                default -> {
                    if (!command.equalsIgnoreCase("exit")) {
                        out.println("Opció invàlida. Torna-ho a provar.");
                    }
                }
            }
        } while (!command.equalsIgnoreCase("exit"));

        out.println("\n*** Programa tancat correctament ***\n");
    }

    private static void showWelcomeMessage() {
        out.println("************************************************************");
        out.println("*                                                          *");
        out.println("*               WELCOME TO SHOE SHOP MANAGER              *");
        out.println("*                                                          *");
        out.println("*            Your ultimate Back Office Solution!          *");
        out.println("*                                                          *");
        out.println("************************************************************");
        out.println("             Manage Clients, Shoes, and More!              ");
        out.println("------------------------------------------------------------");
    }

    private static void showMainMenu() {
        out.println("\n------------------------------------------------------------");
        out.println("                    *** MAIN MENU ***");
        out.println("------------------------------------------------------------");
        out.println("  1. Manage Clients");
        out.println("  2. Manage Shoes");
        out.println("  3. Manage Addresses");
        out.println("  4. Manage Shoe Stores (Coming soon...)");
        out.println("------------------------------------------------------------");
        out.println("  Type 'exit' to quit the application.");
        out.println("------------------------------------------------------------");
        out.print("  Enter your choice: ");
    }
}

