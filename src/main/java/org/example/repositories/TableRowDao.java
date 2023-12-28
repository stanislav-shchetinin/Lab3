package org.example.repositories;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.SneakyThrows;
import org.example.models.TableRow;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
@Getter
public class TableRowDao implements TableRowRepo, Serializable {

    List<TableRow> tableRowList = new ArrayList<>();
    @Override
    public void addTableRow(TableRow row) {
        row.fillResult();
        tableRowList.add(row.clone());
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
