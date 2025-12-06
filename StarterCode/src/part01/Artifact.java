package part01;

import java.util.ArrayList;

public class Artifact {
    // Variables
    private String name = "Unknown";
    private String type = "Unknown";
    private String description = "Unknown";
    private String id = "Unknown";
    private int engagementMinutes = 0;

    // Constructor method taking all inputs + setting them
    public Artifact(String type, String name, String description, int engagementMinutes, String id)  {
        // Uses setter methods to set values to given values
        setName(name);
        setType(type);
        setDescription(description);
        setEngagementMinutes(engagementMinutes);
        setId(id);
    }

    // Getter methods
    public String getName() { return name; }
    public String getType() {return type; }
    public String getDescription() {return description; }
    public String getId() { return id; }
    public int getEngagementMinutes() { return engagementMinutes; }

    // Setter methods
    public void setName(String name) { this.name = name; }
    public void setType(String type) {this.type = type;}
    public void setDescription(String description) {this.description = description; }
    public void setId(String id) { this.id = id; }
    public void setEngagementMinutes(int engagementMinutes) { this.engagementMinutes = engagementMinutes; }

    @Override
    public String toString() {
        return "Artifact [name=" + name + ", type=" + type + ", description=" + description + ", id=" + id +  ", engagementMinutes=" + engagementMinutes + "]";
    }
}
