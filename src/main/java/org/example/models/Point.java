package org.example.models;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
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
public class Point implements Serializable {
    private double x;
    private double y;

    @Override
    public Point clone() {
        Point clone = new Point();
        clone.x = x;
        clone.y = y;
        return clone;
    }
}
