package part01;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import api.QUBMuseumAPI;

public class QUBMuseum implements QUBMuseumAPI {

    HashMap<String, Artifact> artifacts = new HashMap<>();
    HashMap<String, Exhibit> exhibits = new HashMap<>();
    HashMap<String, AnnualPlan> annualPlans = new HashMap<>();

    // ID counts for each class.
    int artifactIdCount = 0;
    int exhibitIdCount = 0;
    int annualPlanIdCount = 0;

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
    // DONE
	@Override
	public String createArtifact(String type, String name, String description, int engagementMinutes) throws Exception {
        try {
            String id = Integer.toString(artifactIdCount); // Creates an incremental ID for the new artifact (+1 of previous artifacts ID)
            artifactIdCount++; // Increments ID count
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

    // DONE TODO: But likely full of logical errors haven't tested or properly looked at
	@Override
	public ArrayList<String> findArtifacts(String searchCriteria, String sortBy) throws Exception {
        String[] splitSearchCriteria = searchCriteria.split("=", 2); // Parses input breaking down search filter type : search value
        String searchType = splitSearchCriteria[0];
        String searchValue = splitSearchCriteria[1];

        ArrayList<Artifact> matchingArtifacts = new ArrayList<>(); // Creates an array list of the artifacts which match the search criteria ID's

        // SEARCHING ALGORITHM HERE (Options: 'type', 'name' (exact), 'name_contains' (just contains), 'id')
        boolean artifactMatches = false; // Flag set to true if match with searchType + searchValue found.

        if (searchType == null ) { // If the searchTypes null
            for(Artifact artifact : artifacts.values()) {
                matchingArtifacts.add(artifact); // All artifacts are added to the matchingArtifacts array (as null means all exhibits)
            }
        }

        for (Artifact artifact : artifacts.values()) {
            artifactMatches = switch (searchType.toLowerCase()) { // Switch statement to accept every possible variable type requested
                case "type" -> artifact.getType().equals(searchValue);// Checks if the artifact type matches exactly with the given searchValue
                case "name" -> artifact.getName().equals(searchValue);// Checks if the artifact name matches exactly with the given searchValue
                case "name_contains" -> artifact.getName().contains(searchValue);// Checks if the artifact name contains the given searchValue
                case "id" -> artifact.getId().equals(searchValue);// Checks if the artifact ID matches exactly with the given searchValue
                default -> artifactMatches;
            };
            if (artifactMatches) { //If the artifacts searchType matched the searchValue this iteration
                matchingArtifacts.add(artifact); } // The artifacts ID was added to matchingArtifactIds
        }
        boolean swapNeeded = false;
        // SORTING ALGORITHM HERE (Options: 'name', 'type' , 'id', 'engagement' - name and type are done alphabetically, id numerically and engagement accending
        //  NEED TO USE String.compareToIgnoreCase to compare lexicographical order (alphabetically kinda but works for all strings)

        if(sortBy == null) { sortBy = "id"; } // In line with docs,  if sortBy = null it will default to sorting by ID

        int swaps; // Counts how many swaps have occurred
        do {
            swaps = 0; // Sets swaps to 0 for a fresh loop
            for (int i = 0; i < matchingArtifacts.size() - 1; i++) {
                Artifact artifact1 = matchingArtifacts.get(i);
                Artifact artifact2 = matchingArtifacts.get(i + 1);
                switch (sortBy) {
                    case "name":  // If the result of compareToIgnoreCase is > 0 (positive), It means that a swap's required as artifact 1s name is deeper in the alphabet compared to artifact 2s name
                        if (artifact1.getName().compareToIgnoreCase(artifact2.getName()) > 0) { swapNeeded = true; }
                    case "type": // If the result of compareToIgnoreCase is > 0 (positive), It means that a swap's required as artifact 1s type is deeper in the alphabet compared to artifact 2s type
                        if (artifact1.getType().compareToIgnoreCase(artifact2.getType()) > 0) { swapNeeded = true; }
                    case "id": // If the converted to int id of artifact1 is > artifact2's, they're swapped as it means that artifact 1's ID is higher than the next value
                        if (Integer.parseInt(artifact1.getId()) > Integer.parseInt(artifact2.getId())) { swapNeeded = true; }
                    case "engagement":
                        if (artifact1.getEngagementMinutes() > artifact2.getEngagementMinutes()) { swapNeeded = true; }
                }
                if(swapNeeded) { // Switches the position of the two artifacts in the ArrayList
                    matchingArtifacts.set(i+1, artifact1);
                    matchingArtifacts.set(i, artifact2);
                    swaps++;
                }
            } // End of for loop
        } while (swaps > 0); // Continues until swaps are 0 meaning the artifacts are fully sorted as no changes were made in a full loop of the dataset.

        // Converts the matchingArtifacts array to the sortedArtifactIds array as the method return requests
        ArrayList<String> sortedArtifactIds = new ArrayList<>();
        for (Artifact artifact : matchingArtifacts) { sortedArtifactIds.add(artifact.getId()); }

        return sortedArtifactIds; // Then returns this sorted ArrayList back
    }
    
    // --- Exhibit Management ---
    // DONE
	@Override
	public String createExhibit(String name, String description) throws Exception  {
        try {
            String id = Integer.toString(exhibitIdCount);
            exhibitIdCount++; // Increments id count
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

    // TODO: Vaugely working -> no testing done, so definitely logical errors
	@Override
	public ArrayList<String> findExhibits(String searchCriteria, String sortBy) throws Exception {
        String[] splitSearchCriteria = searchCriteria.split("=", 2); // Parses input breaking down search filter type : search value
        String searchType = splitSearchCriteria[0];
        String searchValue = splitSearchCriteria[1];

        ArrayList<Exhibit> matchingExhibits = new ArrayList<>(); // Creates an array list of the exhibits which match the search criteria ID's

        // SEARCHING ALGORITHM HERE (Options: 'name' (exact), 'name_contains' (just contains), 'id')
        boolean exhibitMatches = false; // Flag set to true if match with searchType + searchValue found.
        if (searchType == null ) { // If the searchTypes null
            for(Exhibit exhibit : exhibits.values()) {
                matchingExhibits.add(exhibit); // All exhibits are added to the matchingExhibits array (as null == all exhibits according to docs)
            }
        }
        else {
            for (Exhibit exhibit : exhibits.values()) {
                exhibitMatches = switch (searchType.toLowerCase()) { // Switch statement to accept every possible variable type requested
                    case "name" ->  exhibit.getName().equals(searchValue); // Checks if the exhibit name matches exactly with the given searchValue
                    case "name_contains" -> exhibit.getName().contains(searchValue); // Checks if the exhibit name contains the given searchValue
                    case "id" ->  exhibit.getId().equals(searchValue); // Checks if the exhibit ID matches exactly with the given searchValue
                    default -> throw new Exception("Invalid searchCriteria given");
                };
                if (exhibitMatches) { //If the artifacts searchType matched the searchValue this iteration
                    matchingExhibits.add(exhibit);
                } // The artifacts ID was added to matchingArtifactIds
            }
        }
        // SORTING ALGORITHM HERE (Options: 'name', 'id', null - name's done alphabetically and id numerically, null is default done by ID
        //  NEED TO USE String.compareToIgnoreCase to compare lexicographical order (alphabetically essentially)
        if(sortBy == null) { sortBy = "id"; } // In line with docs,  if sortBy = null it will default to sorting by ID

        boolean swapNeeded = false;
        int swaps; // Counts how many swaps have occurred
        do {
            swaps = 0; // Sets swaps to 0 for a fresh loop
            for (int i = 0; i < matchingExhibits.size() - 1; i++) {
                Exhibit exhibit1 = matchingExhibits.get(i);
                Exhibit exhibit2 = matchingExhibits.get(i + 1);
                switch (sortBy) {
                    case "name":  // If the result of compareToIgnoreCase is > 0 (positive), It means that a swap's required as exhibit 1s name is deeper in the alphabet compared to exhibit 2s name
                        if (exhibit1.getName().compareToIgnoreCase(exhibit2.getName()) > 0) { swapNeeded = true; }
                    case "id": // If the converted to int id of exhibit1 is > exhibit2's, they're swapped as it means that exhibit 1's ID is higher than the next value
                        if (Integer.parseInt(exhibit1.getId()) > Integer.parseInt(exhibit2.getId())) { swapNeeded = true; }
                    default: // TODO: throw exception
                }
                if(swapNeeded) { // Switches the position of the two exhibits in the ArrayList
                    matchingExhibits.set(i+1, exhibit1);
                    matchingExhibits.set(i, exhibit2);
                    swaps++;
                }
            } // End of for loop
        } while (swaps > 0); // Continues until swaps are 0 meaning the artifacts are fully sorted as no changes were made in a full loop of the dataset.

        // Converts the matchingExhibit array to the sortedExhibitIds array as the method return requests
        ArrayList<String> sortedExhibitIds = new ArrayList<>();
        for (Exhibit exhibit  : matchingExhibits) { sortedExhibitIds.add(exhibit.getId()); }

        return sortedExhibitIds; // Then returns this sorted ArrayList back
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

    // DONE
	@Override
	public ArrayList<String> getExhibitArtifacts(String exhibitId) throws Exception
	{
        Exhibit exhibit = exhibits.get(exhibitId);
        ArrayList<String> exhibitIds = new ArrayList<>();

        // Converts the array of artifacts -> array of artifact IDs:
        for(Artifact artifact : exhibit.getArtifacts()) {
            exhibitIds.add(exhibit.getId());
        }
        return exhibitIds; // Returns the converted arrayList of artifact ID's
	}

    // DONE
	@Override
	public void reorderExhibitArtifacts(String exhibitId, ArrayList<String> artifactIds) throws Exception
	{
		Exhibit exhibit =  exhibits.get(exhibitId);

        // VALIDATION OF GIVEN ARTIFACT IDS:

        // Creation of a checklist
        ArrayList<String> checklistOfExhibitIds = new ArrayList<>(); // To validate all artifactId's given exist (in the exhibit) + aren't duplicated
        for(Artifact artifact : exhibit.getArtifacts() ) { // Adds all artifactIds in the exhibit to the checklist
            checklistOfExhibitIds.add(exhibit.getId());
        }
        for(String artifactId : artifactIds) { // Goes through all the given artifactIds
            if(!checklistOfExhibitIds.contains(artifactId)) { // If the given artifactID doesn't exist in the list of preexisting exhibits artifact ID's
                throw new Exception("Given artifactIds list is invalid, relevant artifact either doesn't exist in exhibit or is duplicated"); // an exceptions thrown
            } else {
                checklistOfExhibitIds.remove(artifactId); // If it exists its then removed from the checklist so duplicates will flag as not existing
            }
        }

        // REORDERING OF VALIDATED ID'S
        // Takes copy of all exhibits artifact signs
        HashMap<Artifact, String> exhibitSignsCopy =  new HashMap<>();
        for(Artifact artifact : exhibit.getArtifacts()) {
            exhibitSignsCopy.put(artifact, exhibit.getArtifactSign(artifact));
        }
        // Then, it goes through the list of given artifactIds in order, adding each artifact paired with its saved sign in order
        for (String artifactId : artifactIds) {
            Artifact artifact = artifacts.get(artifactId); // Finds artifact using its ID from artifactIds
            exhibit.addArtifact(artifact, exhibitSignsCopy.get(artifact)); // Adds artifact + its sign to the exhibit
        }
	}

    // --- Annual Plan Management ---

    // DONE
	@Override
	public String createAnnualPlan(int year) throws Exception
	{
        String id = Integer.toString(annualPlanIdCount);
        annualPlanIdCount++;
        annualPlans.put(id, new AnnualPlan(year, id)); // TODO: Check if validation required
		return id;
	}

    // DONE
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

    // DONE
	@Override
    public void addExhibitToAnnualPlan(String exhibitId, String planId, String hall, int month) throws Exception
	{
        Exhibit exhibit =  exhibits.get(exhibitId); // Gets reference to exhibit
        AnnualPlan annualPlan = annualPlans.get(planId); // Gets reference to annualPlan
        annualPlan.addExhibit(exhibit, hall, month); // Adds exhibit to annualPlan with its hall + month
	}

    // DONE
	@Override
	public ArrayList<String> getAnnualPlanExhibits(String planId, String hall, int month) throws Exception
	{
        ArrayList<String> matchingExhibitIds = new ArrayList<>();
        AnnualPlan annualPlan = annualPlans.get(planId);

        for(Exhibit exhibit : annualPlan.getExhibits()) { // Goes through all exhibits -> checks if exhibits hall + month match input;
            if(annualPlan.getExhibitMonth(exhibit) == month && annualPlan.getExhibitHall(exhibit).equals(hall) ){
                matchingExhibitIds.add(exhibit.getId());  // If they match, the exhibits ID's added to matchingExhibitIds
            }
        }
		return matchingExhibitIds; // Returns the ArrayList of all exhibits IDs which match the given hall + month
	}

    // DONE
	@Override
	public void deleteAnnualPlan(String planId) throws Exception
	{
		annualPlans.remove(planId);
	}

    // DONE
	@Override
	public String getAnnualPlanInfo(String planId, String infoName) throws Exception
	{
        AnnualPlan annualPlan = annualPlans.get(planId); // Gets reference to annualPlan using its given ID

        if (annualPlan == null) { return null; } // In line with docs, null is returned if annualPlan isn't found

        return switch (infoName) {
            case "year" ->  Integer.toString(annualPlan.getYear());
            case "total_exhibits" -> Integer.toString(annualPlan.getExhibits().size());
            default -> throw new Exception("Invalid infoName given");
        };
	}

    // DONE
	@Override
	public void updateAnnualPlanInfo(String planId, String infoName, String newValue) throws Exception
	{
		AnnualPlan annualPlan = annualPlans.get(planId); // Gets reference to annual plan

        switch (infoName) {
            case "year" ->  {
                for(AnnualPlan plan : annualPlans.values()) { // Goes through all annualPlans to validate non have the given year
                    if(plan.getYear() == Integer.parseInt(newValue)) { // If the year of the annualPlan's the same as the given value  bn
                        throw new Exception("Invalid infoName given; Year already exists for an annual plan");
                    }
                }
                annualPlan.setYear(Integer.parseInt(newValue));
            }
            case "id" -> {
                if (annualPlans.get(newValue) == null) {
                    annualPlans.remove(planId); // Removes annualPlan from hashmap under old ID
                    annualPlans.put(newValue, annualPlan); // Adds annualPlan to hashmap with new given ID
                    annualPlan.setId(newValue); // Updates the annualPlans stored ID
                } else {
                    throw new Exception("Invalid newValue for ID given (already exists");
                }
            }
            default -> throw new Exception("Invalid infoName given");
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
