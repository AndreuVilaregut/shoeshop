package cat.uvic.teknos.shoeshop.services.clients;

import cat.uvic.teknos.shoeshop.services.RequestRouterImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import cat.uvic.teknos.shoeshop.services.clients.IOUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static cat.uvic.teknos.shoeshop.services.clients.IOUtils.readLine;

public class AddressManager {
    private final BufferedReader in;
    private final ObjectMapper objectMapper;
    private final RawHttp http;
    private final RequestRouterImplementation router;
    private static final String BASE_PATH = "/address";

    public AddressManager(BufferedReader in, RequestRouterImplementation router) {
        this.in = in;
        this.router = router;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.http = new RawHttp();
    }

    private RawHttpResponse<?> sendRequest(String method, String path, String body) throws IOException {
        String requestString = method + " " + path + " HTTP/1.1\r\n" +
                "Host: localhost\r\n" +
                "Content-Type: application/json\r\n";

        if (body != null && !body.isEmpty()) {
            requestString += "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n" + body;
        } else {
            requestString += "\r\n";
        }

        RawHttpRequest request = http.parseRequest(requestString);
        return router.route(request);
    }

    public void start() throws IOException {
        var command = "";
        while (true) {
            showMenu();
            command = readLine(in);

            switch (command) {
                case "1":
                    insert();
                    break;
                case "2":
                    update();
                    break;
                case "3":
                    delete();
                    break;
                case "4":
                    get();
                    break;
                case "5":
                    getAll();
                    break;
                case "exit":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void showMenu() {
        System.out.println("***Address Manager***");
        System.out.println("Type:");
        System.out.println("1 to insert a new Address");
        System.out.println("2 to update Address");
        System.out.println("3 to delete Address");
        System.out.println("4 to get an Address");
        System.out.println("5 to show all Addresses");
        System.out.println("'exit' to exit");
    }

    private void get() throws IOException {
        System.out.println("Please enter the address id you wish to search");
        String addressId = readLine(in);
        try {
            int id = Integer.parseInt(addressId);
            RawHttpResponse<?> response = sendRequest("GET", BASE_PATH + "/" + id, null);
            if (response != null) {
                String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                prettyPrintJson(responseBody);
            }
        } catch (Exception e) {
            System.out.println("Error getting address: " + e.getMessage());
        }
    }

    private void getAll() throws IOException {
        try {
            RawHttpResponse<?> response = sendRequest("GET", BASE_PATH, null);
            if (response != null) {
                String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                prettyPrintJson(responseBody);
            }
        } catch (Exception e) {
            System.out.println("Error getting all addresses: " + e.getMessage());
        }
    }

    private void insert() throws IOException {
        try {
            ObjectNode addressNode = createAddressNode();
            RawHttpResponse<?> response = sendRequest("POST", BASE_PATH, addressNode.toString());
            if (response != null) {
                System.out.println("Address created successfully");
            }
        } catch (Exception e) {
            System.out.println("Error creating address: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ObjectNode createAddressNode() throws IOException {
        ObjectNode addressNode = objectMapper.createObjectNode();

        System.out.println("Please enter the street name: ");
        String streetName = readLine(in);
        addressNode.put("streetName", streetName);

        System.out.println("Enter the city:");
        String city = readLine(in);
        addressNode.put("city", city);

        System.out.println("Enter the zip code:");
        String zipCode = readLine(in);
        addressNode.put("zipCode", zipCode);

        System.out.println("Enter the country:");
        String country = readLine(in);
        addressNode.put("country", country);

        return addressNode;
    }

    private void update() throws IOException {
        System.out.println("Please enter the address's id you wish to update");
        String idStr = readLine(in);

        try {
            int id = Integer.parseInt(idStr);  // Parse the input ID

            // Create the updated address node based on user input
            ObjectNode addressNode = createUpdateAddressNode();

            // Send the PUT request to update the address
            RawHttpResponse<?> response = sendRequest("PUT", BASE_PATH + "/" + id, addressNode.toString());

            if (response != null && response.getStatusCode() == 200) {
                // If the response is successful (200 OK)
                System.out.println("Address updated successfully");
                String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                prettyPrintJson(responseBody);  // Optionally print the updated address
            } else {
                System.out.println("Failed to update the address. Server response: " + response.getStatusCode());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please check your input.");
        } catch (Exception e) {
            System.out.println("Error updating address: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ObjectNode createUpdateAddressNode() throws IOException {
        ObjectNode addressNode = objectMapper.createObjectNode();

        System.out.println("Please enter the new street name (or press enter to skip):");
        String streetName = readLine(in);
        if (!streetName.isEmpty()) {
            addressNode.put("streetName", streetName);
        }

        System.out.println("Enter new city (or press enter to skip):");
        String city = readLine(in);
        if (!city.isEmpty()) {
            addressNode.put("city", city);
        }

        System.out.println("Enter new zip code (or press enter to skip):");
        String zipCode = readLine(in);
        if (!zipCode.isEmpty()) {
            addressNode.put("zipCode", zipCode);
        }

        System.out.println("Enter new country (or press enter to skip):");
        String country = readLine(in);
        if (!country.isEmpty()) {
            addressNode.put("country", country);
        }

        return addressNode;
    }

    private void delete() throws IOException {
        System.out.println("Please enter the id of the address you would like to delete");
        String idStr = readLine(in);

        try {
            int id = Integer.parseInt(idStr);
            RawHttpResponse<?> response = sendRequest("DELETE", BASE_PATH + "/" + id, null);
            if (response != null) {
                System.out.println("Address deleted successfully");
            }
        } catch (Exception e) {
            System.out.println("Error deleting address: " + e.getMessage());
        }
    }

    private void prettyPrintJson(String json) {
        try {
            Object jsonObject = objectMapper.readValue(json, Object.class);
            System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject));
        } catch (Exception e) {
            System.out.println("Error formatting JSON: " + e.getMessage());
            System.out.println(json);
        }
    }
}

