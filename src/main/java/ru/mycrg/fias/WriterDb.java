package ru.mycrg.fias;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class WriterDb {

    final String INSERT_INTO = "insert into %s.%s (%s) values (%s) ";

    public int write(Map<Integer, String> info) {
        DataSource dataSource = initDataSource();
        int i = 0;
        
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            for (Map.Entry<Integer, String> q: info.entrySet()) {
                stmt.executeUpdate(q.getValue());
                i++;
            }
        } catch (SQLException e) {
            e.getMessage();
        }

        return i;
    }

    private DataSource initDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5434/Fias");
        dataSource.setUsername("fiz");
        dataSource.setPassword("314");

        return dataSource;
    }
}
