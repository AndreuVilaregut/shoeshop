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

public class ClientManager {
    private final BufferedReader in;
    private final ObjectMapper objectMapper;
    private final RawHttp http;
    private final RequestRouterImplementation router;
    private static final String BASE_PATH = "/client";

    public ClientManager(BufferedReader in, RequestRouterImplementation router) {
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
        System.out.println("***Client Manager***");
        System.out.println("Type:");
        System.out.println("1 to insert a new Client");
        System.out.println("2 to update Client");
        System.out.println("3 to delete Client");
        System.out.println("4 to get a Client");
        System.out.println("5 to show all Clients");
        System.out.println("'exit' to exit");
    }

    private void get() throws IOException {
        System.out.println("Please enter the client id you wish to search");
        String clientId = readLine(in);
        try {
            int id = Integer.parseInt(clientId);
            RawHttpResponse<?> response = sendRequest("GET", BASE_PATH + "/" + id, null);
            if (response != null) {
                String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                prettyPrintJson(responseBody);
            }
        } catch (Exception e) {
            System.out.println("Error getting client: " + e.getMessage());
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
            System.out.println("Error getting all clients: " + e.getMessage());
        }
    }

    private void insert() throws IOException {
        try {
            ObjectNode clientNode = createClientNode();
            RawHttpResponse<?> response = sendRequest("POST", BASE_PATH, clientNode.toString());
            if (response != null) {
                System.out.println("Client created successfully");
            }
        } catch (Exception e) {
            System.out.println("Error creating client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ObjectNode createClientNode() throws IOException {
        ObjectNode clientNode = objectMapper.createObjectNode();

        System.out.println("Please enter the DNI: ");
        String dni = readLine(in);
        clientNode.put("dni", dni);

        System.out.println("Enter the name:");
        String name = readLine(in);
        clientNode.put("name", name);

        System.out.println("Enter the phone number:");
        String phone = readLine(in);
        clientNode.put("phone", phone);

        System.out.println("Enter the address ID:");
        String addressId = readLine(in);
        clientNode.put("addressId", addressId);

        return clientNode;
    }

    private void update() throws IOException {
        System.out.println("Please enter the client id you wish to update");
        String idStr = readLine(in);

        try {
            int id = Integer.parseInt(idStr);

            ObjectNode clientNode = createUpdateClientNode();

            RawHttpResponse<?> response = sendRequest("PUT", BASE_PATH + "/" + id, clientNode.toString());

            if (response != null && response.getStatusCode() == 200) {
                System.out.println("Client updated successfully");
                String responseBody = response.getBody().get().decodeBodyToString(StandardCharsets.UTF_8);
                prettyPrintJson(responseBody);  // Optionally print the updated client
            } else {
                System.out.println("Failed to update the client. Server response: " + response.getStatusCode());
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please check your input.");
        } catch (Exception e) {
            System.out.println("Error updating client: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private ObjectNode createUpdateClientNode() throws IOException {
        ObjectNode clientNode = objectMapper.createObjectNode();

        System.out.println("Please enter the new DNI (or press enter to skip):");
        String dni = readLine(in);
        if (!dni.isEmpty()) {
            clientNode.put("dni", dni);
        }

        System.out.println("Enter new name (or press enter to skip):");
        String name = readLine(in);
        if (!name.isEmpty()) {
            clientNode.put("name", name);
        }

        System.out.println("Enter new phone number (or press enter to skip):");
        String phone = readLine(in);
        if (!phone.isEmpty()) {
            clientNode.put("phone", phone);
        }

        System.out.println("Enter new address ID (or press enter to skip):");
        String addressId = readLine(in);
        if (!addressId.isEmpty()) {
            clientNode.put("addressId", addressId);
        }

        return clientNode;
    }

    private void delete() throws IOException {
        System.out.println("Please enter the id of the client you would like to delete");
        String idStr = readLine(in);

        try {
            int id = Integer.parseInt(idStr);
            RawHttpResponse<?> response = sendRequest("DELETE", BASE_PATH + "/" + id, null);
            if (response != null) {
                System.out.println("Client deleted successfully");
            }
        } catch (Exception e) {
            System.out.println("Error deleting client: " + e.getMessage());
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
