package part01;

import java.util.ArrayList;
import java.util.HashMap;

public class AnnualPlan {

    // Variables:

    private String id = "Unknown";
    private int year = -1;

    private ArrayList<Exhibit> exhibits = new ArrayList<Exhibit>();
    private HashMap<Exhibit, String> exhibitHalls = new HashMap<Exhibit, String>();

    public AnnualPlan(int year, String id) {
        setYear(year);
        setId(id);
    }

    // Getter methods:

    public int getYear() { return year; }
    public String getId() {return id; }
    public String getTotalExhibits() { return Integer.toString(exhibits.size()); }

    // Setter methods:
    public void setYear(int year) { this.year = year; }
    public void setId(String id) { this.id = id; }



    public void addExhibit(Exhibit exhibit, String hall) {
        exhibits.add(exhibit);
        exhibitHalls.put(exhibit, hall);
    }
    public void removeExhibit(Exhibit exhibit) {
        exhibits.remove(exhibit);
    }
}
