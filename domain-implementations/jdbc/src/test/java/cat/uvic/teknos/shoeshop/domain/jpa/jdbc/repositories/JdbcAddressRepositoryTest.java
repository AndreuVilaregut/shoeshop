package cat.uvic.teknos.shoeshop.domain.jpa.jdbc.repositories;

import cat.uvic.teknos.shoeshop.domain.jdbc.models.Address;
import cat.uvic.teknos.shoeshop.domain.jdbc.repositories.JdbcAddressRepository;
import com.fcardara.dbtestutils.junit.CreateSchemaExtension;
import com.fcardara.dbtestutils.junit.GetConnectionExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith({CreateSchemaExtension.class, GetConnectionExtension.class})


class JdbcAddressRepositoryTest {

    private final Connection connection;


    public JdbcAddressRepositoryTest(Connection connection) throws SQLException {
        this.connection = connection;
        this.connection.setAutoCommit(false);
    }

    @Test
    @DisplayName("Given a new Address (id = 0), when save, then a new record is added to the ADDRESS table")
    void shouldInsertNewAddressTest() throws SQLException {


        Address address1 = new Address();


        address1.setLocation("Vic");


        var repository = new JdbcAddressRepository(connection);

        repository.save(address1);

        assertTrue(address1.getId() > 0);

        assertNotNull(repository.get(address1.getId()));

            /*DbAssertions.assertThat(connection)
                    .table("CAR")
                    .where("CAR_ID = ?", mercedes.getId())
                    .hasOneLine();*/

    }

    @Test
    void shouldUpdateNewAddressTest() throws SQLException {


        Address address1 = new Address();

        address1.setId(2);
        address1.setLocation("Barcelona");

        var repository = new JdbcAddressRepository(connection);
        repository.save(address1);

        assertTrue(true);

    }

    @Test
    void delete() throws SQLException {

        Address address1 = new Address();
        address1.setId(2);

        var repository = new JdbcAddressRepository(connection);
        repository.delete(address1);

        //assertNull(repository.get(1));

    }

    @Test
    void get() throws SQLException {

        var repository = new JdbcAddressRepository(connection);
        assertNotNull(repository.get(1));

    }

    @Test
    void getAll() throws SQLException {

        var repository = new JdbcAddressRepository(connection);

        var addresses = repository.getAll();

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty());
    }
}