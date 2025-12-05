package part01;

import java.util.ArrayList;
import java.util.HashMap;

public class Exhibit {

    // Variables + list to store artifacts
    private String name = "Unknown";
    private String description = "Unknown";
    private String id = "Unknown";

    private ArrayList<Artifact> artifacts = new ArrayList<Artifact>();

    // Hashmap to store a sign for each artifact
    private HashMap<Artifact, String> artifactSigns = new HashMap<>(); // (ArtifactID, Sign)

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
    public String getArtifactSign(Artifact artifact) { return artifactSigns.get(artifact); }
    public ArrayList<Artifact> getArtifacts() { return artifacts; }

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
    public void setArtifactSign(Artifact artifact) { this.artifactSigns.put(artifact, artifactSigns.get(artifact)); }

    public void addArtifact(Artifact artifact, String artifactSign) {
        this.artifacts.add(artifact); // Adds artifact to artifacts arraylist:
        this.artifactSigns.put(artifact, artifactSign); // Adds the artifacts sign to the artifactSigns hashmap with (id, sign):
    }
    public void removeArtifact(Artifact artifact) {
        artifactSigns.remove(artifact); // Removes the artifact id + sign from the artifactSigns hashmap
        this.artifacts.remove(artifact); // Removes artifact from artifacts arrayList
    }

    public String toString() {
// TODO: Figure out how to output artifacts paired with their signs
        return "Artifact [name=" + name + ", description=" + description + ", id=" + id + "\n Artifacts contained: ]";
    }
}