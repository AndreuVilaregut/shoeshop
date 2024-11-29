package cat.uvic.teknos.shoeshop.services.clients;

import cat.uvic.teknos.shoeshop.services.RequestRouterImplementation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static cat.uvic.teknos.shoeshop.services.clients.IOUtils.readLine;

public class ShoeStoreManager {
    private final BufferedReader in;
    private final ObjectMapper objectMapper;
    private final RawHttp http;
    private final RequestRouterImplementation router;
    private static final String BASE_PATH = "/shoestore";

    public ShoeStoreManager(BufferedReader in, RequestRouterImplementation router) {
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
        System.out.println("***Shoe Store Manager***");
        System.out.println("Type:");
        System.out.println("1 to insert a new Shoe Store");
        System.out.println("2 to update Shoe Store");
        System.out.println("3 to delete Shoe Store");
        System.out.println("4 to get a Shoe Store");
        System.out.println("5 to show all Shoe Stores");
        System.out.println("'exit' to exit");
    }

    private void get() throws IOException {
        System.out.println("Please enter the Shoe Store id you wish to search:");
        String storeId = readLine(in);
        try {
            int id = Integer.parseInt(storeId);
            RawHttpResponse<?> response = sendRequest("GET", BASE_PATH + "/" + id, null);
            if (response != null) {
                String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                prettyPrintJson(responseBody);
            }
        } catch (Exception e) {
            System.out.println("Error getting Shoe Store: " + e.getMessage());
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
            System.out.println("Error getting all Shoe Stores: " + e.getMessage());
        }
    }

    private void insert() throws IOException {
        try {
            ObjectNode shoeStoreNode = createShoeStoreNode();
            RawHttpResponse<?> response = sendRequest("POST", BASE_PATH, shoeStoreNode.toString());
            if (response != null) {
                System.out.println("Shoe Store created successfully");
            }
        } catch (Exception e) {
            System.out.println("Error creating Shoe Store: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ObjectNode createShoeStoreNode() throws IOException {
        ObjectNode shoeStoreNode = objectMapper.createObjectNode();

        System.out.println("Please enter the name of the Shoe Store:");
        String name = readLine(in);
        shoeStoreNode.put("name", name);

        System.out.println("Enter the owner of the store:");
        String owner = readLine(in);
        shoeStoreNode.put("owner", owner);

        System.out.println("Enter the location of the store:");
        String location = readLine(in);
        shoeStoreNode.put("location", location);

        return shoeStoreNode;
    }

    private void update() throws IOException {
        System.out.println("Please enter the Shoe Store id you wish to update:");
        String idStr = readLine(in);

        try {
            int id = Integer.parseInt(idStr);

            ObjectNode shoeStoreNode = createUpdateShoeStoreNode();

            RawHttpResponse<?> response = sendRequest("PUT", BASE_PATH + "/" + id, shoeStoreNode.toString());

            if (response != null && response.getStatusCode() == 200) {
                System.out.println("Shoe Store updated successfully");
                String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                prettyPrintJson(responseBody);  // Optionally print the updated Shoe Store
            } else {
                System.out.println("Failed to update the Shoe Store. Server response: " + response.getStatusCode());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please check your input.");
        } catch (Exception e) {
            System.out.println("Error updating Shoe Store: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ObjectNode createUpdateShoeStoreNode() throws IOException {
        ObjectNode shoeStoreNode = objectMapper.createObjectNode();

        System.out.println("Please enter the new name (or press enter to skip):");
        String name = readLine(in);
        if (!name.isEmpty()) {
            shoeStoreNode.put("name", name);
        }

        System.out.println("Enter new owner (or press enter to skip):");
        String owner = readLine(in);
        if (!owner.isEmpty()) {
            shoeStoreNode.put("owner", owner);
        }

        System.out.println("Enter new location (or press enter to skip):");
        String location = readLine(in);
        if (!location.isEmpty()) {
            shoeStoreNode.put("location", location);
        }

        return shoeStoreNode;
    }

    private void delete() throws IOException {
        System.out.println("Please enter the id of the Shoe Store you would like to delete:");
        String idStr = readLine(in);

        try {
            int id = Integer.parseInt(idStr);
            RawHttpResponse<?> response = sendRequest("DELETE", BASE_PATH + "/" + id, null);
            if (response != null) {
                System.out.println("Shoe Store deleted successfully");
            }
        } catch (Exception e) {
            System.out.println("Error deleting Shoe Store: " + e.getMessage());
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
