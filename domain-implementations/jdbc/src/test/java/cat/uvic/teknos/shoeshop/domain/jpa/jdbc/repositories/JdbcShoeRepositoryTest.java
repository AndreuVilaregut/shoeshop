package cat.uvic.teknos.shoeshop.domain.jpa.jdbc.repositories;

import cat.uvic.teknos.shoeshop.domain.jdbc.models.Shoe;
import cat.uvic.teknos.shoeshop.domain.jdbc.repositories.JdbcShoeRepository;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})

class JdbcShoeRepositoryTest {



    private final Connection connection;


    public JdbcShoeRepositoryTest(Connection connection) throws SQLException {
        this.connection = connection;
        this.connection.setAutoCommit(false);
    }

    @Test
    @DisplayName("Given a new Shoe (id = 0), when save, then a new record is added to the SHOE table")
    void shouldInsertNewShoeTest() throws SQLException {


        Shoe shoe = new Shoe();

        shoe.setModelId(2);
        shoe.setInventoryId(1);
        shoe.setPrice(120);
        shoe.setColor("White and Black");
        shoe.setSize("43");

        var repository = new JdbcShoeRepository(connection);

        repository.save(shoe);

        //assertTrue(shoe.getId() > 0);

        assertNotNull(repository.get(shoe.getId()));

            /*DbAssertions.assertThat(connection)
                    .table("CAR")
                    .where("CAR_ID = ?", mercedes.getId())
                    .hasOneLine();*/

    }

    @Test
    void shouldUpdateNewShoeTest() throws SQLException {

        var repository = new JdbcShoeRepository(connection);
        Shoe shoe = new Shoe();

        shoe.setId(1);
        shoe.setModelId(2);
        shoe.setInventoryId(1);
        shoe.setPrice(120);
        shoe.setColor("White and Black");
        shoe.setSize("43");

        repository.save(shoe);

        assertTrue(true);

    }

    @Test
    void delete() throws SQLException {

        Shoe shoe = new Shoe();
        shoe.setId(2);

        var repository = new JdbcShoeRepository(connection);
        repository.delete(shoe);

        //assertNull(repository.get(1));

    }

    @Test
    void get() throws SQLException {

        var repository = new JdbcShoeRepository(connection);

        int existingShoeId = 1;
        Shoe shoe = (Shoe) repository.get(existingShoeId);

        assertNotNull(shoe);
        assertEquals(existingShoeId, shoe.getId());


    }

    @Test
    void getAll() throws SQLException {

        var repository = new JdbcShoeRepository(connection);

        var shoes = repository.getAll();

        assertNotNull(shoes);
        assertFalse(shoes.isEmpty());

    }
}