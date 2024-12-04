/*
package cat.uvic.teknos.shoeshop.clients.manager;

import cat.uvic.teknos.shoeshop.clients.dto.ClientDto;
import cat.uvic.teknos.shoeshop.models.Address;
import cat.uvic.teknos.shoeshop.models.ShoeStore;
import cat.uvic.teknos.shoeshop.clients.utils.Mappers;
import cat.uvic.teknos.shoeshop.clients.utils.RestClient;
import cat.uvic.teknos.shoeshop.clients.exceptions.RequestException;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

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
            printClientDetails(client);
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
            printClientDetails(client);

        } catch (RequestException e) {
            out.println("❌ Error fetching client: " + e.getMessage());
        }
    }

    private void createClient() throws IOException {
        ClientDto client = readClientDetails();

        try {
            String clientJson = Mappers.get().writeValueAsString(client);
            restClient.post("/client", clientJson);
            out.println("✅ Client created successfully!");
        } catch (JsonProcessingException | RequestException e) {
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
            ClientDto updates = readClientDetails(existingClient);

            String updatesJson = Mappers.get().writeValueAsString(updates);
            restClient.put("/client/" + id, updatesJson);
            out.println("✅ Client updated successfully!");

        } catch (RequestException | JsonProcessingException e) {
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

    private ClientDto readClientDetails() throws IOException {
        return readClientDetails(null);
    }

    private ClientDto readClientDetails(ClientDto existingClient) throws IOException {
        var client = existingClient != null ? existingClient : new ClientDto();

        out.printf("Enter Name [%s]: ", existingClient != null ? existingClient.getName() : "N/A");
        String name = readLine();
        if (!name.isBlank()) client.setName(name);

        out.printf("Enter DNI [%s]: ", existingClient != null ? existingClient.getDni() : "N/A");
        String dni = readLine();
        if (!dni.isBlank()) client.setDni(dni);

        out.printf("Enter Phone [%s]: ", existingClient != null ? existingClient.getPhone() : "N/A");
        String phone = readLine();
        if (!phone.isBlank()) client.setPhone(phone);

        out.printf("Enter Address [%s]: ", existingClient != null && existingClient.getAddresses() != null
                ? existingClient.getAddresses().getLocation() : "N/A");
        String address = readLine();
        if (!address.isBlank()) {
            var addressDto = new cat.uvic.teknos.shoeshop.clients.dto.AddressDto();
            addressDto.setLocation(address);
            client.setAddresses(addressDto);
        }

        out.printf("Enter ShoeStore Name [%s]: ", existingClient != null && existingClient.getShoeStores() != null
                ? existingClient.getShoeStores().getName() : "N/A");
        String storeName = readLine();
        if (!storeName.isBlank()) {
            var shoeStore = new cat.uvic.teknos.shoeshop.clients.dto.ShoeStoreDto();
            shoeStore.setName(storeName);
            client.setShoeStores(shoeStore);
        }

        return client;
    }

    private void printClientDetails(ClientDto client) {
        out.printf("📍 ID: %d%n", client.getId());
        out.printf("📍 Name: %s%n", client.getName());
        out.printf("📍 DNI: %s%n", client.getDni());
        out.printf("📍 Phone: %s%n", client.getPhone());
        out.printf("📍 Address: %s%n",
                client.getAddresses() != null ? client.getAddresses().getLocation() : "N/A");
        out.printf("📍 ShoeStore: %s%n",
                client.getShoeStores() != null ? client.getShoeStores().getName() : "N/A");
        out.println("=".repeat(50));
    }
}
*/
