package part01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import api.QUBMuseumAPI;

public class QUBMuseum implements QUBMuseumAPI {

    HashMap<String, Artifact> artifacts = new HashMap<>();
    HashMap<String, Exhibit> exhibits = new HashMap<>();
    HashMap<String, AnnualPlan> annualPlans = new HashMap<>();

	public static void main(String[] args) {
		// Create API which manages all business logic
		QUBMuseum api = new QUBMuseum();
		// Create UI instance
		Scanner input = new Scanner(System.in);

		// Main loop
		boolean running = true;
		while (running) {
			// Display current prompt
			System.out.println(api.getCurrentPrompt());
			// Get user input
			String userInput = input.nextLine().trim();
			// Process input through state machine
			String result = api.processInput(userInput);
			// Check for exit condition
			if ("EXIT".equals(result)) { running = false;} 
			else if (!result.isEmpty()) {
				System.out.println(result);
			}
		}
	}
	
    // --- Artifact Management ---
    // TODO: COME UP WITH BETTER ID SETTING IDEA -> CURRENT ONE WONT WORK IF STUFF'S DELETED
	@Override
	public String createArtifact(String type, String name, String description, int engagementMinutes) throws Exception {
        try {
            String id = Integer.toString(artifacts.size()); // Creates an incremental ID for the new artifact (+1 of previous artifacts ID)
            Artifact artifact = new Artifact(type, name, description, engagementMinutes, id); // Creates a new artifact with given parameters
            artifacts.put(id, artifact); // Adds the artifact to the hashmap with the generated ID
            return id; // Returns the ID
        } catch (Exception e) {
            System.err.println(e.getMessage()); // Outputs exception
            return null; // Returns null as creation was unsuccessful (as documentation says)
        }
	}

	@Override
	public String getArtifactInfo(String artifactId, String infoName) throws Exception
	{
        // Finds the artifact using its ID as a key for the artifacts hashmap
        Artifact artifact = artifacts.get(artifactId);
        // Depending on infoName, it then returns the appropiate info (info names based off the API documentation)
        return switch (infoName) {
            case "name" -> artifact.getName();
            case "type" -> artifact.getType();
            case "description" -> artifact.getDescription();
            case "engagement_minutes" -> Integer.toString(artifact.getEngagementMinutes());
            default -> null;
        };
    }

	@Override
	public int getArtifactEngagementTime(String artifactId) throws Exception {
        return artifacts.get(artifactId).getEngagementMinutes();
	}

	@Override
	public void updateArtifactInfo(String artifactId, String infoName, String newValue) throws Exception
	{
        // Finds the artifact using its ID as a key for the artifacts hashmap
        Artifact artifact = artifacts.get(artifactId);

        // Depending on infoName, it then returns the appropriate
        switch (infoName) {
            case "name" -> artifact.setName(newValue);
            case "type" -> artifact.setType(newValue);
            case "description" -> artifact.setDescription(newValue);
            case "engagement_minutes" -> artifact.setEngagementMinutes(Integer.parseInt(newValue));
            default -> throw new Exception("Invalid artifact info name given");
        };
	}
// Needs to delete ALL instances of artifact
	@Override
	public void deleteArtifact(String artifactId) throws Exception
    {
        // For each artifact it should store which exhibits the artifact is stored within (somehow) -> for efficient deletion
	}

	@Override
	public ArrayList<String> findArtifacts(String searchCriteria, String sortBy) throws Exception
	{

        // Need to figure out how to sort the return effectively
		return null;
	}

    // --- Exhibit Management ---
    // TODO: COME UP WITH BETTER ID SETTING IDEA -> CURRENT ONE WONT WORK IF STUFF'S DELETED
	@Override
	public String createExhibit(String name, String description) throws Exception
	{
        try {
            String id = Integer.toString(exhibits.size());
            exhibits.put(id, new Exhibit(name, description, id));
            return id; // Returns ID (as documentation outlines)
        } catch (Exception ex) {
            return null; // Returns null if nothing was found (as documentation outlines)
        }
	}

	@Override
	public String getExhibitInfo(String exhibitId, String infoName) throws Exception
	{
        // Finds the exhibit using its ID as a key for the exhibits hashmap
        Exhibit exhibit = exhibits.get(exhibitId);
        // Depending on infoName, it then returns the appropiate info (info names based off the API documentation)
        return switch (infoName) {
            case "name" -> exhibit.getName();
            case "description" -> exhibit.getDescription();
            default -> null;
        };
	}

	@Override
	public String getExhibitArtifactSign(String exhibitId, String artifactId) throws Exception {
		return null;
	}

	@Override
	public int getExhibitEngagementTime(String exhibitId) throws Exception {
		return exhibits.get(exhibitId).getEngagementTime();
	}

	@Override
	public void updateExhibitInfo(String exhibitId, String infoName, String newValue) throws Exception
	{
        if(newValue.isEmpty()) { throw new Exception("New value cannot be empty"); } // Validates newValue isn't empty

		Exhibit exhibit = exhibits.get(exhibitId);
        switch (infoName) {
            case "name" -> exhibit.setName(newValue);
            case "description" -> exhibit.setDescription(newValue);
            default -> throw new Exception("Invalid artifact info name given");
        };
	}

	@Override
	public void deleteExhibit(String exhibitId) throws Exception
	{

	}

	@Override
	public ArrayList<String> findExhibits(String searchCriteria, String sortBy) throws Exception
	{
		return null;
	}

	@Override
	public void addArtifactToExhibit(String artifactId, String exhibitId, String sign) throws Exception
	{

		Exhibit exhibit = exhibits.get(exhibitId);
        Artifact artifact = artifacts.get(artifactId);
	}

	@Override
	public void removeArtifactFromExhibit(String artifactId, String exhibitId) throws Exception
	{
		
	}

	@Override
	public ArrayList<String> getExhibitArtifacts(String exhibitId) throws Exception
	{
		return null;
	}

	@Override
	public void reorderExhibitArtifacts(String exhibitId, ArrayList<String> artifactIds) throws Exception
	{
		
	}

    // --- Annual Plan Management ---

	@Override
	public String createAnnualPlan(int year) throws Exception
	{
        String id = Integer.toString(annualPlans.size()); // TODO: BETTER ID GENERATION SYSYEM
        annualPlans.put(id, new AnnualPlan(year));
		return id;
	}

	@Override
	public String getAnnualPlan(int year) throws Exception
	{
		return "";
	}

	@Override
    public void addExhibitToAnnualPlan(String exhibitId, String planId, String hall, int month) throws Exception
	{

	}

	@Override
	public ArrayList<String> getAnnualPlanExhibits(String planId, String hall, int month) throws Exception
	{
		return null;
	}

	@Override
	public void deleteAnnualPlan(String planId) throws Exception
	{
		
	}

	@Override
	public String getAnnualPlanInfo(String planId, String infoName) throws Exception
	{
		return "";
	}

	@Override
	public void updateAnnualPlanInfo(String planId, String infoName, String newValue) throws Exception
	{
		
	}

    // --- Console UI Testing Support ---

	@Override
	public String getCurrentState() 
	{
		return "";
	}

	@Override
	public String getCurrentPrompt()
	{
		return "";
	}

	@Override
    public boolean isValidInput(String input) 
    {
    	return true;
    }
    
	@Override
	public ArrayList<String> getValidInputs()
    {
    	return null;
    }

	@Override
    public ArrayList<String> getInvalidInputs()
    {
    	return null;
    }

	@Override
	public String processInput(String input)
    {
    	return "EXIT";
    }

}
