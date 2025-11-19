package api;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Museum Management System API Interface
 * 
 * This interface defines the contract for a museum management system that handles
 * artifacts, exhibits, and annual planning through a console-based user interface.
 * The only change you make to this file should be to uncomment some of the advanced functionality if you attempt them. 
 */
public interface QUBMuseumAPI {
    
    // --- Artifact Management ---

    /**
     * Creates a new artifact and adds it to the museum collection.
     * 
     * @param type The artifact type. Valid types include common categories like:
     *             "PAINTING", "SCULPTURE", "POTTERY", "JEWELRY", "TEXTILE", "WEAPON", 
     *             "TOOL", "FOSSIL", "COIN", "PHOTOGRAPH", "DOCUMENT", "OTHER"
     * @param name The display name of the artifact (must be non-empty)
     * @param description Descriptive text about the artifact (must be non-empty)
     * @param engagementMinutes Expected dwell time spent by a visitor interacting with the artifact (must be > 0)
     * @return The unique ID of the created artifact as a string, or null if creation failed
     */
    public String createArtifact(String type, String name, String description, int engagementMinutes) throws Exception;

    /**
     * Retrieves information about a specific artifact.
     * 
     * @param artifactId The unique ID of the artifact
     * @param infoName The information field to retrieve. Valid values:
     *                 "name" - returns the artifact's display name
     *                 "type" - returns the artifact's type
     *                 "description" - returns the artifact's description text
     *                 "engagement_minutes" - returns the expected engagement time as a string
     * @return The requested information as a string, or null if artifact not found or invalid infoName
     */
    public String getArtifactInfo(String artifactId, String infoName) throws Exception;

    /**
     * Retrieves the engagement time for an artifact.
     * 
     * @param artifactId The unique ID of the artifact
     * @return Engagement time in minutes
     */
    public int getArtifactEngagementTime(String artifactId) throws Exception;

    /**
     * Updates information for an existing artifact.
     * 
     * @param artifactId The unique ID of the artifact to update
     * @param infoName The information field to update. Valid values:
     *                 "name" - updates the artifact's display name
     *                 "type" - updates the artifact's type (must be valid type)
     *                 "description" - updates the artifact's description text
     *                 "engagement_minutes" - updates the artifact's engagement time
     * @param newValue The new value for the specified field (must be non-empty)
     */
    public void updateArtifactInfo(String artifactId, String infoName, String newValue) throws Exception;

    /**
     * Removes an artifact from the museum collection.
     * 
     * @param artifactId The unique ID of the artifact to delete
     */
    public void deleteArtifact(String artifactId) throws Exception;

    /**
     * Searches for artifacts based on criteria and returns them in sorted order.
     * 
     * @param searchCriteria Search filter. Valid formats:
     *                       "type=TYPE_NAME" - finds artifacts of specific type
     *                       "name=TEXT" - finds artifacts whose names exactly match TEXT
     *                       "name_contains=TEXT" - finds artifacts whose names contain the text
     *                       "id=ARTIFACT_ID" - finds the artifact with the provided id
     *                       "" or null - returns all artifacts
     * @param sortBy Sort order. Valid values:
     *               "name" - sorts by artifact name alphabetically
     *               "type" - sorts by artifact type alphabetically  
     *               "id" - sorts by artifact ID numerically
     *               "engagement" - sorts by engagement time ascending
     *               "" or null - default order (typically by ID)
     * @return List of artifact IDs matching the criteria in the requested sort order
     */
    public ArrayList<String> findArtifacts(String searchCriteria, String sortBy) throws Exception;

    // --- Exhibit Management ---

    /**
     * Creates a new exhibit that can contain multiple artifacts.
     * 
     * @param name The display name of the exhibit (must be non-empty)
     * @param description Descriptive text about the exhibit theme or content (must be non-empty)
     * @return The unique ID of the created exhibit as a string, or null if creation failed
     */
    public String createExhibit(String name, String description) throws Exception;

    /**
     * Retrieves information about a specific exhibit.
     * 
     * @param exhibitId The unique ID of the exhibit
     * @param infoName The information field to retrieve. Valid values:
     *                 "name" - returns the exhibit's display name
     *                 "description" - returns the exhibit's description text
     * @return The requested information as a string, or null if exhibit not found or invalid infoName
     */
    public String getExhibitInfo(String exhibitId, String infoName) throws Exception;

    /**
     * Updates information for an existing exhibit.
     * 
     * @param exhibitId The unique ID of the exhibit to update
     * @param infoName The information field to update. Valid values:
     *                 "name" - updates the exhibit's display name
     *                 "description" - updates the exhibit's description text
     * @param newValue The new value for the specified field (must be non-empty)
     */
    public void updateExhibitInfo(String exhibitId, String infoName, String newValue) throws Exception;

    /**
     * Removes an exhibit from the museum. 
     * 
     * @param exhibitId The unique ID of the exhibit to delete
     */
    public void deleteExhibit(String exhibitId) throws Exception;

