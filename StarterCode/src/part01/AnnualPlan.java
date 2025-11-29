package part01;

import java.util.ArrayList;
import java.util.HashMap;

public class AnnualPlan {

    private static class exhibitDisplayInfo {
        exhibitDisplayInfo(String hall, String month) { this.hall = hall; this.month = month; }
        String hall;
        String month;
    }

    // Variables:
    private String id = "Unknown";
    private int year = -1;

    private ArrayList<Exhibit> exhibits = new ArrayList<Exhibit>();
    private HashMap<Exhibit, exhibitDisplayInfo> exhibitDisplayInfos = new HashMap<>();

    public AnnualPlan(int year, String id) {
        setYear(year);
        setId(id);
    }

    // Getter methods:
    public int getYear() { return year; }
    public String getId() {return id; }
    public String getExhibitMonth(Exhibit exhibit) { return exhibitDisplayInfos.get(exhibit).month; }
    public String getExhibitHall(Exhibit exhibit) { return exhibitDisplayInfos.get(exhibit).hall; }
    public ArrayList<Exhibit> getExhibits() {  return exhibits; }

    // Setter methods:
    public void setYear(int year) { this.year = year; }
    public void setId(String id) { this.id = id; }

    // Add/remove methods:
    public void addExhibit(Exhibit exhibit, String hall, String month) {
        exhibits.add(exhibit); // Adds the exhibit to the exhibits array
        exhibitDisplayInfos.put(exhibit, new exhibitDisplayInfo(hall, month)); // Adds the extra info about the exhibit to exhibitDisplayInfos
    }
    public void removeExhibit(Exhibit exhibit) {
        exhibits.remove(exhibit);
        exhibitDisplayInfos.remove(exhibit);
    }
}
