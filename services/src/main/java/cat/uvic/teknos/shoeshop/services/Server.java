package cat.uvic.teknos.shoeshop.services;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    private RawHttp http;
    private ClientRepository clientRepository;

    public Server(int port, ClientRepository clientRepository) {
        this.port = port;
        this.http = new RawHttp();
        this.clientRepository = clientRepository;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                handleClientRequest(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        new Thread(() -> {
            try {
                RawHttpRequest request = http.parseRequest(clientSocket.getInputStream());
                System.out.println("Received request: " + request.getStartLine());

                RequestRouter router = new RequestRouter(clientRepository);
                RawHttpResponse<?> response = router.route(request);

                OutputStream outputStream = clientSocket.getOutputStream();
                response.writeTo(outputStream);
                outputStream.flush();
            } catch (IOException e) {
                System.err.println("Error processing client request: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }).start();
    }
}
