package org.example.repositories;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.models.Form;
import org.example.models.Point;
import org.example.models.TableRow;
import org.example.orm.Query;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
@Getter
public class TableRowDao implements TableRowRepo, Serializable {

    private static final String DRIVER = "org.postgresql.Driver";
    private static final String URL = "jdbc:postgresql://localhost:5432/web";
    private static final String TABLE_NAME = "point";

    private List<TableRow> tableRowList;
    private Connection conn;
    private final Query<TableRow> tableRowQuery;

    public TableRowDao(){
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.conn = DriverManager.getConnection(URL,
                    "postgres", "09082001");
        } catch (SQLException e){
            e.printStackTrace();
        }
        tableRowQuery = new Query<>();
        create();
        load();
    }

    @PreDestroy
    public void destroy(){
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void addTableRow(TableRow row) {
        row.fillResult();
        tableRowList.add(row.clone());

        try {
            tableRowQuery.insert(conn, TABLE_NAME, row);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clearAll() {
        tableRowList.clear();
        String query = String.format("DELETE FROM %s", TABLE_NAME);
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(){
        String query = "CREATE TABLE IF NOT EXISTS point (\n" +
                "    id SERIAL PRIMARY KEY,\n" +
                "    x DOUBLE PRECISION NOT NULL,\n" +
                "    y DOUBLE PRECISION NOT NULL,\n" +
                "    radius INTEGER,\n" +
                "    res BOOLEAN,\n" +
                "    create_date DATE,\n" +
                "    time_ex BIGINT\n" +
                ");";
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(query)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        try {
            tableRowList = tableRowQuery.select(conn, TABLE_NAME, TableRow.class);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | IllegalAccessException |
                 InstantiationException e) {
            e.printStackTrace();
        }
    }
}
