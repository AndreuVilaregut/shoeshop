package cat.uvic.teknos.shoeshop.domain.jdbc.repositories;

import cat.uvic.teknos.shoeshop.models.Address;
import cat.uvic.teknos.shoeshop.models.Client;
import cat.uvic.teknos.shoeshop.models.ShoeStore;
import cat.uvic.teknos.shoeshop.repositories.ClientRepository;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})
public class JdbcClientRepository implements ClientRepository {

    private final Connection connection;

    public JdbcClientRepository(Connection connection){
        this.connection = connection;
    }

    private static final String INSERT_CLIENT = "INSERT INTO CLIENT (DNI, NAME, PHONE, ADDRESS_ID) VALUES (?,?,?,?)";
    private static final String INSERT_ADDRESS = "INSERT INTO ADDRESS (LOCATION) VALUES (?)";
    private static final String UPDATE_CLIENT = "UPDATE CLIENT SET DNI=?, NAME=?, PHONE=?, ADDRESS_ID=? WHERE CLIENT_ID = ?";
    private static final String UPDATE_ADDRESS = "UPDATE ADDRESS SET LOCATION = ? WHERE ADDRESS_ID = ?";
    private static final String DELETE_CLIENT = "DELETE FROM CLIENT WHERE CLIENT_ID = ?";
    private static final String DELETE_ADDRESS = "DELETE FROM ADDRESS WHERE ADDRESS_ID = ?";
    private static final String INSERT_SHOE_STORE = "INSERT INTO SHOE_STORE (NAME, OWNER, LOCATION) VALUES (?,?,?)";

    @Override
    public void save(Client model) {
        if (model.getId() <= 0) {
            insert(model);
        } else {
            update(model);
        }
    }

    private void insert(Client model) {
        try {
            connection.setAutoCommit(false);

            // Insert address if it doesn't exist
            if (model.getAddresses().getId() <= 0) {
                insertAddress(model.getAddresses());
            }

            try (PreparedStatement clientStatement = connection.prepareStatement(INSERT_CLIENT, Statement.RETURN_GENERATED_KEYS)) {
                clientStatement.setString(1, model.getDni());
                clientStatement.setString(2, model.getName());
                clientStatement.setString(3, model.getPhone());
                clientStatement.setInt(4, model.getAddresses().getId());

                clientStatement.executeUpdate();

                try (ResultSet keys = clientStatement.getGeneratedKeys()) {
                    if (keys.next()) {
                        model.setId(keys.getInt(1));
                    }
                }
            }

            if (model.getShoeStores().getId() <= 0) {
                insertShoeStore(model.getShoeStores());
            }


            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException(rollbackEx);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void update(Client model) {
        try {
            connection.setAutoCommit(false);

            // Update address
            if (model.getAddresses().getId() > 0) {
                updateAddress(model.getAddresses());
            }

            try (PreparedStatement clientStatement = connection.prepareStatement(UPDATE_CLIENT)) {
                clientStatement.setString(1, model.getDni());
                clientStatement.setString(2, model.getName());
                clientStatement.setString(3, model.getPhone());
                clientStatement.setInt(4, model.getAddresses().getId());
                clientStatement.setInt(5, model.getId());

                clientStatement.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException(rollbackEx);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void insertAddress(Address model) throws SQLException {
        try (PreparedStatement addressStatement = connection.prepareStatement(INSERT_ADDRESS, Statement.RETURN_GENERATED_KEYS)) {
            addressStatement.setString(1, model.getLocation());
            addressStatement.executeUpdate();

            try (ResultSet keys = addressStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    model.setId(keys.getInt(1));
                }
            }
        }
    }

    private void updateAddress(Address model) throws SQLException {
        try (PreparedStatement addressStatement = connection.prepareStatement(UPDATE_ADDRESS)) {
            addressStatement.setString(1, model.getLocation());
            addressStatement.setInt(2, model.getId());
            addressStatement.executeUpdate();
        }
    }

    @Override
    public void delete(Client model) {
        try {
            connection.setAutoCommit(false);

            String deleteClientShoeStore = "DELETE FROM CLIENT WHERE CLIENT_ID = ?";
            try (PreparedStatement clientShoeStoreStmt = connection.prepareStatement(deleteClientShoeStore)) {
                clientShoeStoreStmt.setInt(1, model.getId());
                clientShoeStoreStmt.executeUpdate();
            }

            try (PreparedStatement clientStatement = connection.prepareStatement(DELETE_CLIENT)) {
                clientStatement.setInt(1, model.getId());
                clientStatement.executeUpdate();
            }

            if (model.getAddresses() != null) {
                try (PreparedStatement addressStatement = connection.prepareStatement(DELETE_ADDRESS)) {
                    addressStatement.setInt(1, model.getAddresses().getId());
                    addressStatement.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                throw new RuntimeException(rollbackEx);
            }
            throw new RuntimeException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }



    private void insertShoeStore(ShoeStore model) throws SQLException {
        try (PreparedStatement shoeStoreStatement = connection.prepareStatement(INSERT_SHOE_STORE)) {
            shoeStoreStatement.setString(1, (model.getName()));
            shoeStoreStatement.setString(2, (model.getOwner()));
            shoeStoreStatement.setString(3, (model.getLocation()));

            shoeStoreStatement.executeUpdate();
        }
    }

    @Override
    public Client get(Integer id) {
        String query = "SELECT * FROM CLIENT WHERE CLIENT_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return getClient(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Set<Client> getAll() {
        String query = "SELECT * FROM CLIENT";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            Set<Client> clients = new HashSet<>();
            while (resultSet.next()) {
                clients.add(getClient(resultSet));
            }
            return clients;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Client getClient(ResultSet resultSet) throws SQLException {
        Client client = new cat.uvic.teknos.shoeshop.domain.jdbc.models.Client();
        client.setId(resultSet.getInt("CLIENT_ID"));
        client.setDni(resultSet.getString("DNI"));
        client.setName(resultSet.getString("NAME"));
        client.setPhone(resultSet.getString("PHONE"));

        int addressId = resultSet.getInt("ADDRESS_ID");
        Address address = getAddressById(addressId);
        client.setAddresses(address);

        return client;
    }

    private Address getAddressById(int addressId) throws SQLException {
        String query = "SELECT * FROM ADDRESS WHERE ADDRESS_ID = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, addressId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Address address = new cat.uvic.teknos.shoeshop.domain.jdbc.models.Address();
                    address.setId(resultSet.getInt("ADDRESS_ID"));
                    address.setLocation(resultSet.getString("LOCATION"));
                    return address;
                }
            }
        }
        return null;
    }
}
