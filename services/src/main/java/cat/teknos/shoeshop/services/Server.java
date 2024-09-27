package cat.teknos.shoeshop.services;

import rawhttp.core.RawHttp;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {

    public static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        var serverSocket = new ServerSocket(PORT);

        while (true) {

            try (var clientSocket = serverSocket.accept()){

                var rawHttp = new RawHttp();
                var request = rawHttp.parseRequest(clientSocket.getInputStream());


            }

        }
    }
}