package part01;

import java.util.ArrayList;
import java.util.HashMap;

public class Exhibit {

    // Variables + list to store artifacts
    String name = "Unknown";
    String description = "Unknown";
    String id = "Unknown";

    ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

    // Hashmap to store a sign for each artifact
    HashMap<String, String> artifactSigns = new HashMap<>(); // (ArtifactID, Sign)

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

    public int getEngagementTime() {
        int engagementTime = 0;
        for(Artifact a : artifacts) {
            engagementTime += a.getEngagementMinutes();
        }
        return engagementTime;
    }

    // Setter methods:
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setId(String id) { this.id = id; }

    public void addArtifact(Artifact artifact, String artifactSign) {
        // Adds artifact to artifacts arraylist:
        this.artifacts.add(artifact);
        // Adds the artifacts sign to the artifactSigns hashmap with (id, sign):
        this.artifactSigns.put(artifact.getId(), artifactSign);

    }

}