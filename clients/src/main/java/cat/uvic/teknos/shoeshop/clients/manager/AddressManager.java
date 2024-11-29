package cat.uvic.teknos.shoeshop.clients.manager;

import cat.uvic.teknos.shoeshop.clients.dto.AddressDto;
import cat.uvic.teknos.shoeshop.clients.utils.Mappers;
import cat.uvic.teknos.shoeshop.clients.utils.RestClient;
import cat.uvic.teknos.shoeshop.clients.exceptions.RequestException;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

import static cat.uvic.teknos.shoeshop.clients.utils.Mappers.mapper;

public class AddressManager {

    private final RestClient restClient;
    private final BufferedReader in;

    public AddressManager(RestClient restClient, BufferedReader in) {
        this.restClient = restClient;
        this.in = in;
    }

    public void start() throws IOException {
        String command;
        do {
            showMenu();
            command = in.readLine();

            switch (command) {
                case "1" -> listAddresses();
                case "2" -> getAddressById();
                case "3" -> createAddress();
                case "4" -> updateAddress();
                case "5" -> deleteAddress();
            }
        } while (!command.equalsIgnoreCase("exit"));
    }

    private void showMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🌐          ADDRESS MANAGEMENT MENU          🌐");
        System.out.println("=".repeat(50));
        System.out.printf("%-3s %-30s %n", "1.", "📋 List all addresses");
        System.out.printf("%-3s %-30s %n", "2.", "🔍 Get address by ID");
        System.out.printf("%-3s %-30s %n", "3.", "✏️  Create a new address");
        System.out.printf("%-3s %-30s %n", "4.", "🛠️  Update an address");
        System.out.printf("%-3s %-30s %n", "5.", "🗑️  Delete an address");
        System.out.println("\nType 'exit' to return to the main menu.");
        System.out.println("=".repeat(50));
        System.out.print("Select an option: ");
    }

    private void listAddresses() {
        try {
            // Obtenim la resposta JSON del servidor
            String jsonResponse = restClient.get("/address", String.class);

            // Deserialitzem el JSON com a array d'AddressDto
            AddressDto[] addresses = mapper.readValue(jsonResponse, AddressDto[].class);

            if (addresses.length == 0) {
                System.out.println("⚠️  No addresses found.");
                return;
            }

            System.out.println("\n📋 === Address List ===");
            for (var address : addresses) {
                System.out.printf("%-20s: %-30s%n", "📍 Location", address.getLocation());
                System.out.println("-".repeat(50));
            }
        } catch (RequestException e) {
            System.out.println("❌ Error fetching addresses: " + e.getMessage());
        } catch (JsonProcessingException e) {
            System.out.println("❌ JSON Parsing Error: " + e.getMessage());
        }
    }



    private void getAddressById() throws IOException {
        System.out.print("\nEnter address ID: ");
        String id = in.readLine();
        try {
            var address = restClient.get("/address/" + id, AddressDto.class);

            if (address == null) {
                System.out.println("⚠️  Address with ID " + id + " not found.");
                return;
            }

            System.out.println("\n📋 === Address Details ===");
            System.out.printf("%-20s: %-30s%n", "📍 Location", address.getLocation());
            System.out.println("=".repeat(50));
        } catch (RequestException e) {
            System.out.println("❌ Error fetching address: " + e.getMessage());
        }
    }

    private void createAddress() throws IOException {
        System.out.print("\nEnter Location: ");
        String location = in.readLine();

        var addressDto = new AddressDto();
        addressDto.setLocation(location);

        try {
            restClient.post("/address", Mappers.get().writeValueAsString(addressDto));
            System.out.println("✅ Address created successfully!");
        } catch (RequestException e) {
            System.out.println("❌ Error creating address: " + e.getMessage());
        }
    }

    private void updateAddress() throws IOException {
        System.out.print("\nEnter Address ID to update: ");
        String id = in.readLine();

        try {
            var existingAddress = restClient.get("/address/" + id, AddressDto.class);
            if (existingAddress == null) {
                System.out.println("⚠️  Address with ID " + id + " not found.");
                return;
            }

            System.out.println("\n📋 === Editing Address ===");
            System.out.printf("%-20s: %-30s%n", "📍 Current Location", existingAddress.getLocation());
            System.out.print("Enter new Location (leave blank to keep current): ");
            String location = in.readLine();

            if (!location.isBlank()) existingAddress.setLocation(location);

            restClient.put("/address/" + id, Mappers.get().writeValueAsString(existingAddress));
            System.out.println("✅ Address updated successfully!");
        } catch (RequestException e) {
            System.out.println("❌ Error updating address: " + e.getMessage());
        }
    }

    private void deleteAddress() throws IOException {
        System.out.print("\nEnter Address ID to delete: ");
        String id = in.readLine();

        try {
            restClient.asd("/address/" + id);
            System.out.println("✅ Address deleted successfully!");
        } catch (RequestException e) {
            System.out.println("❌ Error deleting address: " + e.getMessage());
        }
    }
}
