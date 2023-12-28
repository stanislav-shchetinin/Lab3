package org.example.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import org.example.models.TableRow;
import org.primefaces.PrimeFaces;

@Named
@ApplicationScoped
public class Canvas {
    public void changeRadius(int radius){
        PrimeFaces.current().executeScript(String.format("changeRadius(%d)", radius));
    }
}
