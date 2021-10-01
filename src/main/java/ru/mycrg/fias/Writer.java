package ru.mycrg.fias;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import static ru.mycrg.fias.XmlParser.SCHEMA;
import static ru.mycrg.fias.XmlParser.TABLE_NAME;

public class Writer {

    private final Logger log = LoggerFactory.getLogger(Writer.class);
    private final String TABLE = SCHEMA + "." + TABLE_NAME;

    private DataSource dataSource;

    public Writer() {
        dataSource = initDataSource();
    }

    public int writeValue(Map<String, String> info) {

        int i = 0;

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            for (Map.Entry<String, String> query: info.entrySet()) {
                String objectId = query.getKey();
                String value = query.getValue();
                String queryCount = String.format("select count(*) from %s where objectid='%s'", TABLE, objectId);
                ResultSet rs = stmt.executeQuery(queryCount);

                int count = -1;
                if (rs.next()) {
                    count = rs.getInt("count");
                }

                if (count == 0) {
                    stmt.executeUpdate(value);
                    i++;
                } else if (count > 0) {
                    String params = initParams(value);
                    String queryUpdate = String.format("update %s set %s where objectid='%s';", TABLE, params,
                                                       objectId);
                    stmt.executeUpdate(queryUpdate);
                    i++;
                }
            }
        } catch (SQLException e) {
            log.error("Не удалось записать в БД: {}", e.getMessage());
        }

        return i;
    }

    private String initParams(String value) {
        String[] columns = value.substring(value.indexOf("(") + 1, value.indexOf(")")).split(",");
        String[] values = value.substring(value.lastIndexOf("(") + 1, value.lastIndexOf(")")).split(",");
        String params = "";
        for (int i = 0; i < columns.length; i++) {
            params = String.join(",", params, columns[i] + "=" + values[i]);
        }
        return params.substring(1);
    }

    public void truncateDb() {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(String.format("truncate %s;", TABLE));
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