    /**
     * Searches for exhibits based on criteria and returns them in sorted order.
     * 
     * @param searchCriteria Search filter. Valid formats:
     *                       "name=TEXT" - finds exhibits whose names match TEXT
     *                       "name_contains=TEXT" - finds exhibits whose names contain the text
     *                       "id=EXHIBIT_ID" - finds the exhibit with the provided id
     *                       "" or null - returns all exhibits
     * @param sortBy Sort order. Valid values:
     *               "name" - sorts by exhibit name alphabetically
     *               "id" - sorts by exhibit ID numerically
     *               "" or null - default order (typically by ID)
     * @return List of exhibit IDs matching the criteria in the requested sort order
     */
    public ArrayList<String> findExhibits(String searchCriteria, String sortBy) throws Exception;

    /**
     * Adds an artifact to an exhibit with optional signage information.
     * 
     * @param artifactId The unique ID of the artifact to add
     * @param exhibitId The unique ID of the exhibit to add the artifact to
     * @param sign Additional signage or context information for this artifact in this exhibit
     */
    public void addArtifactToExhibit(String artifactId, String exhibitId, String sign) throws Exception;

    /**
     * Removes an artifact from an exhibit
     * 
     * @param artifactId The unique ID of the artifact to remove
     * @param exhibitId The unique ID of the exhibit to remove the artifact from
     */
    public void removeArtifactFromExhibit(String artifactId, String exhibitId) throws Exception;

    /**
     * Gets the list of artifacts currently in an exhibit.
     * 
     * @param exhibitId The unique ID of the exhibit
     * @return List of artifact IDs currently in the exhibit, or empty list if exhibit has no artifacts
     */
    public ArrayList<String> getExhibitArtifacts(String exhibitId) throws Exception;

    /**
     * Retrieves the descriptive sign text for a specific artifact within an exhibit route.
     * 
     * @param exhibitId The exhibit identifier
     * @param artifactId The artifact identifier inside the exhibit
     * @return The text displayed on the sign for this artifact in this exhibit, or null if not found
     */
    public String getExhibitArtifactSign(String exhibitId, String artifactId) throws Exception;

    /**
     * Calculates the total visitor engagement time for an exhibit based on its artifacts.
     * 
     * @param exhibitId The exhibit identifier
     * @return Total engagement time in minutes
     */
    public int getExhibitEngagementTime(String exhibitId) throws Exception;

    /**
     * Changes the display order of artifacts within an exhibit.
     * 
     * @param exhibitId The unique ID of the exhibit
     * @param artifactIds List of artifact IDs in the desired display order
     */
    public void reorderExhibitArtifacts(String exhibitId, ArrayList<String> artifactIds) throws Exception;

    // --- Annual Plan Management ---

    /**
     * Creates an annual exhibition plan for a specific year.
     * 
     * @param year The year for the annual plan (e.g., 2024)
     * @return A unique id for the annual plan
     */
    public String createAnnualPlan(int year) throws Exception;

    /**
     * Creates an annual exhibition plan for a specific year.
     * 
     * @param year The year for the annual plan (e.g., 2024)
     * @return The unique id for the annual plan
     */
    public String getAnnualPlan(int year) throws Exception;

    /**
     * Schedules an exhibit to be displayed in a specific hall during a specific month.
     * 
     * @param exhibitId The unique ID of the exhibit to schedule
     * @param planId The unique ID of the annual plan
     * @param hall The exhibition hall name ("HALL_A", "HALL_B", or "HALL_C")
     * @param month The month number (1-12, where 1=January, 12=December)
     */
    public void addExhibitToAnnualPlan(String exhibitId, String planId, String hall, int month) throws Exception;

    /**
     * Gets the exhibits scheduled for a specific hall and month.
     * 
     * @param planId The unique ID of the annual plan
     * @param hall The exhibition hall name ("HALL_A", "HALL_B", or "HALL_C")
     * @param month The month number (1-12)
     * @return List of exhibit IDs scheduled for the specified hall and month
     */
    public ArrayList<String> getAnnualPlanExhibits(String planId, String hall, int month) throws Exception;

    /**
     * Removes an entire annual plan, clearing all scheduled exhibits for that year.
     * 
     * @param planId The unique ID of the annual plan
     */
    public void deleteAnnualPlan(String planId) throws Exception;

    /**
     * Gets information about an annual plan.
     * 
     * @param planId The unique ID of the annual plan
     * @param infoName The information field to retrieve. Valid values:
     *                 "year" - returns the year as a string
     *                 "total_exhibits" - returns count of scheduled exhibits
     * @return The requested information as a string, or null if plan not found
     */
    public String getAnnualPlanInfo(String planId, String infoName) throws Exception;

    /**
     * Updates information about an annual plan.
     * 
     * @param planId The unique ID of the annual plan
     * @param infoName The information field to update (implementation-specific)
     * @param newValue The new value for the specified field
     */
    public void updateAnnualPlanInfo(String planId, String infoName, String newValue) throws Exception;

    // --- Console UI Testing Support ---

    /**
     * Gets the current state of the user interface for testing purposes.
     * This should return a unique identifier for the current menu or input state.
     * 
     * Examples: "MAIN_MENU", "ARTIFACT_CREATE_NAME", "EXIT"
     * 
     * @return A string identifying the current UI state
     */
    public String getCurrentState();

