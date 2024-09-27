package cat.teknos.shoeshop.services.controllers;

public interface Controller<K, V> {

    String get(K k);
    String getAll();
    void post(V value);
    void put(K key, V value);
    void delete(K key);

}
