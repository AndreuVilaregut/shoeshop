package cat.uvic.teknos.shoeshop.clients.manager;

import cat.uvic.teknos.shoeshop.clients.dto.ClientDto;
import cat.uvic.teknos.shoeshop.clients.exceptions.RequestException;
import cat.uvic.teknos.shoeshop.clients.utils.Mappers;
import cat.uvic.teknos.shoeshop.clients.utils.RestClient;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class ClientManager {

    private final RestClient restClient;
    private final BufferedReader in;
    private final PrintStream out;

    public ClientManager(RestClient restClient, BufferedReader in) {
        this.restClient = restClient;
        this.in = in;
        this.out = new PrintStream(System.out);
    }

    public void start() throws IOException, RequestException {
        String command;
        do {
            showClientMenu();
            command = readLine();

            switch (command) {
                case "1" -> listClients();
                case "2" -> getClientById();
                case "3" -> createClient();
                case "4" -> updateClient();
                case "5" -> deleteClient();
            }
        } while (!command.equalsIgnoreCase("exit"));
    }

    private void showClientMenu() {
        out.println("\n" + "=".repeat(50));
        out.println("🌐          CLIENT MANAGEMENT MENU          🌐");
        out.println("=".repeat(50));
        out.printf("%-3s %-30s %n", "1.", "📋 List all clients");
        out.printf("%-3s %-30s %n", "2.", "🔍 Get client by ID");
        out.printf("%-3s %-30s %n", "3.", "✏️  Create a new client");
        out.printf("%-3s %-30s %n", "4.", "🛠️  Update a client");
        out.printf("%-3s %-30s %n", "5.", "🗑️  Delete a client");
        out.println("\nType 'exit' to return to the main menu.");
        out.println("=".repeat(50));
        out.print("Select an option: ");
    }

    private void listClients() throws RequestException {
        var clients = restClient.getAll("/client", ClientDto[].class);

        if (clients.length == 0) {
            out.println("⚠️  No clients found.");
            return;
        }

        out.println("\n📋 === Client List ===");
        for (var client : clients) {
            out.printf("%-20s: %-30s%n", "📍 Name", client.getName());
            out.printf("%-20s: %-30s%n", "📍 DNI", client.getDni());
            out.printf("%-20s: %-30s%n", "📍 Phone", client.getPhone());
            out.printf("%-20s: %-30s%n", "📍 Address", client.getAddresses() != null ? client.getAddresses().getLocation() : "N/A");
            out.println("-".repeat(50));
        }
    }

    private void getClientById() throws IOException {
        out.print("\nEnter client ID: ");
        String id = readLine();

        try {
            var client = restClient.get("/client/" + id, ClientDto.class);

            if (client == null) {
                out.println("⚠️  Client with ID " + id + " not found.");
                return;
            }

            out.println("\n📋 === Client Details ===");
            out.printf("%-20s: %-30s%n", "📍 ID", client.getId());
            out.printf("%-20s: %-30s%n", "📍 Name", client.getName());
            out.printf("%-20s: %-30s%n", "📍 DNI", client.getDni());
            out.printf("%-20s: %-30s%n", "📍 Phone", client.getPhone());
            out.printf("%-20s: %-30s%n", "📍 Address", client.getAddresses() != null ? client.getAddresses().getLocation() : "N/A");
            out.println("=".repeat(50));

        } catch (RequestException e) {
            out.println("❌ Error fetching client: " + e.getMessage());
        }
    }

    private void createClient() throws IOException {
        // Demanar dades per al client
        out.print("\nEnter Name: ");
        String name = readLine();

        out.print("Enter DNI: ");
        String dni = readLine();

        out.print("Enter Phone: ");
        String phone = readLine();

        // Crear Address per l'adreça
        out.print("Enter Address Location: ");
        String addressLocation = readLine(); // Capturar la ubicació de l'adreça

        var address = new cat.uvic.teknos.shoeshop.clients.dto.AddressDto(); // Creem la classe concreta Address
        address.setLocation(addressLocation); // Assignar la ubicació a l'adreça

        // Crear ShoeStore per la botiga
        out.print("Enter Shoe Store Name: ");
        var shoeStore = new cat.uvic.teknos.shoeshop.clients.dto.ShoeStoreDto(); // Creem la classe concreta ShoeStore
        shoeStore.setName(readLine());

        out.print("Enter Shoe Store Owner: ");
        shoeStore.setOwner(readLine());

        out.print("Enter Shoe Store Location: ");
        shoeStore.setLocation(readLine());

        // Crear ClientDto i associar l'adreça i la botiga de sabates
        var clientDto = new ClientDto();
        clientDto.setName(name);
        clientDto.setDni(dni);
        clientDto.setPhone(phone);
        clientDto.setAddresses(address); // Afegir adreça
        clientDto.setShoeStores(shoeStore); // Afegir shoe store

        try {
            // Comprovar el valor de l'adreça per garantir que es captura bé
            System.out.println("Address Location: " + clientDto.getAddresses());  // Afegir per verificar

            // Convertir l'objecte clientDto a JSON per enviar-lo
            String clientJson = Mappers.get().writeValueAsString(clientDto); // Serialització correcte a JSON

            // Mostrar el JSON per verificar la correcta serialització
            System.out.println("Client JSON: " + clientJson);  // Afegir per veure el JSON generat

            // Enviar la petició al servidor REST
            restClient.post("/client", clientJson);

            out.println("✅ Client created successfully!");
        } catch (RequestException | JsonProcessingException e) {
            out.println("❌ Error creating client: " + e.getMessage());
        }
    }

    private void updateClient() throws IOException {
        out.print("\nEnter Client ID to update: ");
        String id = readLine();

        try {
            var existingClient = restClient.get("/client/" + id, ClientDto.class);
            if (existingClient == null) {
                out.println("⚠️  Client with ID " + id + " not found.");
                return;
            }

            out.println("\n📋 === Editing Client ===");
            out.printf("%-20s: %-30s%n", "📍 Current Name", existingClient.getName());
            out.print("Enter new Name (leave blank to keep current): ");
            String name = readLine();
            if (!name.isBlank()) existingClient.setName(name);

            out.printf("%-20s: %-30s%n", "📍 Current DNI", existingClient.getDni());
            out.print("Enter new DNI (leave blank to keep current): ");
            String dni = readLine();
            if (!dni.isBlank()) existingClient.setDni(dni);

            out.printf("%-20s: %-30s%n", "📍 Current Phone", existingClient.getPhone());
            out.print("Enter new Phone (leave blank to keep current): ");
            String phone = readLine();
            if (!phone.isBlank()) existingClient.setPhone(phone);

            out.printf("%-20s: %-30s%n", "📍 Current Address", existingClient.getAddresses() != null ? existingClient.getAddresses().getLocation() : "N/A");
            out.print("Enter new Address Location (leave blank to keep current): ");
            String address = readLine();
            if (!address.isBlank()) {
                var newAddress = new cat.uvic.teknos.shoeshop.clients.dto.AddressDto();
                newAddress.setLocation(address);
                existingClient.setAddresses(newAddress);
            }

            restClient.put("/client/" + id, Mappers.get().writeValueAsString(existingClient));
            out.println("✅ Client updated successfully!");

        } catch (RequestException e) {
            out.println("❌ Error updating client: " + e.getMessage());
        }
    }

    private void deleteClient() throws IOException {
        out.print("\nEnter Client ID to delete: ");
        String id = readLine();

        try {
            restClient.asd("/client/" + id);
            out.println("✅ Client deleted successfully!");
        } catch (RequestException e) {
            out.println("❌ Error deleting client: " + e.getMessage());
        }
    }

    private String readLine() {
        try {
            return in.readLine();
        } catch (IOException e) {
            throw new RuntimeException("❌ Error reading from console: " + e.getMessage(), e);
        }
    }
}
