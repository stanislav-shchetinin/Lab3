package org.example.repositories;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import org.example.models.TableRow;

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
        tableRowList.add(row);
    }

    @Override
    public void clearAll() {
        tableRowList.clear();
    }
}
