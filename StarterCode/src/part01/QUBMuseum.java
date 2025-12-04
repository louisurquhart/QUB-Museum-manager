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

    // Main menu variables
    private MenuStates currentState = MenuStates.MAIN_MENU;

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
        } else {
            for (Artifact artifact : artifacts.values()) {
                artifactMatches = switch (searchType.toLowerCase()) { // Switch statement to accept every possible variable type requested
                    case "type" ->
                            artifact.getType().equals(searchValue);// Checks if the artifact type matches exactly with the given searchValue
                    case "name" ->
                            artifact.getName().equals(searchValue);// Checks if the artifact name matches exactly with the given searchValue
                    case "name_contains" ->
                            artifact.getName().contains(searchValue);// Checks if the artifact name contains the given searchValue
                    case "id" ->
                            artifact.getId().equals(searchValue);// Checks if the artifact ID matches exactly with the given searchValue
                    default -> artifactMatches;
                };
                if (artifactMatches) { //If the artifacts searchType matched the searchValue this iteration
                    matchingArtifacts.add(artifact);
                } // The artifacts ID was added to matchingArtifactIds
            }
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

		Exhibit exhibit = exhibits.get(exhibitId);
        if(exhibit == null) { throw new Exception("Exhibit with id " + exhibitId + " not found"); }

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
        // Generates ID for annual plan + increments counts
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
        // LEVEL 1 STATE:
        if(currentState == MenuStates.MAIN_MENU) {
            return """
                    MAIN MENU
                    Options:
                    1. Manage Artifacts
                    2. Manage Exhibits
                    3. Manage Annual Plans
                    4. Exit programme
                    """;
        }
        // LEVEL 2 STATES:
        else if (currentState == MenuStates.MANAGE_ARTIFACTS) {
            return """
                    MANAGE ARTIFACTS MENU
                    Options:
                    1. Add an artifact
                    2. View an artifact
                    3. Delete an artifact
                    4. Update an artifacts info
                    """;
        } else if(currentState == MenuStates.MANAGE_EXHIBITS) {
            return """
                    MANAGE EXHIBITS MENU
                    Options:
                    1. Add an exhibit
                    2. View an exhibit
                    3. Delete an exhibit
                    4. Update an exhibits info
                    5. Add artifact to an exhibit
                    6. Delete artifact from an exhibit
                    7. Reorder artifacts in an exhibit
                    """;
        } else if(currentState == MenuStates.MANAGE_ANNUAL_PLANS) {
            return """
                    MANAGE ANNUAL_PLANS MENU
                    Options:
                    1. Add an annual plan
                    2. View an annual plan
                    3. Delete an annual plan
                    4. Update an annual plan info
                    5. Add an exhibit to an annual plan
                    6. Delete an exhibit from an annual plan
                    """;
        }
        // -------- MANAGE ARTIFACT METHODS --------
        else if(currentState == MenuStates.ADD_ARTIFACT) {
            return """
                    Enter the new artifacts type, name, description, engagement minutes in format:
                    (type;name;description;engagementMinutes)
            """;
        } else if(currentState == MenuStates.VIEW_ARTIFACT) {
            return """
                    Enter the artifacts property type + value + how to sort the matching artifacts.
                    - searchType options: type, name, name_contains, id, null (blank)
                    - sortBy options: name, type, id, engagement, null (blank)
                    - null (blank) for searchType returns all artifacts; null for sortBy returns in ID order
                    - Format: (searchType=searchValue;sortBy) - ignoring brackets, no whitespace, semicolons separating each value
            """;
        } else if(currentState == MenuStates.DELETE_ARTIFACT) {
            return """
                    Enter the artifacts ID:
            """;
        } else if(currentState == MenuStates.UPDATE_ARTIFACT) {
            return """
                    Enter the artifacts ID + the information to updates name + the value to replace the info
                    - infoName options: name, type, description, engagement_minutes
                    - newValue restrictions (name/type/description - Continuous string, engagement_minutes - Integer)
                    - Format: (ID;infoName;newValue) - ignoring brackets, no whitespace, semicolons separating each value
                    """;
        }
        // -------- MANAGE EXHIBITS METHODS --------
        else if(currentState == MenuStates.ADD_EXHIBIT) {
            return """
                    Enter the new exhibits name and description in format:
                    (name;description)
            """;
        } else if(currentState == MenuStates.VIEW_EXHIBIT) {
            return """
                    Enter the exhibits property type + value + how to sort the matching exhibits.
                    - searchType options: name, name_contains, id, null (blank)
                    - sortBy options: name, id, null (blank)
                    - null (blank) for searchType returns all exhibits; null for sortBy returns in ID order
                    - Format: (searchType=searchValue;sortBy) - ignoring brackets, no whitespace, semicolons separating each value
            """;
        } else if (currentState == MenuStates.DELETE_EXHIBIT) {
            return """
                    Enter the exhibits ID:
            """;
        } else if (currentState == MenuStates.UPDATE_EXHIBIT) {
            return """
                    Enter the exhibits ID + the information to updates name + the value to replace the info
                    - infoName options: name, description
                    - newValue restrictions (name/type/description - Continuous string, engagement_minutes - Integer)
                    - Format: (ID;infoName;newValue) - ignoring brackets, no whitespace, semicolons separating each value
                    """;
        } else if(currentState == MenuStates.ADD_ARTIFACT_TO_EXHIBIT) {
            return """
                    Enter artifacts ID, the exhibits ID and the sign to go with the artifact
                    - Format: (artifactID;exhibitID,sign)
                    """;
        } else if(currentState == MenuStates.DELETE_ARTIFACT_IN_EXHIBIT) {
            return """
                    Enter artifacts ID + exhibits ID
                    - Format: (artifactID;exhibitID) - ignoring brackets, no whitespace, semicolons separating each value
            """;
        } else if(currentState == MenuStates.REORDER_ARTIFACTS_IN_EXHIBIT) {
            return """
                    Enter exhibitID + List of artifact ID's in a new order (ID's must all already exist in exhibit + not repeat)
                    Format: (exhibitID;artifactID1,artifactID2,...) - ignoring brackets, no whitespace, semicolons separating each value.
            """;
        }
        // -------- MODIFY ANNUAL PLAN METHODS --------
        else if(currentState == MenuStates.ADD_ANNUAL_PLAN) {
            return """
                    Enter new annual plans year:
                    """;
        } else if(currentState == MenuStates.VIEW_ANNUAL_PLAN) {
            return """
                    Enter annual plans year:
                    """;
        } else if(currentState == MenuStates.UPDATE_ANNUAL_PLAN) {
            return """
                    Enter the annual plans ID:
            """;
        } else if(currentState == MenuStates.DELETE_ANNUAL_PLAN) {
            return """
                    Enter the annual plans ID + the information to updates name + the value to replace the info
                    - infoName options: year, id
                    - newValue restrictions (year/id - Integers)
                    - Format: (ID;infoName;newValue) - ignoring brackets, no whitespace, semicolons separating each value
            """;
        } else if(currentState == MenuStates.ADD_EXHIBIT_TO_ANNUAL_PLAN) {
            return """
                    Enter exhibitID + annualPlanID:
            """;
        } else if(currentState == MenuStates.DELETE_EXHIBIT_IN_ANNUAL_PLAN) {
            return """
                    Enter exhibitID + annualPlanID:
            """;
        }
        return "EXIT";
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
        String output = "Unknown";

        switch(currentState) {
            // Level 1 - Main menu
            case MAIN_MENU:
                switch (input) {
                    case "1": // Manage artifacts option
                        output = "Entering artifact management menu";
                        currentState = MenuStates.MANAGE_ARTIFACTS;
                        break;
                    case "2": // Manage exhibits option
                        output = "Entering exhibit management menu";
                        currentState = MenuStates.MANAGE_EXHIBITS;
                        break;
                    case "3": // Manage annualPlans option
                        output = "Entering annual plan management menu";
                        currentState = MenuStates.MANAGE_ANNUAL_PLANS;
                        break;
                    case "4": // Exit programme option
                        return "EXIT"; // Returns exit which in the main class will exit the program if returned
                }
                break;
            // Level 2 - Manage options
            case MANAGE_ARTIFACTS:
                switch (input) {
                    case "1": // Add an artifact option
                        currentState = MenuStates.ADD_ARTIFACT;
                        output = "";
                        break;
                    case "2": // View an artifact option
                        currentState = MenuStates.VIEW_ARTIFACT;
                        output = "";
                        break;
                    case "3": // Delete an artifact option
                        currentState = MenuStates.DELETE_ARTIFACT;
                        output = "";
                        break;
                    case "4": // Update an artifact option
                        currentState = MenuStates.UPDATE_ARTIFACT;
                        output = "";
                        break;
                }
                break;
            case MANAGE_EXHIBITS:
                switch (input) {
                    case "1": // Add an exhibit option
                        currentState = MenuStates.ADD_EXHIBIT;
                        output = "";
                        break;
                    case "2": // view an exhibit option
                        currentState = MenuStates.VIEW_EXHIBIT;
                        output = "";
                        break;
                    case "3": // Delete an exhibit option
                        currentState = MenuStates.DELETE_EXHIBIT;
                        output = "";
                        break;
                    case "4": // Update an exhibit option
                        currentState = MenuStates.UPDATE_EXHIBIT;
                        output = "";
                        break;
                }
                break;
            case MANAGE_ANNUAL_PLANS:
                switch (input) {
                    case "1": // Add an annual plan option
                        currentState = MenuStates.ADD_ANNUAL_PLAN;
                        output = "";
                        break;
                    case "2": // View an annual plan option
                        currentState = MenuStates.VIEW_ANNUAL_PLAN;
                        output = "";
                        break;
                    case "3": // Delete an annual plan option
                        currentState = MenuStates.DELETE_ANNUAL_PLAN;
                        output = "";
                        break;
                    case "4": // Update an annual plan option
                        currentState = MenuStates.UPDATE_ANNUAL_PLAN;
                        output = "";
                        break;
                }
                break;
            case ADD_ARTIFACT:
                try {
                    String[] splitSearchCriteria = input.split(";", 4);  // Splits input breaking it down into seperate variables
                    if(splitSearchCriteria.length != 4) { // If there isn't 4 seperate varaibles after parsing, an error is returned
                        return "ERROR - Invalid format given; Invalid amount of parts. Format:(type;name;description;engagementMinutes)";
                    }
                    // Sets corresponding variables to broken up input parts
                    String type = splitSearchCriteria[0];
                    String name = splitSearchCriteria[1];
                    String description = splitSearchCriteria[2];
                    int engagementMinutes = Integer.parseInt(splitSearchCriteria[3]); // Could throw exception if not integer

                    // Creates the artifact with the broken down, parsed input (and records the ID of the new artifact)
                    String newArtifactsId = createArtifact(type, name, description, engagementMinutes);

                    // createArtifact will return null if it fails to create so an error will be output if so:
                    if(newArtifactsId == null) { return "ERROR - Failiure to create artifact; Potentially invalid input"; }

                } catch (NumberFormatException e) {
                    return "ERROR - Invalid engagementMinutes given; Needs to be an integer";
                } catch (Exception e) {
                    return "ERROR -" + e.getMessage(); // Returns exception as something bad has happened (something's wrong with code)
                }
        }
        // Level 3 - Sub management options
        return output;
    }

}