    /**
     * Gets the current prompt text being displayed to the user.
     * This helps automated testing understand what input is expected.
     * 
     * Examples: "Enter artifact name:", "Select option (1-4):", "Confirm deletion (y/n):"
     * 
     * @return The prompt text currently displayed, or empty string if no prompt
     */
    public String getCurrentPrompt();

    /**
     * Validates if the current input is valid for the current menu state.
     * 
     * @return If the input is valid
     */
    public boolean isValidInput(String input);

    /**
     * Gets a list of valid inputs for the current UI state.
     * This helps automated testing know what inputs to try for comprehensive testing.
     * 
     * @return List of valid inputs for current state, or null if not implemented
     */
    public ArrayList<String> getValidInputs();

    /**
     * Gets a list of invalid inputs for the current UI state.
     * This enables you to automate testing of the UI.
     * 
     * @return List of valid inputs for current state, or null if not implemented
     */
    public ArrayList<String> getInvalidInputs();

    /**
     * Processes user input and updates the UI state accordingly.
     * This is the main method for interacting with the console interface.
     * 
     * @param input The user's input string
     * @return Empty string if input was processed successfully, or error message if input was invalid
     */
    public String processInput(String input);
    
    // --- Part03 optional advanced functionality ---  
    // If you wish to attempt a feature make sure to uncomment the associated function, 
    // only include this functionality if it does not cause the code to crash, including crashing functionality will subtract from your mark
    // You are not expected to complete all of this functionality and it is not necessary to do so to get a good mark in the module
    // This functionality is designed to challenge you and requires you to teach yourself material beyond what is taught in the course
    
	/**
	 * Auto complete 
	 * @param partialInput a partial string representing part of the input to the menu
	 * @return An array list of possible valid inputs for the current situation
	 */
	//public ArrayList<String> autoComplete(String partialInput) throws Exception;

    /**
     * Loads museum data from a file, replacing current data.
     * 
     * @param filePath The path to the file containing saved museum data
     * @throws java.io.IOException If file cannot be read or data format is invalid
     */
    //public void loadData(String filePath) throws java.io.IOException;

    /**
     * Saves current museum data to a file for later loading.
     * 
     * @param filePath The path where museum data should be saved
     * @throws java.io.IOException If file cannot be written
     */
    //public void saveData(String filePath) throws java.io.IOException;
    
    /**
     * Undo the last action 
     */
    //public void undo() throws Exception;
    /**
     * Redo the last action if it was an undo 
     */
    //public void redo() throws Exception;

    /**
     * Create string that represents an html form which has contains the values of an artifact to be updated
     * @param artifactId The unique ID of the artifact, if null then it represents a new artifact
     * @return A String that stores a valid html file that represents an html form initialised with the values of the artifact
     */
    //public String createHTMLArtifactEditPage(String artifactId) throws Exception;
    /**
     * Create string that represents an html form which has contains the values of an exhibit to be updated
     * @param exhibitId The unique ID of the exhibit, if null then it represents a new exhibit
     * @return A String that stores a valid html file that represents an html form initialised with the values of the exhibit
     */
    //public String createHTMLExhibitEditPage(String exhibitId) throws Exception;
    /**
     * Create string that represents an html form which has contains the values of an annual plan to be updated
     * @param annualPlanId The unique ID of the annual plan, if null then it represents a new annual plan
     * @return A String that stores a valid html file that represents an html form initialised with the values of the annual plan
     */
    //public String createHTMLAnnualPlanEditPage(String annualPlanId) throws Exception;

    /**
     * Process an update to an Artifact from a form
     * @param artifactId The unique ID of the artifact, if null then it represents a new artifact
     * @param a hashmap which stores the fields of an artifact and the values to update or set
     * @return a hashmap which returns stores feedback to the user if any values are invalid 
     */
    //public HashMap<String, String> updateArtifact(String artifactId, HashMap<String, String> values) throws Exception;
    
    /**
     * Process an update to an Exhibit from a form
     * @param exhibitId The unique ID of the exhibit, if null then it represents a new artifact
     * @param a hashmap which stores the fields of an exhibit and the values to update or set
     * @return a hashmap which returns stores feedback to the user if any values are invalid 
     */
    //public HashMap<String, String> updateExhibit(String exhibitId, HashMap<String, String> values) throws Exception;
    
    /**
     * Process an update to an Annual Plan from a form
     * @param annualPlanId The unique ID of the annual plan, if null then it represents a new artifact
     * @param a hashmap which stores the fields of an annual plan and the values to update or set
     * @return a hashmap which returns stores feedback to the user if any values are invalid 
     */
    //public HashMap<String, String> updateAnnualPlan(String annualPlanId, HashMap<String, String> values) throws Exception;
    
    /**
     * Create an SVG file visualising a Gantt chart of the artifacts within an exhibit
     * @param exhibitId The unique ID of the exhibit
     * @return A String that stores a valid SVG file that visualises the exhibit artifact times as a Gantt chart
     */
    //public String createSVGGanttChart(String exhibitId) throws Exception;
}
