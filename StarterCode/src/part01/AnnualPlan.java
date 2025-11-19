package part01;

import java.util.ArrayList;

public class AnnualPlan {

    // Variables:
    String id = "Unknown";
    int year = -1;

    ArrayList<Exhibit> Exhibits = new ArrayList<Exhibit>();

    AnnualPlan(int year) {
        setYear(year);
    }

    // Getter methods:
    public int getYear() { return year; }
    public String getId() {return id;}

    // Setter methods:
    public void setYear(int year) { this.year = year; }
    public void setId(String id) { this.id = id; }
}
