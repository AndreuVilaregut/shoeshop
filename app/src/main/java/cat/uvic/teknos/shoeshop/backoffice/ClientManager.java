package cat.uvic.teknos.shoeshop.backoffice;

import cat.uvic.teknos.shoeshop.models.Client;
import cat.uvic.teknos.shoeshop.models.ModelFactory;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;

import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.util.Arrays;

import static cat.uvic.teknos.shoeshop.backoffice.IOUtils.*;

public class ClientManager {

    private final PrintStream out;
    private final BufferedReader in;
    private final ClientRepository clientRepository;
    private final ModelFactory modelFactory;

    public ClientManager(BufferedReader in, PrintStream out, ClientRepository clientRepository, ModelFactory modelFactory) {
        this.out = out;
        this.in = in;
        this.clientRepository = clientRepository;
        this.modelFactory = modelFactory;
    }

    public void start() {
        out.println("\n*** Client Management ***\n");

        var command = "";
        do {
            showClientMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> insert();
                case "2" -> update();
                case "3" -> delete();
                case "4" -> getAll();
            }

        } while (!command.equals("exit"));

        out.println("\n*** Exiting Client Management ***\n");
    }

    private void getAll() {
        out.println("\n*** List of Clients ***\n");

        var clients = clientRepository.getAll();

        out.println(AsciiTable.getTable(clients, Arrays.asList(
                new Column().header("Id").with(client -> String.valueOf(client.getId())),
                new Column().header("D.N.I").with(Client::getDni),
                new Column().header("Name").with(Client::getName),
                new Column().header("Phone").with(Client::getPhone)
        )));
    }

    private void delete() {
        out.println("\n*** Delete Client ***\n");

        var client = modelFactory.createClient();

        out.println("Enter the ID of the client to delete:");
        int id = Integer.parseInt(readLine(in));
        client.setId(id);

        clientRepository.delete(client);
        out.println("\nSuccessfully deleted.\n");
    }

    private void update() {
        out.println("\n*** Update Client ***\n");

        try {
            var client = modelFactory.createClient();

            out.println("Enter the ID of the client to update:");
            int id = Integer.parseInt(readLine(in));
            client.setId(id);

            out.println("Enter new Name:");
            client.setName(readLine(in));

            out.println("Enter new Phone:");
            client.setPhone(readLine(in));

            clientRepository.save(client);
            out.println("\nSuccessfully updated.\n");

        } catch (NumberFormatException e) {
            out.println("\nInvalid client ID. Please enter a valid integer ID.\n");
        } catch (Exception e) {
            out.println("\nAn error occurred while updating the client: " + e.getMessage() + "\n");
        }
    }

    private void insert() {
        out.println("\n*** Insert Client ***\n");

        var client = modelFactory.createClient();

        out.println("Enter the DNI:");
        client.setDni((readLine(in)));

        out.println("Enter the Name:");
        client.setName(readLine(in));

        out.println("Enter the Phone:");
        client.setPhone(readLine(in));

        clientRepository.save(client);
        out.println("\nSuccessfully inserted.\n");
    }

    private void showClientMenu() {
        out.println("\n*** Client Management Menu ***\n");
        out.println("1. Insert Client");
        out.println("2. Update Client");
        out.println("3. Delete Client");
        out.println("4. Get All Clients");
        out.println("Type 'exit' to quit.");
        out.println();
    }
}