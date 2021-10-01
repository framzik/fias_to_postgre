package ru.mycrg.fias;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.mycrg.fias.XmlParser.SCHEMA;
import static ru.mycrg.fias.XmlParser.TABLE_NAME;

public class Writer {

    private static final Logger log = Logger.getLogger(Writer.class);

    private final String TABLE = SCHEMA + "." + TABLE_NAME;

    private DataSource dataSource;

    public Writer() {
        dataSource = initDataSource();
    }

    public void writeValue(Map<String, String> info, File xmlFile) {
        log.info(String.format("Start writing info from %s", xmlFile.getName()));
        System.out.println(String.format("Start writing info from %s", xmlFile.getName()));

        int i = 0;
        String queryUpdate = "";

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {

            for (Map.Entry<String, String> query: info.entrySet()) {
                if (i % 1000 == 0) {
                    log.info(String.format("Processing....was wrote %s raws of %s", i, info.size()));
                    System.out.println(String.format("Was wrote %s raws of %s", i, info.size()));
                }

                String objectId = query.getKey();
                String value = query.getValue();
                String queryCount = String.format("select count(*) from %s where objectid='%s'", TABLE, objectId);
                ResultSet rs = stmt.executeQuery(queryCount);

                int count = -1;
                if (rs.next()) {
                    count = rs.getInt("count");
                }

                if (count == 0) {
                    queryUpdate = value;
                    stmt.executeUpdate(queryUpdate);
                    i++;
                } else if (count > 0) {
                    String params = initParams(value);
                    queryUpdate = String.format("update %s set %s where objectid='%s';", TABLE, params,
                                                objectId);
                    stmt.executeUpdate(queryUpdate);
                    i++;
                }
            }
        } catch (SQLException e) {
            log.error(String.format("Не удалось записать в БД, sql:[%s],error: %s", queryUpdate, e.getMessage()));
            System.out.println(String.format("Не удалось записать в БД, sql:[%s],error: %s", queryUpdate, e.getMessage()));
        }
        log.info(String.format("Was wrote %s raws of %s", i, info.size()));
        System.out.println(String.format("Was wrote %s raws of %s", i, info.size()));
    }

    private String initParams(String value) {
        String[] columns = value.substring(value.indexOf("(") + 1, value.indexOf(")")).split(",");
        String valueWithoutColumns = value.substring(value.indexOf(")"));
        String[] valuesWithRegex = valueWithoutColumns.substring(valueWithoutColumns.indexOf("(") + 1, valueWithoutColumns.lastIndexOf(")")).split("'");
        List<String> values = new ArrayList<>();
        for (int i = 1; i < valuesWithRegex.length; i += 2) {
            values.add(valuesWithRegex[i]);
        }
        String params = "";
        try{
            for (int i = 0; i < columns.length; i++) {
                params = String.join(",", params, columns[i] + "='" + values.get(i) + "'");
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("Hi");
        }

        return params.substring(1);
    }

    public void truncateDb() {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute(String.format("truncate %s;", TABLE));
        } catch (SQLException e) {
            log.error(String.format("Не удалось очистить БД: %s", e.getMessage()));
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
