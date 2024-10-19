package cat.teknos.shoeshop.services.controllers;

import cat.uvic.teknos.shoeshop.models.Client;

public class ClientController implements Controller <Integer, Client>{

    @Override
    public String get(Integer integer) {

        // retrieve (get) student from db
        // serialize student in json format

        return ""; // JSON
    }

    @Override
    public String get() {

        return null;

    }

    @Override
    public void post(Client value) {

    }

    @Override
    public void put(Integer key, Client value) {

    }

    @Override
    public void delete(Integer key) {

    }
}
