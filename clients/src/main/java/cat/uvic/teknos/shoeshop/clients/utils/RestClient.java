package cat.uvic.teknos.shoeshop.clients.utils;

import cat.uvic.teknos.shoeshop.clients.exceptions.RequestException;

public interface RestClient {
    <T> T get(String path, Class<T> returnType) throws RequestException;
    <T> T[] getAll(String path, Class<T[]> returnType) throws RequestException;
    void post(String path, String body) throws RequestException;
    void put(String path, String body) throws RequestException;
    void delete(String path, String body) throws RequestException;
    void asd(String path) throws RequestException;  // Keep a single delete method
// Keep a single delete method
}

