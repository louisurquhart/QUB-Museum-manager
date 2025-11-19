package part01;

import java.util.ArrayList;

public class Exhibit {

    // Variables + list to store artifacts
    String name = "Unknown";
    String description = "Unknown";
    String id = "Unknown";
    int engagementTime = -1;

    ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

    // Constructor to create exhibit
    public Exhibit(String name, String description, String id)  {
        setName(name);
        setDescription(description);
        setId(id);
    }

    // Getter methods:
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getId() { return id; }
    public int getEngagementTime() { return engagementTime; }

    // Setter methods:
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setId(String id) { this.id = id; }
    public void setEngagementTime(int engagementTime) { this.engagementTime = engagementTime; }

}