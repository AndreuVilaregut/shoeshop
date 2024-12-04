package cat.uvic.teknos.shoeshop.services;

import cat.uvic.teknos.shoeshop.services.exceptions.ServerException;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpOptions;
import rawhttp.core.RawHttpResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final String CONFIG_PATH = "services/src/main/resources/server.properties";
    private static final int CHECK_INTERVAL_SECONDS = 5;
    private final int PORT = 9998;
    private final RequestRouter requestRouter;
    private volatile boolean SHUTDOWN_SERVER = false;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public Server(RequestRouter requestRouter) {
        this.requestRouter = requestRouter;
        startShutdownChecker();
    }

    public void start() {
        try (var serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (!SHUTDOWN_SERVER) {
                try {
                    var clientSocket = serverSocket.accept();
                    threadPool.submit(() -> handleClientRequest(clientSocket));
                } catch (IOException e) {
                    if (!SHUTDOWN_SERVER) {
                        throw new ServerException("Error accepting connection", e);
                    }
                }
            }
        } catch (IOException e) {
            throw new ServerException("Server error", e);
        } finally {
            shutdownThreadPool();
            shutdownScheduler();
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        try (clientSocket) {
            var rawHttp = new RawHttp(RawHttpOptions.newBuilder().doNotInsertHostHeaderIfMissing().build());
            var request = rawHttp.parseRequest(clientSocket.getInputStream());
            RawHttpResponse<?> response = requestRouter.route(request);
            response.writeTo(clientSocket.getOutputStream());
        } catch (Exception e) {
            System.err.println("Error handling client request: " + e.getMessage());
            // You can log more details or return a custom error message to the client here if necessary
        }
    }

    private void startShutdownChecker() {
        scheduler.scheduleAtFixedRate(() -> {
            try (var input = new FileInputStream(CONFIG_PATH)) {
                Properties properties = new Properties();
                properties.load(input);
                String shutdownValue = properties.getProperty("shutdown", "false");

                if (Boolean.parseBoolean(shutdownValue)) {
                    System.out.println("Shutdown signal received. Stopping server...");
                    stop();
                }
            } catch (IOException e) {
                System.err.println("Error reading server.properties: " + e.getMessage());
            }
        }, 0, CHECK_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }

    public void stop() {
        SHUTDOWN_SERVER = true;
        System.out.println("Stopping server...");
        shutdownThreadPool();
        shutdownScheduler();
    }

    private void shutdownThreadPool() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void shutdownScheduler() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
