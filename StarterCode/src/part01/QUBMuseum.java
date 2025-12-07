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
        // TODO: ASK SOMEONE ABOUT THIS VALIDATION AS DOCUMENTATIONS SUPER UNCLEAR AND COULD BE REALLY BAD IF MISINTERPRETED DOCS
        // TYPE VALIDATION - According to documentation, valid types are these common types
//        String[] validTypes = { "PAINTING", "SCULPTURE", "POTTERY", "JEWELRY", "TEXTILE", "WEAPON", "TOOL", "FOSSIL", "COIN", "PHOTOGRAPH", "DOCUMENT", "OTHER" };
//        boolean typeIsValid = false;
//        for(String validType : validTypes) {
//            if(validType.equalsIgnoreCase(type.toLowerCase())) { // Checks if the given type is the current valid type
//                typeIsValid = true;
//                break;
//           }
//       }
//        if(!typeIsValid) { return null; }
        if(engagementMinutes <= 0 || type.isBlank() || name.isBlank() || description.isBlank()) { return null; }

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
        if(artifact == null) { return null; } // Returns null if artifact not found
        // Depending on infoName, it then returns the appropiate info (info names based off the API documentation)
        try {
            return switch (infoName.toLowerCase()) { // Puts it to lowercase to remove accidental capitalisation typos
                case "name" -> artifact.getName();
                case "type" -> artifact.getType();
                case "description" -> artifact.getDescription();
                case "engagement_minutes" -> Integer.toString(artifact.getEngagementMinutes());
                default -> null;
            };
        } catch(NumberFormatException e) {
            return null; // Returns null if infoName is invalid (not int for engagement minutes) inline with docs
        }
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

        // Validates new value isn't empty/whitespace
        if(newValue.isBlank()) { throw new Exception("newValue cannot be empty"); }

        // Validates if engagement_minutes the value isn't negative:
        if(infoName.equals("engagement_minutes") && Integer.parseInt(newValue) <= 0) {
            throw new Exception("engagement_minutes cannot be less than or equal to 0");
        }
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
                        if (artifact1.getName().compareToIgnoreCase(artifact2.getName()) > 0) { swapNeeded = true; } break;
                    case "type": // If the result of compareToIgnoreCase is > 0 (positive), It means that a swap's required as artifact 1s type is deeper in the alphabet compared to artifact 2s type
                        if (artifact1.getType().compareToIgnoreCase(artifact2.getType()) > 0) { swapNeeded = true; } break;
                    case "id": // If the converted to int id of artifact1 is > artifact2's, they're swapped as it means that artifact 1's ID is higher than the next value
                        if (Integer.parseInt(artifact1.getId()) > Integer.parseInt(artifact2.getId())) { swapNeeded = true; } break;
                    case "engagement":
                        if (artifact1.getEngagementMinutes() > artifact2.getEngagementMinutes()) { swapNeeded = true; } break;
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
        if(name.isBlank() || description.isBlank()) { // Valides name/description aren't null/whitespace
            return null; // If they are it returns null inline with documentation
        }
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
        if(newValue.isBlank()) { throw new Exception("New value cannot be empty"); } // Validates newValue isn't empty

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
                        if (exhibit1.getName().compareToIgnoreCase(exhibit2.getName()) > 0) { swapNeeded = true; } break;
                    case "id": // If the converted to int id of exhibit1 is > exhibit2's, they're swapped as it means that exhibit 1's ID is higher than the next value
                        if (Integer.parseInt(exhibit1.getId()) > Integer.parseInt(exhibit2.getId())) { swapNeeded = true; } break;
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
        // Clears the exhibits preexisting list of artifacts ready for it to be replaced with a reordered list.
        ArrayList<Artifact> blankArtifactArrayList = new ArrayList<>();
        exhibit.setArtifacts(blankArtifactArrayList);

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
        // Validates annual plan at year doesn't already exist
        for(AnnualPlan annualPlan: annualPlans.values()){
            if(annualPlan.getYear() == year) {
                throw new Exception("Annual plan with year " + year + " already exists");
            }
        }
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
	public String getAnnualPlanExhibit(String planId, String hall, int month) throws Exception
	{
        AnnualPlan annualPlan = annualPlans.get(planId);

        for(Exhibit exhibit : annualPlan.getExhibits()) { // Goes through all exhibits -> checks if exhibits hall + month match input;
            if (annualPlan.getExhibitMonth(exhibit) == month && annualPlan.getExhibitHall(exhibit).equals(hall)) {
                return exhibit.getId();// If they match, the exhibits IDs returned
            }
        }
        throw new Exception("Exhibit at given hall + month not found");
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
                    if(plan.getYear() == Integer.parseInt(newValue)) { // If the year of the annualPlan's the same as the given value
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
            4. Update an artifacts info5
            5. Go back
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
            8. Go back
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
            6. Go back
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
            Enter the annual plans ID, New infos name and the new value
            - infoName options: year, id
            - newValue restrictions: (year - integer, id - integer)
            - Format: (planId, infoName; newValue) - ignoring brackets, no whitespace, semicolons separating each 
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
            Enter exhibitID + annualPlanID + hall and month of exhibit:
            - Restrictions: month must be in integer format (1-12 = january - december)
            - Format: (exhibitID; annualPlanID, hall, month) - ignoring brackets, no whitespace, semicolons separating each
            """;
        }
        return "EXIT";
	}


	@Override
    public boolean isValidInput(String input) 
    {
//        switch(currentState) { // Switch statements to show valid inputs for each
//            case MAIN_MENU:
//                try {
//                    int inputInteger = Integer.parseInt(input);
//                    if (inputInteger <= 0 || inputInteger > 4) { // If input isn't 1-4 for a menu option
//                        return false; // False is returned as its invalid
//                    }
//                } catch(NumberFormatException e) {
//                    return false; // Inputs not a number
//                }
//                break;
//            case MANAGE_ARTIFACTS:
//                try {
//                    int inputInteger = Integer.parseInt(input);
//                    if (inputInteger <= 0 || inputInteger > 5) { // If input isn't 1-5 for a menu option
//                        return false; // False is returned as its invalid
//                    }
//                } catch(NumberFormatException e) {
//                    return false; // Inputs not a number
//                }
//                break;
//            case MANAGE_EXHIBITS:
//                try {
//                    int inputInteger = Integer.parseInt(input);
//                    if (inputInteger <= 0 || inputInteger > 8) { // If input isn't 1-8 for a menu option
//                        return false; // False is returned as its invalid
//                    }
//                } catch(NumberFormatException e) {
//                    return false; // Inputs not a number
//                }
//            case MANAGE_ANNUAL_PLANS:
//                try {
//                    int inputInteger = Integer.parseInt(input);
//                    if (inputInteger <= 0 || inputInteger > 6) { // If input isn't 1-8 for a menu option
//                        return false; // False is returned as its invalid
//                    }
//                } catch(NumberFormatException e) {
//                    return false; // Inputs not a number
//                }
//            case ADD_ARTIFACT:
//                String[] splitSearchCriteria = input.split(";", 4);  // Splits input breaking it down into seperate variables
//                if (splitSearchCriteria.length != 4) { // If there isn't 4 seperate varaibles after parsing its invalid
//                    return false;
//                } else if(splitSearchCriteria[0])
//                try { // Checks that the
//                    Integer.parseInt(splitSearchCriteria[3]);
//                }
//             }end of switch statement

        //return true; // If any of the statements above haven't found input invalid it's assumed to be valid

        return false;
    }
    
	@Override
	public ArrayList<String> getValidInputs()
    {
        // TODO: ASK WHAT THIS METHODS SUPPOSED TO DO
    	return null;
    }

	@Override
    public ArrayList<String> getInvalidInputs()
    {
    	return null;
        // TODO: ASK WHAT THIS METHODS SUPPOSED TO DO
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
            // Level 2 - Manage options TODO: ADD GOBACK OPTIONS
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
                    case "5":
                        currentState = MenuStates.MAIN_MENU;
                        output = "Returning to main menu";
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
                    case "5":
                        currentState = MenuStates.ADD_ARTIFACT_TO_EXHIBIT;
                        output = "";
                        break;
                    case "6":
                        currentState = MenuStates.DELETE_ARTIFACT_IN_EXHIBIT;
                        output = "";
                        break;
                    case "7":
                        currentState = MenuStates.REORDER_ARTIFACTS_IN_EXHIBIT;
                        output = "";
                        break;
                    case "8":
                        currentState = MenuStates.MAIN_MENU;
                        output = "Returning to main menu";
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
                    case "5":
                        currentState = MenuStates.ADD_EXHIBIT_TO_ANNUAL_PLAN;
                        output = "";
                        break;
                    case "6":
                        currentState = MenuStates.MAIN_MENU;
                        output = "Returning to main menu";
                        break;
                }
                break;
            case ADD_ARTIFACT:
                try {
                    String[] splitSearchCriteria = input.split(";", 4);  // Splits input breaking it down into seperate variables
                    if (splitSearchCriteria.length != 4) { // If there isn't 4 seperate varaibles after parsing, an error is returned
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
                    if (newArtifactsId == null) {
                        return "ERROR - Failiure to create artifact; Potentially invalid input";
                    }

                } catch (NumberFormatException e) {
                    return "ERROR - Invalid engagementMinutes given; Needs to be an integer";
                } catch (Exception e) {
                    return "ERROR -" + e.getMessage(); // Returns exception as something bad has happened (something's wrong with code)
                }
                output = "Artifact added successfully"; // If no errors caused early return, its assumed artifact creation was success.
                currentState = MenuStates.MANAGE_ARTIFACTS;
                break;
            case VIEW_ARTIFACT:
                try {
                    // Splits input into separate variables:
                    String[] splitSearchCriteria = input.split(";", 2);
                    String searchCriteria = splitSearchCriteria[0];
                    String sortBy = splitSearchCriteria[1];

                    // Calls find artifact
                    ArrayList<String> foundArtifactIds = findArtifacts(searchCriteria, sortBy);

                    output = ""; // Clears output from before so concatination works properly
                    if(foundArtifactIds.size() == 0) { // If no artifact IDs are returned
                        return "No artifacts found under given search criteria";
                    }
                    for (String artifactId : foundArtifactIds ) { // Combines all artifacts toString results in a loop
                        output = output + (artifacts.get(artifactId).toString() + "\n");
                    }
                } catch(Exception e) {
                    return "ERROR - " +  e.getMessage(); // Unknown exception thrown, returned to user; probably to do with code
                }
                currentState = MenuStates.MANAGE_ARTIFACTS;
                break;
            case DELETE_ARTIFACT: // Artifact ID is input for this
                try {
                    Artifact artifact = artifacts.get(input); // Gets artifact using its ID
                    if (artifact != null) { // If artifact exists in the hashmap (ID is valid)
                        deleteArtifact(input); // Delete artifacts called
                    } else {
                        return "ERROR - Artifact ID is not found, ID given likely invalid.";
                    }
                } catch(Exception e) {
                    return "ERROR - " +  e.getMessage(); // probs something wrong with code
                }
                output =  "Artifact deleted successfully";
                currentState = MenuStates.MANAGE_ARTIFACTS;
                break;
            case UPDATE_ARTIFACT:
               try {
                   // Splits input up into separate variables
                   String[] splitSearchCriteria = input.split(";", 3);
                   String artifactId = splitSearchCriteria[0];
                   String infoName = splitSearchCriteria[1].toLowerCase();
                   String newValue =  splitSearchCriteria[2].toLowerCase();

                   // Attempts to validate user input. All other invalid inputs will be caught by exception
                   if(artifacts.get(artifactId) == null) { // Validates artifact exists. If if statements true it doesn't
                       return "ERROR - Artifact ID is not found, ID given likely invalid.";
                   }
                   else if(!(infoName.equals("name") || infoName.equals("type") || infoName.equals("description") || infoName.equals("engagementMinutes"))) {
                       return "ERROR - Given infoName is invalid, must be either name, type, description or engagementMinutes";
                   }
                   else if(newValue == null) {
                       return "ERROR - newValue is null";
                   }
                   // If basic validation passes, updateArtifactInfo is called with variables inputted
                   updateArtifactInfo(artifactId, infoName, newValue);
               } catch (Exception e) { // TODO: COULD MAYBE CHECK IF INVALID INTEGER EXCEPTION IF ENGAGEMENTMINUTES ISNT INT
                   return "ERROR - " +  e.getMessage(); // Could be due to code OR invalid user input
               }
               output = "Artifact updated successfully";
                currentState = MenuStates.MANAGE_ARTIFACTS;
               break;
            // EXHIBIT STUFF
            case ADD_EXHIBIT:
                try {
                    String[] splitSearchCriteria = input.split(";", 2);  // Splits input into separate variables
                    if (splitSearchCriteria.length != 2) { // If there isn't 2 seperate varaibles after parsing, an error is returned
                        return "ERROR - Invalid format given; Invalid amount of parts. Format:(name; description)";
                    }
                    // Sets corresponding variables to broken up input parts
                    String name = splitSearchCriteria[0];
                    String description = splitSearchCriteria[1];

                    // Validates the name/descriptions not null
                    if (name == null || description == null) {
                        return "ERROR - Given name or description is null.";
                    }

                    // Creates the artifact with the broken down, parsed input (and records the ID of the new artifact)
                    String newExhibitsId = createExhibit(name, description);

                    // createExhibit will return null if it fails to create so an error will be output if so:
                    if (newExhibitsId == null) {
                        return "ERROR - Failure to create exhibit; Potentially invalid input";
                    }
                } catch (Exception e) {
                    return "ERROR -" + e.getMessage(); // Returns exception as something bad has happened (something's wrong with code)
                }
                output = "Exhibit added successfully"; // If no errors caused early return, its assumed exhibits creation was success.
                currentState = MenuStates.MANAGE_EXHIBITS;
                break;
            case VIEW_EXHIBIT:
                try {
                    // Splits input into separate variables:
                    String[] splitSearchCriteria = input.split(";", 2);
                    String searchCriteria = splitSearchCriteria[0];
                    String sortBy = splitSearchCriteria[1];

                    // Calls find exhibits (records id too to validate it exists)
                    ArrayList<String> foundExhibitIds = findExhibits(searchCriteria, sortBy);

                    if(foundExhibitIds.size() == 0) { // If no IDs are returned
                        return "No artifacts found under given search criteria";
                    }
                    for (String exhibitId : foundExhibitIds ) { // Combines all IDs toString() methods results using a loop
                        output = output + (exhibits.get(exhibitId).toString() + "\n");
                    }
                } catch(Exception e) {
                    return "ERROR - " +  e.getMessage(); // Unknown exception thrown, returned to user; probably to do with code
                }
                currentState = MenuStates.MANAGE_EXHIBITS;
                break;
            case DELETE_EXHIBIT:
                try {
                    Exhibit exhibit = exhibits.get(input); // Gets object using its ID
                    if (exhibit != null) { // If exhibit exists in the hashmap (ID is valid)
                        deleteExhibit(input); // The exhibits deleted
                    } else {
                        return "ERROR - Exhibit ID is not found, ID given likely invalid.";
                    }
                } catch(Exception e) {
                    return "ERROR - " +  e.getMessage(); // Probably something wrong with code
                }
                output =  "Artifact deleted successfully";
                currentState = MenuStates.MANAGE_EXHIBITS;
                break;
            case UPDATE_EXHIBIT:
                try {
                    // Splits input up into separate variables
                    String[] splitSearchCriteria = input.split(";", 3);
                    String exhibitId = splitSearchCriteria[0];
                    String infoName = splitSearchCriteria[1];
                    String newValue =  splitSearchCriteria[2];

                    // Attempts to validate user input. All other invalid inputs will be caught by exception
                    if(exhibits.get(exhibitId) == null) { // Validates object exists. (If the statements true it doesn't)
                        return "ERROR - Exhibit ID is not found, ID given likely invalid.";
                    }
                    else if(!(infoName.equals("name") || infoName.equals("description"))) {
                        return "ERROR - Given infoName is invalid, must be either name or description";
                    }
                    else if(newValue == null) {
                        return "ERROR - newValue is null";
                    }
                    // If basic validation passes, updateExhibitInfo is called with variables inputted
                    updateExhibitInfo(exhibitId, infoName, newValue);
                } catch (Exception e) { // Catches all other errors
                    return "ERROR - " +  e.getMessage(); // Could be due to code OR invalid user input
                }
                output =  "Exhibit updated successfully";
                currentState = MenuStates.MANAGE_EXHIBITS;
                break;
            case ADD_ARTIFACT_TO_EXHIBIT:
                try {
                    // Splits input values into seperate variables
                    String[] splitSearchCriteria = input.split(";", 3);
                    String artifactId = splitSearchCriteria[0];
                    String exhibitId = splitSearchCriteria[1];
                    String sign = splitSearchCriteria[2];

                    if(artifacts.get(artifactId) == null || exhibits.get(exhibitId) == null) { // If exhibit/artifact ID doesn't exist
                        return "ERROR - Artifact or Exhibit ID not found.";
                    }
                    addArtifactToExhibit(artifactId, exhibitId, sign); // If ID's exist it's attempted to add artifact to the exhibit
                } catch(Exception e) {
                    return "ERROR - " +  e.getMessage();
                }
                output = "Artifact added to exhibit successfully";
                currentState = MenuStates.MANAGE_EXHIBITS;
                break;
            case DELETE_ARTIFACT_IN_EXHIBIT:
                try {
                    // Splits input values into separate variables
                    String[] splitSearchCriteria = input.split(";", 2);
                    String artifactId = splitSearchCriteria[0];
                    String exhibitId = splitSearchCriteria[1];

                    if(exhibits.get(exhibitId) == null ||  artifacts.get(artifactId) == null) {
                        return "ERROR - Artifact or Exhibit ID not found.";
                    }
                    removeArtifactFromExhibit(artifactId, exhibitId); // Removes artifact
                } catch(Exception e) {
                    return "ERROR - " +  e.getMessage();
                }
                currentState = MenuStates.MANAGE_EXHIBITS;
                break;
            case REORDER_ARTIFACTS_IN_EXHIBIT:
                try {
                    // Breaks up initial input into exhibit + artifact IDs
                    String[] splitSearchCriteria = input.split(";", 2);
                    String exhibitId = splitSearchCriteria[0];
                    String artifactIds = splitSearchCriteria[1];

                    // Further breaks up artifactId's into separate values
                    String[] splitArtifactIds = artifactIds.split(",", 0);

                    // Converts to arraylist for reorderExhibitArtifacts()
                    ArrayList<String> splitArtifactIdsList = new ArrayList<>();

                    for(String artifactId : splitArtifactIds) { splitArtifactIdsList.add(artifactId); }

                    reorderExhibitArtifacts(exhibitId, splitArtifactIdsList);
                } catch(Exception e) {
                    return "ERROR - " +  e.getMessage();
                }
                currentState = MenuStates.MANAGE_EXHIBITS;
                break;
            case ADD_ANNUAL_PLAN:
                try {
                    createAnnualPlan(Integer.parseInt(input)); // Tries to create annual plan
                } catch(Exception e) { // If it fails exceptions thrown
                    return "ERROR - " +  e.getMessage(); // Likely to do with invalid year but could also be code
                }
                output = "Annual plan added successfully";
                currentState = MenuStates.MANAGE_ANNUAL_PLANS;
                break;
            case VIEW_ANNUAL_PLAN:
                try {
                    String planId = getAnnualPlan(Integer.parseInt(input));
                    output = annualPlans.get(planId).toString();
                } catch (Exception e) {
                    return "ERROR - " +  e.getMessage(); // Likely that no annual plan was found
                }
                currentState = MenuStates.MANAGE_ANNUAL_PLANS;
                break;
            case DELETE_ANNUAL_PLAN:
                try {
                    deleteAnnualPlan(input);
                } catch(Exception e) {
                    return "ERROR - " +  e.getMessage();
                }
                output = "Annual plan deleted successfully";
                currentState = MenuStates.MANAGE_ANNUAL_PLANS;
                break;
            case UPDATE_ANNUAL_PLAN:
                try {
                    // Splits up input into separate variables
                    String[] splitSearchCriteria = input.split(";", 3);
                    String planId = splitSearchCriteria[0];
                    String infoName = splitSearchCriteria[1];
                    String newValue =  splitSearchCriteria[2];
                    updateAnnualPlanInfo(planId, infoName, newValue);
                } catch (Exception e) {
                    return "ERROR - " +  e.getMessage();
                }
                output =  "Annual plan updated successfully";
                currentState = MenuStates.MANAGE_ANNUAL_PLANS;
                break;
            case ADD_EXHIBIT_TO_ANNUAL_PLAN:
                try {
                    // Splits up input into separate variables
                    String[] splitSearchCriteria = input.split(";", 4);
                    String exhibitId = splitSearchCriteria[0];
                    String annualPlanId = splitSearchCriteria[1];
                    String hall = splitSearchCriteria[2];
                    String month = splitSearchCriteria[3];

                    addExhibitToAnnualPlan(exhibitId, annualPlanId, hall, Integer.parseInt(month));
                } catch(NumberFormatException e) {
                    return "ERROR - Invalid month format. Must be an integer (1-12)";
                }  catch (Exception e) {
                    return "ERROR - " +  e.getMessage();
                }
                output = "Exhibit added to annualPlan successfully";
                currentState = MenuStates.MANAGE_ANNUAL_PLANS;
                break;
        }
        return output;
    }

}
