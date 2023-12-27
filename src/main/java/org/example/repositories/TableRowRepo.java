package org.example.repositories;

import org.example.models.TableRow;

public interface TableRowRepo {
    void addTableRow(TableRow row);
    void clearAll();
}
