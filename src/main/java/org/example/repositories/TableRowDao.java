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

    private final List<TableRow> tableRowList = new ArrayList<>();
    private Connection conn;

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
            Query.insert(conn, "point", row);
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void clearAll() {
        tableRowList.clear();
        String query = "DELETE FROM point";
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(query)) {
            int rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateCanvas(TableRow row){
        PrimeFaces.current().executeScript(String.format("redrawCanvas(%s, %d)",
                tableRowList.toString(), row.getForm().getRadius()));
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
            int rows = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void load(){
        tableRowList.clear();
        String query = "SELECT * FROM point";
        try (PreparedStatement preparedStatement = this.conn.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                int radius = resultSet.getInt("radius");
                boolean result = resultSet.getBoolean("res");
                Date currentDate = resultSet.getDate("create_date");
                long requestTime = resultSet.getLong("time_ex");

                Point point = new Point(x, y);
                Form form = new Form(point, radius);
                TableRow tableRow = new TableRow(form, currentDate, requestTime, result);

                tableRowList.add(tableRow);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
