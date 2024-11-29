package cat.uvic.teknos.shoeshop.clients.manager;

import cat.uvic.teknos.shoeshop.clients.dto.ShoeDto;
import cat.uvic.teknos.shoeshop.clients.dto.ModelDto;
import cat.uvic.teknos.shoeshop.clients.dto.InventoryDto;
import cat.uvic.teknos.shoeshop.clients.exceptions.RequestException;
import cat.uvic.teknos.shoeshop.clients.utils.Mappers;
import cat.uvic.teknos.shoeshop.clients.utils.RestClient;
import cat.uvic.teknos.shoeshop.clients.utils.RestClientImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.freva.asciitable.AsciiTable;
import com.github.freva.asciitable.Column;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ShoeManager {

    private final RestClientImpl restClient;
    private final BufferedReader in;
    private final PrintStream out;

    public ShoeManager(RestClientImpl restClient, BufferedReader in) {
        this.restClient = restClient;
        this.in = in;
        this.out = new PrintStream(System.out);
    }

    public void start() throws RequestException, JsonProcessingException {
        String command;
        do {
            showShoeMenu();
            command = readLine(in);

            switch (command) {
                case "1" -> listAllShoes();
                case "2" -> showShoeDetails();
                case "3" -> addNewShoe();
                case "4" -> deleteShoe();
                case "5" -> updateShoe();
                default -> out.println("Comanda no vàlida.");
            }

        } while (!command.equals("exit"));
    }

    private void showShoeMenu() {
        out.println("\n" + "=".repeat(50));
        out.println("🌐       GESTIÓ DE SABATES       🌐");
        out.println("=".repeat(50));
        out.printf("%-3s %-30s %n", "1.", "📋 Llista de totes les sabates");
        out.printf("%-3s %-30s %n", "2.", "🔍 Detalls d'una sabata");
        out.printf("%-3s %-30s %n", "3.", "✏️ Afegir una nova sabata");
        out.printf("%-3s %-30s %n", "4.", "🗑️ Eliminar una sabata existent");
        out.printf("%-3s %-30s %n", "5.", "🛠️ Editar una sabata existent");
        out.println("\nEscriu 'exit' per sortir.");
        out.println("=".repeat(50));
        out.print("Selecciona una opció: ");
    }

    private void listAllShoes() throws RequestException {
        try {
            var shoes = restClient.getAll("/shoe", ShoeDto[].class);  // Recuperem totes les sabates
            if (shoes == null || shoes.length == 0) {
                out.println("⚠️ No hi ha sabates per mostrar.");
            } else {
                showShoesTable(shoes);
            }
        } catch (RequestException e) {
            out.println("❌ Error al obtenir la llista de sabates: " + e.getMessage());
        }
    }

    private void showShoeDetails() throws RequestException {
        out.print("\nEnter the Shoe ID to view details: ");
        var shoeId = readLine(in);

        try {
            var shoe = restClient.get("/shoe/" + shoeId, ShoeDto.class);  // Recuperem una sabata per ID

            if (shoe != null) {
                out.println("\n📋 Detalls de la sabata:");
                out.printf("%-20s: %-30s%n", "📍 ID", shoe.getId());
                out.printf("%-20s: %-30s%n", "📍 Preu", shoe.getPrice());
                out.printf("%-20s: %-30s%n", "📍 Color", shoe.getColor());
                out.printf("%-20s: %-30s%n", "📍 Talla", shoe.getSize());

                ModelDto model = (ModelDto) shoe.getModels();
                out.printf("%-20s: %-30s%n", "📍 Model", model != null ? model.getName() : "No disponible");

                InventoryDto inventory = (InventoryDto) shoe.getInventories();
                if (inventory != null) {
                    out.printf("%-20s: %-30s%n", "📍 Capacitat inventari", inventory.getCapacity());
                    out.printf("%-20s: %-30s%n", "📍 Inventari sabates", inventory.getShoes() != null ? inventory.getShoes().size() : "No disponible");
                } else {
                    out.println("📍 Inventari: No disponible");
                }
            } else {
                out.println("⚠️ La sabata amb ID " + shoeId + " no existeix.");
            }
        } catch (RequestException e) {
            out.println("❌ Error al obtenir la sabata: " + e.getMessage());
        }
    }

    private void addNewShoe() throws RequestException, JsonProcessingException {
        out.print("📦 Insereix el preu de la sabata: ");
        var shoe = new ShoeDto();
        shoe.setPrice(Double.parseDouble(readLine(in)));

        out.print("🎨 Insereix el color de la sabata: ");
        shoe.setColor(readLine(in));

        out.print("👟 Insereix la talla de la sabata: ");
        shoe.setSize(readLine(in));

        out.print("📛 Insereix el nom del model: ");
        var model = new ModelDto();
        model.setName(readLine(in));

        out.print("🏷️ Insereix la marca del model: ");
        model.setBrand(readLine(in));

        shoe.setModels(model);

        out.print("📦 Insereix la capacitat de l'inventari: ");
        var inventory = new InventoryDto();
        inventory.setCapacity(Integer.parseInt(readLine(in)));

        shoe.setInventories(inventory);

        try {
            restClient.post("/shoe", Mappers.get().writeValueAsString(shoe));
            out.println("✅ Sabata afegida correctament!");
        } catch (JsonProcessingException | RequestException e) {
            out.println("❌ Error al afegir la sabata: " + e.getMessage());
        }
    }

    private void deleteShoe() throws RequestException {
        out.print("\nID de la sabata a eliminar: ");
        var shoeId = readLine(in);

        try {
            var shoe = restClient.get("/shoe/" + shoeId, ShoeDto.class);

            if (shoe != null) {
                restClient.delete("/shoe/" + shoeId, null);
                out.println("✅ Sabata eliminada correctament.");
            } else {
                out.println("⚠️ No s'ha trobat cap sabata amb ID " + shoeId);
            }
        } catch (RequestException e) {
            out.println("❌ Error al eliminar la sabata: " + e.getMessage());
        }
    }

    private void updateShoe() throws RequestException, JsonProcessingException {
        out.print("\nEnter the Shoe ID to update: ");
        var shoeId = readLine(in);

        var existingShoe = restClient.get("/shoe/" + shoeId, ShoeDto.class);
        if (existingShoe == null) {
            out.println("⚠️ La sabata amb ID " + shoeId + " no existeix.");
            return;
        }

        out.println("\nActualitzant la sabata amb ID: " + shoeId);
        out.println("(Prem Enter per mantenir els valors existents)");

        out.print("📦 Preu actual (" + existingShoe.getPrice() + "): ");
        String price = readLine(in);
        if (!price.isEmpty()) existingShoe.setPrice(Double.parseDouble(price));

        out.print("🎨 Color actual (" + existingShoe.getColor() + "): ");
        String color = readLine(in);
        if (!color.isEmpty()) existingShoe.setColor(color);

        out.print("👟 Talla actual (" + existingShoe.getSize() + "): ");
        String size = readLine(in);
        if (!size.isEmpty()) existingShoe.setSize(size);

        var model = existingShoe.getModels();
        out.print("📛 Model actual (" + model.getName() + "): ");
        String modelName = readLine(in);
        if (!modelName.isEmpty()) model.setName(modelName);

        out.print("🏷️ Marca actual (" + model.getBrand() + "): ");
        String modelBrand = readLine(in);
        if (!modelBrand.isEmpty()) model.setBrand(modelBrand);

        existingShoe.setModels(model);

        var inventory = existingShoe.getInventories();
        out.print("📦 Capacitat actual (" + inventory.getCapacity() + "): ");
        String inventoryCapacity = readLine(in);
        if (!inventoryCapacity.isEmpty()) inventory.setCapacity(Integer.parseInt(inventoryCapacity));

        existingShoe.setInventories(inventory);

        try {
            restClient.put("/shoe/" + shoeId, Mappers.get().writeValueAsString(existingShoe));
            out.println("✅ Sabata actualitzada correctament!");
        } catch (JsonProcessingException | RequestException e) {
            out.println("❌ Error en actualitzar la sabata: " + e.getMessage());
        }
    }

    private void showShoesTable(ShoeDto[] shoes) {
        if (shoes == null || shoes.length == 0) {
            out.println("No hi ha sabates per mostrar.");
            return;
        }

        String[] headers = {"ID", "Preu", "Color", "Talla"};

        List<List<String>> rows = Arrays.stream(shoes)
                .map(shoe -> Arrays.asList(
                        String.valueOf(shoe.getId()),
                        String.format("%.2f", shoe.getPrice()),
                        shoe.getColor() != null ? shoe.getColor() : "Sense color",
                        shoe.getSize() != null ? shoe.getSize() : "Sense talla"
                ))
                .toList();

        StringBuilder table = new StringBuilder();
        table.append(String.join(" | ", headers)).append("\n");
        table.append("-".repeat(50)).append("\n"); // Línia de separació

        for (List<String> row : rows) {
            table.append(String.join(" | ", row)).append("\n");
        }

        out.println(table.toString());
    }


    private String readLine(BufferedReader in) {
        try {
            return in.readLine();
        } catch (IOException e) {
            out.println("❌ Error en llegir entrada: " + e.getMessage());
            return "";
        }
    }
}
