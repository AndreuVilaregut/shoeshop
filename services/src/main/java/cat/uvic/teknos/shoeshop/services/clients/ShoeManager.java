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

public class ShoeManager {
    private final BufferedReader in;
    private final ObjectMapper objectMapper;
    private final RawHttp http;
    private final RequestRouterImplementation router;
    private static final String BASE_PATH = "/shoes";

    public ShoeManager(BufferedReader in, RequestRouterImplementation router) {
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
        String command = "";
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
        System.out.println("***Shoe Manager***");
        System.out.println("Type:");
        System.out.println("1 to insert a new Shoe");
        System.out.println("2 to update a Shoe");
        System.out.println("3 to delete a Shoe");
        System.out.println("4 to get a Shoe by ID");
        System.out.println("5 to show all Shoes");
        System.out.println("'exit' to exit");
    }

    private void get() throws IOException {
        System.out.println("Please enter the Shoe id you wish to search:");
        String shoeId = readLine(in);
        try {
            int id = Integer.parseInt(shoeId);
            RawHttpResponse<?> response = sendRequest("GET", BASE_PATH + "/" + id, null);
            if (response != null) {
                String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                prettyPrintJson(responseBody);
            }
        } catch (Exception e) {
            System.out.println("Error getting Shoe: " + e.getMessage());
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
            System.out.println("Error getting all Shoes: " + e.getMessage());
        }
    }

    private void insert() throws IOException {
        try {
            ObjectNode shoeNode = createShoeNode();
            RawHttpResponse<?> response = sendRequest("POST", BASE_PATH, shoeNode.toString());
            if (response != null) {
                System.out.println("Shoe created successfully");
            }
        } catch (Exception e) {
            System.out.println("Error creating Shoe: " + e.getMessage());
        }
    }

    private ObjectNode createShoeNode() throws IOException {
        ObjectNode shoeNode = objectMapper.createObjectNode();

        System.out.println("Enter the Shoe model:");
        String model = readLine(in);
        shoeNode.put("model", model);

        System.out.println("Enter the Shoe size:");
        double size = Double.parseDouble(readLine(in));
        shoeNode.put("size", size);

        System.out.println("Enter the Shoe color:");
        String color = readLine(in);
        shoeNode.put("color", color);

        System.out.println("Enter the Shoe price:");
        double price = Double.parseDouble(readLine(in));
        shoeNode.put("price", price);

        return shoeNode;
    }

    private void update() throws IOException {
        System.out.println("Enter the Shoe id you wish to update:");
        String idStr = readLine(in);

        try {
            int id = Integer.parseInt(idStr);
            ObjectNode shoeNode = createShoeNode();

            RawHttpResponse<?> response = sendRequest("PUT", BASE_PATH + "/" + id, shoeNode.toString());
            if (response != null && response.getStatusCode() == 200) {
                System.out.println("Shoe updated successfully");
            } else {
                System.out.println("Failed to update Shoe");
            }
        } catch (Exception e) {
            System.out.println("Error updating Shoe: " + e.getMessage());
        }
    }

    private void delete() throws IOException {
        System.out.println("Enter the Shoe id you wish to delete:");
        String idStr = readLine(in);

        try {
            int id = Integer.parseInt(idStr);
            RawHttpResponse<?> response = sendRequest("DELETE", BASE_PATH + "/" + id, null);
            if (response != null) {
                System.out.println("Shoe deleted successfully");
            }
        } catch (Exception e) {
            System.out.println("Error deleting Shoe: " + e.getMessage());
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
