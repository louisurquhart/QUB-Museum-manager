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
    // DONE
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
    // DONE
	@Override
	public int getArtifactEngagementTime(String artifactId) throws Exception {
        return artifacts.get(artifactId).getEngagementMinutes();
	}
    // DONE
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
    // DONE
	@Override
	public void deleteArtifact(String artifactId) throws Exception {
        Artifact artifact = artifacts.get(artifactId); // Finds artifact using its ID

        for(Exhibit exhibit : exhibits.values()) { // Removes all references to the artifact in all exhibits
            exhibit.removeArtifact(artifact);
        }
        artifacts.remove(artifactId); // Then removes the artifact from the main artifacts hashmap.
	}

    // TODO: REALLY DIFFICULT
	@Override
	public ArrayList<String> findArtifacts(String searchCriteria, String sortBy) throws Exception
	{
        // SEARCHING ALGORITHM HERE

        // SORTING ALGORITHM HERE:

		return null;
	}

    // --- Exhibit Management ---
    // TODO: COME UP WITH BETTER ID SETTING IDEA -> CURRENT ONE WONT WORK IF STUFF'S DELETED
	@Override
	public String createExhibit(String name, String description) throws Exception  {
        try {
            String id = Integer.toString(exhibits.size());
            exhibits.put(id, new Exhibit(name, description, id));
            return id; // Returns ID (as documentation outlines)
        } catch (Exception ex) {
            return null; // Returns null if nothing was found (as documentation outlines)
        }
	}

    // DONE
	@Override
	public String getExhibitInfo(String exhibitId, String infoName) throws Exception
	{
        // Finds the exhibit using its ID as a key for the exhibits hashmap
        Exhibit exhibit = exhibits.get(exhibitId);
        // Depending on infoName, it then returns the appropriate info (info names based off the API documentation)
        return switch (infoName) {
            case "name" -> exhibit.getName();
            case "description" -> exhibit.getDescription();
            default -> null;
        };
	}

    // DONE
	@Override
	public String getExhibitArtifactSign(String exhibitId, String artifactId) throws Exception {
        Exhibit exhibit =  exhibits.get(exhibitId); // Gets exhibit reference
        Artifact artifact = artifacts.get(artifactId); // Gets artifact reference

		return exhibit.getArtifactSign(artifact); // Returns the artifacts sign for the specific exhibit
	}

    // DONE
	@Override
	public int getExhibitEngagementTime(String exhibitId) throws Exception {
		return exhibits.get(exhibitId).getEngagementTime();
	}
    // DONE
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

    // DONE
	@Override
	public void deleteExhibit(String exhibitId) throws Exception  {
        Exhibit exhibit = exhibits.get(exhibitId); // Finds exhibit using its ID

        for(AnnualPlan annualPlan: annualPlans.values()) { // Goes through all AnnualPlans to remove all references to exhibit
            annualPlan.removeExhibit(exhibit); // Calls removeExhibit in AnnualPlan to remove any references to the exhibit (if any)
        }
        exhibits.remove(exhibitId); // Removes exhibit from the main hashmap too
	}

    // TODO: REALLY DIFFICULT
	@Override
	public ArrayList<String> findExhibits(String searchCriteria, String sortBy) throws Exception {
		return null;
	}

    // DONE
	@Override
	public void addArtifactToExhibit(String artifactId, String exhibitId, String sign) throws Exception
	{
		Exhibit exhibit = exhibits.get(exhibitId); // Gets reference to exhibit
        Artifact artifact = artifacts.get(artifactId); // Gets reference to artifact
        exhibit.addArtifact(artifact, sign); // Adds artifact to exhibit with its sign
	}

    // DONE
	@Override
	public void removeArtifactFromExhibit(String artifactId, String exhibitId) throws Exception  {
        Artifact artifact = artifacts.get(artifactId); // Gets a reference to the artifact
        Exhibit exhibit = exhibits.get(exhibitId); // Gets a reference to the exhibit

        exhibit.removeArtifact(artifact);
	}

    //
	@Override
	public ArrayList<String> getExhibitArtifacts(String exhibitId) throws Exception
	{
        Exhibit exhibit = exhibits.get(exhibitId);
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
        String id = Integer.toString(annualPlans.size()); // TODO: BETTER ID GENERATION SYSTEM
        annualPlans.put(id, new AnnualPlan(year, id)); // TODO: Check if validation required
		return id;
	}

	@Override
	public String getAnnualPlan(int year) throws Exception
	{
        for (AnnualPlan annualPlan : annualPlans.values()) {
            if (annualPlan.getYear() == year) {
                return annualPlan.getId();
            }
        }
		throw new Exception("Annual plan at given year not found"); // If no annual plan's found, an exception is thrown
	}

	@Override
    public void addExhibitToAnnualPlan(String exhibitId, String planId, String hall, int month) throws Exception
	{
        Exhibit exhibit =  exhibits.get(exhibitId); // Gets reference to exhibit
        AnnualPlan annualPlan = annualPlans.get(planId); // Gets reference to annualPlan

	}

	@Override
	public ArrayList<String> getAnnualPlanExhibits(String planId, String hall, int month) throws Exception
	{
		return null;
	}

	@Override
	public void deleteAnnualPlan(String planId) throws Exception
	{
		annualPlans.remove(planId);
	}

	@Override
	public String getAnnualPlanInfo(String planId, String infoName) throws Exception
	{
		AnnualPlan annualPlan = annualPlans.get(planId); // Finds reference to the annual plan

        return switch (infoName) {
            case "year" ->  Integer.toString(annualPlan.getYear());
            case "total_exhibits" -> annualPlan.getTotalExhibits();
            default -> null;
        };
	}

	@Override
	public void updateAnnualPlanInfo(String planId, String infoName, String newValue) throws Exception
	{
		AnnualPlan annualPlan = annualPlans.get(planId); // Gets reference to annual plan

        switch (infoName) {
            case "year" ->  annualPlan.setYear(Integer.parseInt(newValue));
            // TODO: realistically more options will need to be added
        }
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
