package cat.uvic.teknos.shoeshop.services;

import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpOptions;
import rawhttp.core.RawHttpResponse;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int PORT = 9998;
    private final RequestRouter requestRouter;
    private final ExecutorService executor;
    private boolean SHUTDOWN_SERVER;
    private ServerSocket serverSocket;

    public Server(RequestRouter requestRouter) throws IOException {
        this.requestRouter = requestRouter;
        this.executor = Executors.newFixedThreadPool(3);
        this.SHUTDOWN_SERVER = true;
        this.serverSocket = new ServerSocket(PORT); // Initialize server socket in constructor
        System.out.println("Server started on port " + PORT);
    }

    public void start() {
        try {
            while (SHUTDOWN_SERVER) {
                try {
                    var clientSocket = serverSocket.accept();
                    executor.execute(() -> {
                        try {
                            var rawHttp = new RawHttp(RawHttpOptions.newBuilder().doNotInsertHostHeaderIfMissing().build());
                            var request = rawHttp.parseRequest(clientSocket.getInputStream());
                            RawHttpResponse<?> response = requestRouter.route(request);
                            response.writeTo(clientSocket.getOutputStream());
                            clientSocket.getOutputStream().flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                clientSocket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    System.out.println("Error acceptant connexió: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            stopServer();
        }
    }

    public void shutDown() {
        this.SHUTDOWN_SERVER = false;  // Corrected to instance variable
        System.out.println("Shutting down the server...");
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();  // Close the server socket to stop accepting new connections
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopServer() {
        executor.shutdown();  // Gracefully shut down the executor service
        System.out.println("Server stopped.");
    }
}
