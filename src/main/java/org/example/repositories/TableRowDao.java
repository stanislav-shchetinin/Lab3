package org.example.repositories;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import org.example.models.TableRow;
import org.example.orm.Query;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.List;
import java.util.Properties;

@Named
@SessionScoped
@Getter
public class TableRowDao implements TableRowRepo, Serializable {

    private static final String TABLE_NAME = "point";

    private List<TableRow> tableRowList;
    private Connection conn;
    private final Query<TableRow> tableRowQuery;

    public TableRowDao(){
        dataBaseConfig();
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

    public void dataBaseConfig(){

        Properties property = new Properties();
        try (InputStream fin = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/config.properties")) {
            property.load(fin);

            String driver = property.getProperty("db.driver");
            String url = property.getProperty("db.url");
            String login = property.getProperty("db.login");
            String password = property.getProperty("db.password");

            Class.forName(driver);
            this.conn = DriverManager.getConnection(url,
                    login, password);
        } catch (SQLException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
