package ru.mycrg.fias;

import java.util.List;

public class TableInfo {

    String tableName;
    String columnNames;
    List<String> values;

    public TableInfo() {
    }

    public TableInfo(String tableName, String columnNames, List<String> values) {
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.values = values;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
