package cat.uvic.teknos.shoeshop.domain.jdbc.repositories;

import cat.uvic.teknos.shoeshop.models.Shoe;
import cat.uvic.teknos.shoeshop.repositories.ShoeRepository;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})

public class JdbcShoeRepository implements ShoeRepository {

    private final Connection connection;

    public JdbcShoeRepository(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(Shoe model) {
        if (model.getId() <= 0) {
            insert(model);
        } else {
            update(model);
        }
    }

    private void insert(Shoe model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO SHOE (ID_SHOE) VALUES (?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, model.getName());
            statement.setInt(2, model.getModelId());
            statement.setInt(3, model.getInventoryId());
            statement.setDouble(4, model.getPrice());
            statement.setString(5, model.getColor());
            statement.setString(6, model.getSize());
            statement.executeUpdate();

            var keys = statement.getGeneratedKeys();
            if (keys.next()) {
                model.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Shoe", e);
        }
    }

    private void update(Shoe model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE SHOE SET NAME = ?, MODEL_ID = ?, INVENTORY_ID = ?, PRICE = ?, COLOR = ?, SIZE = ? WHERE ID = ?")) {

            statement.setString(1, model.getName());
            statement.setInt(2, model.getModelId());
            statement.setInt(3, model.getInventoryId());
            statement.setDouble(4, model.getPrice());
            statement.setString(5, model.getColor());
            statement.setString(6, model.getSize());
            statement.setInt(7, model.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("No items to update");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error updating Shoe", e);
        }
    }

    @Override
    public void delete(Shoe model) {
        try (PreparedStatement statement = connection.prepareStatement(
                "DELETE FROM SHOE WHERE ID = ?")) {
            statement.setInt(1, model.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No item to delete");
            } else {
                System.out.println("Shoe deleted successfully");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting Shoe", e);
        }
    }

    @Override
    public Shoe get(Integer id) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM SHOE WHERE ID = ?")) {
            Shoe shoe = null;
            statement.setInt(1, id);

            var resultSet = statement.executeQuery();
            if (resultSet.next()) {
                shoe = new cat.uvic.teknos.shoeshop.domain.jdbc.models.Shoe();
                shoe.setId(resultSet.getInt("ID"));
                shoe.setName(resultSet.getString("NAME"));
                shoe.setModelId(resultSet.getInt("MODEL_ID"));
                shoe.setInventoryId(resultSet.getInt("INVENTORY_ID"));
                shoe.setPrice(resultSet.getDouble("PRICE"));
                shoe.setColor(resultSet.getString("COLOR"));
                shoe.setSize(resultSet.getString("SIZE"));
            }
            return shoe;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching Shoe", e);
        }
    }

    @Override
    public Set<Shoe> getAll() {
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM SHOE")) {
            var shoes = new HashSet<Shoe>();

            var resultSet = statement.executeQuery();
            while (resultSet.next()) {
                var shoe = new cat.uvic.teknos.shoeshop.domain.jdbc.models.Shoe();
                shoe.setId(resultSet.getInt("ID"));
                shoe.setName(resultSet.getString("NAME"));
                shoe.setModelId(resultSet.getInt("MODEL_ID"));
                shoe.setInventoryId(resultSet.getInt("INVENTORY_ID"));
                shoe.setPrice(resultSet.getDouble("PRICE"));
                shoe.setColor(resultSet.getString("COLOR"));
                shoe.setSize(resultSet.getString("SIZE"));

                shoes.add(shoe);
            }
            return shoes;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching Shoes", e);
        }
    }
}
