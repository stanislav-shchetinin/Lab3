package org.example.models;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.*;
import org.example.orm.annotations.ColumnsName;
import org.example.orm.annotations.LookInside;
import org.primefaces.PrimeFaces;
import org.primefaces.component.tabview.Tab;

import java.io.Serializable;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@Named
@RequestScoped
@AllArgsConstructor
public class TableRow implements Serializable {

    @LookInside
    private Form form = new Form();

    @ColumnsName("create_date")
    private Date currentDate;

    @ColumnsName("time_ex")
    private Long requestTime;

    @ColumnsName("res")
    private Boolean result;
    public void fillResult(){
        long currentTime = System.currentTimeMillis();
        currentDate = new Date(currentTime);
        this.result = checkInArea();
        this.requestTime = System.currentTimeMillis() - currentTime;
    }

    public boolean checkInArea(){
        double x = this.form.getPoint().getX();
        double y = this.form.getPoint().getY();
        int r = this.form.getRadius();
        return y >= 0 && x >= 0 && r - x >= y ||
                y <= 0 && x <= 0 && x >= -r && y >= -r / 2. ||
                x <= 0 && y >= 0 && x * x + y * y <= (r / 2.) * (r / 2.);
    }

    @Override
    public String toString() {
        return String.format("{x: %f, y: %f, res: %b}",
                form.getPoint().getX(), form.getPoint().getY(), result);
    }

    @Override
    public TableRow clone() {
        TableRow clone = new TableRow();
        clone.form = form.clone();
        clone.currentDate = (Date) currentDate.clone();
        clone.requestTime = requestTime;
        clone.result = result;
        return clone;
    }
}
