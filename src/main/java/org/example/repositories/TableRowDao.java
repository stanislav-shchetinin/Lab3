package org.example.repositories;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.models.TableRow;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
@Getter
public class TableRowDao implements TableRowRepo, Serializable {

    List<TableRow> tableRowList = new ArrayList<>();
    Connection conn;

    public TableRowDao(){
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            this.conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/web",
                    "postgres", "09082001");
        } catch (SQLException e){
            e.printStackTrace();
        }
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
            PreparedStatement pstmt = this.conn.prepareStatement(
                    "INSERT INTO point(x, y, radius, res, create_date, time_ex) VALUES (" +
                            "?, ?, ?, ?, ?, ?" +
                            ")");
            pstmt.setDouble(1, row.getForm().getPoint().getX());
            pstmt.setDouble(2, row.getForm().getPoint().getY());
            pstmt.setInt(3, row.getForm().getRadius());
            pstmt.setBoolean(4, row.checkInArea());
            pstmt.setDate(5, row.getCurrentDate());
            pstmt.setLong(6, row.getRequestTime());
            pstmt.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        }

        //просто смапить на бд объект и добавить в бд. из бд ничего браться не будет, т.к. это неоптимально + по заданию должно быть только хранение
    }

    @Override
    public void clearAll() {
        tableRowList.clear();
    }

    public void updateCanvas(TableRow row){
        PrimeFaces.current().executeScript(String.format("redrawCanvas(%s, %d)",
                tableRowList.toString(), row.getForm().getRadius()));
    }
}
