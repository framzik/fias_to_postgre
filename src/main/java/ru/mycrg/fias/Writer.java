package ru.mycrg.fias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static ru.mycrg.fias.XmlParser.SCHEMA;
import static ru.mycrg.fias.XmlParser.TABLE_NAME;

public class Writer {

    private final Logger log = LoggerFactory.getLogger(Writer.class);

    private DataSource dataSource;

    public Writer() {
        dataSource = initDataSource();
    }

    public int writeValue(Map<Integer, String> info) {

        int i = 0;

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            for (Map.Entry<Integer, String> query: info.entrySet()) {
                stmt.executeUpdate(query.getValue());
                i++;
            }
        } catch (SQLException e) {
            log.error("Не удалось записать в БД: {}", e.getMessage());
        }

        return i;
    }

    public void truncateDb() {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(String.format("truncate %s.%s; ", SCHEMA, TABLE_NAME));
        } catch (SQLException e) {
            log.error("Не удалось очистить БД: {}", e.getMessage());
        }
    }

    private DataSource initDataSource() {
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();

        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
        driverManagerDataSource.setUrl("jdbc:postgresql://localhost:5434/Fias");
        driverManagerDataSource.setUsername("fiz");
        driverManagerDataSource.setPassword("314");

        return driverManagerDataSource;
    }
}
