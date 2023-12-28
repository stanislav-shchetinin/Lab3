package org.example.models;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Form implements Serializable {

    private Point point = new Point();
    private int radius;

    @Override
    public Form clone() {
        Form clone = new Form();
        clone.point = point.clone();
        clone.radius = radius;
        return clone;
    }
}
