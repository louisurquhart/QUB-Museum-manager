package part02;

import api.TestingQUBMuseumAPI;
import part01.QUBMuseum;

public class TestingQUBMuseum implements TestingQUBMuseumAPI {
	
	//Tests can replace this instance with a new one if you want a clean start
    QUBMuseum api = new QUBMuseum();

    public static void main(String[] args) {
        
    	TestingQUBMuseum tester = new TestingQUBMuseum();
    	
        // UNIT TESTS
        
        // Run comprehensive test suite covering all API methods and edge cases, including error usage
    	tester.runArtifactCRUDTests();
    	tester.runExhibitCRUDTests();
    	tester.runAnnualPlanCRUDTests();

        //Add additional tests unit tests to cover any advanced functionality
        
        // END-TO-END TESTS

    	// Simulate user inputs to test plausible end to end tests that a user might perform
    	// Use to validate both correct and incorrect actions being handled correctly by the system
    	// Validate both the menu responses and the internal state of the system using the api
        tester.runMenuTests();
        
        // FUZZING TESTS
        // Randomly simulate user inputs with and without errors to validate that the user interface
        // is working correctly
        tester.runFuzzingTests();
    }
// example safe test artifacts
//    String id2 = api.createArtifact("Sculpture", "Acropolis Statues", "Cool statues", 5);
//    String id3 = api.createArtifact("Digital", "Incas Interactive", "Digital artifact to learn about incas", 10);
//    String id4 = api.createArtifact("Tactile", "TouchIt", "Weird electric thingy which you touch (i assume)", 8);
    
	@Override
    public void runArtifactCRUDTests()  {
        int testPasses = 0;
        int testFailures = 0;

        // --- CREATE ARTIFACT TESTS + READ ARTIFACT INFO TESTING (BY PROXY) ---
        // --- SAFE CASES - All valid case testing (does 2 of example artifacts) ---

        // Safe case A:
        if(validArtifactCreationTester("Painting", "Delaware Landscape","Landscape of delaware in the US", 1)) {
            testPasses++;
        } else { testFailures++; }
        // Safe case B:
        if(validArtifactCreationTester("Sculpture", "Acropolis Statues", "Cool statues", 5)) {
            testPasses++;
        } else { testFailures++; }

        // --- INVALID CASES - Invalid cases to test if validation for methods catch them with null return/exception
        // Invalid case A:
        if(invalidArtifactCreationTester("Painting", "Delaware Landscape","Landscape of delaware in the US", -10)) {
            testPasses++;
        } else { testFailures++; }
        // Invalid case B:
        if(invalidArtifactCreationTester("", "Delaware Landscape", "Landscape of delaware in the US", 1)) {
            testPasses++;
        } else { testFailures++; }
        // Invalid case C
        if(invalidArtifactCreationTester("Painting", "", "Landscape of delaware in the US", 1)) {
            testPasses++;
        } else { testFailures++; }
        // Invalid case D
        if(invalidArtifactCreationTester("Painting", "Delaware Landscape", "", 1)) {
            testPasses++;
        } else { testFailures++; }

        // TODO: FIND + SORT ARTIFACT TESTS MAYBE

        // --- UPDATE ARTIFACT TESTS ---

        // Creates a new artifact for testing:
        String testArtifactid = null;
        try {
             testArtifactid = api.createArtifact("Painting", "Delaware Landscape","Landscape of delaware in the US", 1);
        } catch(Exception e) {
            System.out.println("Setup for update artifacts failed." + e.getMessage());
        }

        if(testArtifactid != null) { // Only runs if setup was successful
            // - SAFE VALID CASES:
            if (validArtifactUpdaterTester(testArtifactid, "name", "Test Name")) {
                testPasses++;
            } else { testFailures++; }
            if (validArtifactUpdaterTester(testArtifactid, "type", "Test Type")) {
                testPasses++;
            } else { testFailures++; }
            if (validArtifactUpdaterTester(testArtifactid, "description", "Test Description")) {
                testPasses++;
            } else { testFailures++; }
            if (validArtifactUpdaterTester(testArtifactid, "engagement_minutes", "50")) {
                testPasses++;
            } else { testFailures++; }

            // - INVALID CASES:
            // Tests whitespace (newValue can't be empty)
            if (invalidArtifactUpdaterTester(testArtifactid, "name", "   ")) {
                testPasses++;
            } else { testFailures++; }
            if (invalidArtifactUpdaterTester(testArtifactid, "engagement_minutes", "  ")) {
                testPasses++;
            } else { testFailures++; }

            // Tests invalid infoname
            if (invalidArtifactUpdaterTester(testArtifactid, "Invalid name", "Test value")) {
                testPasses++;
            } else { testFailures++; }
            if(invalidArtifactUpdaterTester(testArtifactid, "   ", "Test value")) {
                testPasses++;
            } else { testFailures++; }

            // Tests invalid engagement_minutes infoname
            if (invalidArtifactUpdaterTester(testArtifactid, "engagement_minutes", "Invalid value")) { // Tests non integer
                testPasses++;
            } else { testFailures++; }
            if (invalidArtifactUpdaterTester(testArtifactid, "engagement_minutes", "0")) { // Tests invalid boundary of 0
                testPasses++;
            } else { testFailures++; }
            if (invalidArtifactUpdaterTester(testArtifactid, "engagement_minutes", "-10")) { // Tests negative
                testPasses++;
            } else { testFailures++; }
        }

        // -- DELETE ARTIFACT TESTS ---
        // Creates a new artifact for testing:
        try {
            testArtifactid = api.createArtifact("DeleteTestType", "TestName","", 1);
        } catch(Exception e) {
            System.out.println("Setup for update artifacts failed." + e.getMessage());
        }
        if(testArtifactid != null) {
            try {
                api.deleteArtifact(testArtifactid);
                if(api.getArtifactInfo(testArtifactid, "name") == null){ // Tries to get info about artifact.
                    System.out.println("PASS - Artifact deletion presumed success as it can no longer be found");
                    testPasses++;
                } else {
                    System.out.println("FAIL - Artifact deletion failed as values can still be pulled using its ID");
                    testFailures++;
                }
            } catch(Exception e) { // If an exceptions caused somehow
                System.out.println("Delete artifact failed for an unknown reason." + e.getMessage());
                testFailures++;
            }
        } else {
            System.out.println("Delete artifact test setup failed. Test artifact ID is null");
            testFailures++;
        }

    }
    // Tester for creating artifacts with valid parameters
    private boolean validArtifactCreationTester(String type, String name, String description, int engagementMinutes) {
        try {
            String testObjectId = api.createArtifact(type, name, description, engagementMinutes); // Creates artifact with inputted parameters

            // Verifies newly created object exists + variables all match using verfiyArtifactVariables()
            if(verifyArtifactVaraibles(testObjectId, type, name, description, engagementMinutes)) {
                System.out.println("PASS - Artifact creation success. All stored values match inputted");
                return true; // If pass true is returned
            } else { return false; } // if fails it returns false

        } catch (Exception e) {
            System.err.println("Artifact creation failure:" + e.getMessage());
            return false; // Code failed somehow. false returned
        }
    }

    // Tester for creating artifacts with invalid parameters
    private boolean invalidArtifactCreationTester(String type, String name, String description, int engagementMinutes) {
        try {
            String testObjectId = api.createArtifact(type, name, description, engagementMinutes); // Creates artifact with inputted parameters

            if(testObjectId != null) { // If object was actually created (given an ID and stuff, not null)
                System.out.println("FAIL - Artifact created when given invalid parameters");
                return false;
            } else {
                System.out.println("PASS - Object creation didn't complete when given invalid parameters");
                return true;
            }
        } catch (Exception e) {
            System.out.println("PARTIAL FAIL - Object creation threw exception instead of returning null when given invalid parameters");
            return false;
        }
    }
    // Tester for updating artifacts with valid parameters
    private boolean validArtifactUpdaterTester(String artifactId, String infoName, String newValue) {
        try {
            api.updateArtifactInfo(artifactId, infoName, newValue); // Attempts to update artifact info with given infoName + newValue
            if(api.getArtifactInfo(artifactId, infoName).equals(newValue)) { // Checks stored value to see if it has been updated
                System.out.println("PASS - UpdateArtifact successfully updated stored value for artifact correctly"); // If so pass is output
                return true; // Returns true to signify pass
            } else {
                System.out.println("FAIL - UpdateArtifact didn't update stored values");
                return false; // Returns false to signify failure
            }
        } catch(Exception e) { // If an exception occurs (which shouldn't happen for valid inputs)
            System.out.println("FAIL - UpdateArtifact threw exception for valid input values. Exception: " + e.getMessage()); // Test is failed
            return false; // Returns false to signify failure
        }
    }
    // Tester for updating artifacts with invalid parameters
    private boolean invalidArtifactUpdaterTester(String artifactId, String infoName, String newValue) {
        try {
            api.updateArtifactInfo(artifactId, infoName, newValue);
            // If no exceptions thrown by attempting to update artifact info with invalid stuff:
            System.out.println("FAIL - UpdateArtifact allowed invalid value: " + newValue); // Test has failed
            return false; // False is returned to signify

        } catch(Exception e) { // If an exception happens (which it's supposed to if input is invalid)
            System.out.println("PASS - UpdateArtifact threw exception (expected for invalid input)");
            return true; // True's is returned to signify pass
        }
    }
    // Tester for checking an artifacts variables match what has been given (and that artifact exists)
    private boolean verifyArtifactVaraibles(String testObjectId, String type, String name, String  description, int engagementMinutes) {
        // Tests all data in newly created artifact matches inputted:
        try {
            // Boolean variables to represent if type, name, description and engagement minutes match
            boolean typeMatch = api.getArtifactInfo(testObjectId, "type").equals(type);
            boolean nameMatch = api.getArtifactInfo(testObjectId, "name").equals(name);
            boolean descriptionMatch = api.getArtifactInfo(testObjectId, "description").equals(description);
            boolean engagementMinutesMatch = Integer.parseInt(api.getArtifactInfo(testObjectId, "engagement_minutes")) == engagementMinutes;

            // TESTS THE ARTIFACT WAS CREATED IN THE FIRST PLACE (id isn't null):
            if (testObjectId == null) { System.out.println("FAIL - Artifact creation failed. ID is null"); return false; } // If null test fails

            // TESTS IF DATA INSIDE CREATED ARTIFACT MATCHES:
            if (typeMatch && nameMatch && descriptionMatch && engagementMinutesMatch) { // If all data is correct
                return true;
            } else {
                System.out.println("FAIL - Not all artifact data stored matches inputted data.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("FAIL - Artifact creation threw an exception: " + e.getMessage());
            return false;
        }
    }


    // TODO: -------------------------- EXHIBIT CRUD TESTS HERE ----------------------------------
    // Exhibit examples:
    // String testObjectID = api.createExhibit("Global warming", "Warming of the planet due to greenhouse gasses");
    // String testObjectID = api.createExhibit("Brexit", "The leaving of the european union")

    @Override
    public void runExhibitCRUDTests()
    {
        int testPasses = 0;
        int testFailures = 0;

        // --- CREATE EXHIBIT TESTS + READ EXHIBIT INFO TESTING (BY PROXY) ---
        // --- SAFE CASES - All valid case testing (does 2 of example exhibits) ---

        // Safe case A:
        if(validExhibitCreationTester("Global warming", "Warming of the planet due to greenhouse gasses")) {
            testPasses++;
        } else { testFailures++; }
        // Safe case B:
        if(validExhibitCreationTester("Brexit", "The leaving of the european union")) {
            testPasses++;
        } else { testFailures++; }

        // --- INVALID CASES - Invalid cases to test if validation for methods catch them with null return/exception
        // Can only test for whitespace/no value
        // Invalid case A:
        if(invalidExhibitCreationTester(" ", "Warming of the planet due to greenhouse gasses")) {
            testPasses++;
        } else { testFailures++; }
        // Invalid case B:
        if(invalidExhibitCreationTester("Brexit", " ")) {
            testPasses++;
        } else { testFailures++; }

        // TODO: FIND EXHIBITS MAYBE (kinda already done by proxy with create/update though)

        // --- UPDATE EXHIBIT TESTS ---

        // Creates a new artifact for testing:
        String testExhibitid = null;
        try {
            testExhibitid = api.createExhibit("Global warming", "Warming of the planet due to greenhouse gasses");
        } catch(Exception e) {
            System.out.println("Setup for update exhibits failed (couldn't create exhibit to update)" + e.getMessage());
        }

        if(testExhibitid != null) { // Only runs if setup was successful
            // - SAFE VALID CASES:
            if (validExhibitUpdaterTester(testExhibitid, "name", "Test Name")) {
                testPasses++;
            } else { testFailures++; }
            if (validExhibitUpdaterTester(testExhibitid, "description", "Test Description")) {
                testPasses++;
            } else { testFailures++; }

            // - INVALID CASES:
            // Tests whitespace (newValue can't be empty)
            if (invalidExhibitUpdaterTester(testExhibitid, "name", "   ")) {
                testPasses++;
            } else { testFailures++; }
            if (invalidExhibitUpdaterTester(testExhibitid, "description", "  ")) {
                testPasses++;
            } else { testFailures++; }

            // Tests invalid infoname
            if (invalidExhibitUpdaterTester(testExhibitid, "Invalid name", "Test value")) {
                testPasses++;
            } else { testFailures++; }
            if(invalidExhibitUpdaterTester(testExhibitid, "   ", "Test value")) {
                testPasses++;
            } else { testFailures++; }
        }

        // -- DELETE EXHIBIT TESTS ---
        // Creates a new exhibit for testing:
        try {
            testExhibitid = api.createExhibit("DeleteTestName", "Test description");
        } catch(Exception e) {
            System.out.println("Setup for update exhibits failed (couldn't create exhibit)" + e.getMessage());
        }
        if(testExhibitid != null) {
            try {
                api.deleteExhibit(testExhibitid);
                if(api.getExhibitInfo(testExhibitid, "name") == null){ // Tries to get info about exhibit.
                    System.out.println("PASS - Exhibit deletion presumed success as it can no longer be found");
                    testPasses++;
                } else {
                    System.out.println("FAIL - Exhibit deletion failed as values can still be pulled using its ID");
                    testFailures++;
                }
            } catch(Exception e) { // If an exceptions caused somehow
                System.out.println("Delete exhibit failed for an unknown reason." + e.getMessage());
                testFailures++;
            }
        } else {
            System.out.println("Delete exhibit test setup failed. Test exhibit ID is null");
            testFailures++;
        }

    }

    // Tester for creating exhibits with valid parameters
    private boolean validExhibitCreationTester(String name, String description) {
        try {
            String testObjectId = api.createExhibit(name, description); // Creates exhibit with inputted parameters

            // Verifies newly created object exists + variables all match using verfiyExhibitVariables()
            if(verifyExhibitVaraibles(testObjectId, name, description)) {
                System.out.println("PASS - Exhibit creation success. All stored values match inputted");
                return true; // If pass true is returned
            } else { return false; } // if fails it returns false

        } catch (Exception e) {
            System.err.println("Exhibit creation failure:" + e.getMessage());
            return false; // Code failed somehow. false returned
        }
    }

    // Tester for creating exhibits with invalid parameters
    private boolean invalidExhibitCreationTester(String name, String description) {
        try {
            String testObjectId = api.createExhibit(name, description); // Creates exhibit with inputted parameters

            if(testObjectId != null) { // If object was actually created (given an ID and stuff, not null)
                System.out.println("FAIL - Exhibit created when given invalid parameters");
                return false;
            } else {
                System.out.println("PASS - Object creation didn't complete when given invalid parameters");
                return true;
            }
        } catch (Exception e) {
            System.out.println("PARTIAL FAIL - Object creation threw exception instead of returning null when given invalid parameters");
            return false;
        }
    }
    // Tester for updating exhibits with valid parameters
    private boolean validExhibitUpdaterTester(String exhibitId, String infoName, String newValue) {
        try {
            api.updateExhibitInfo(exhibitId, infoName, newValue); // Attempts to update exhibit info with given infoName + newValue
            if(api.getExhibitInfo(exhibitId, infoName).equals(newValue)) { // Checks stored value to see if it has been updated
                System.out.println("PASS - UpdateExhibit successfully updated stored value for exhibit correctly"); // If so pass is output
                return true; // Returns true to signify pass
            } else {
                System.out.println("FAIL - UpdateExhibit didn't update stored values");
                return false; // Returns false to signify failure
            }
        } catch(Exception e) { // If an exception occurs (which shouldn't happen for valid inputs)
            System.out.println("FAIL - UpdateExhibit threw exception for valid input values. Exception: " + e.getMessage()); // Test is failed
            return false; // Returns false to signify failure
        }
    }
    // Tester for updating exhibits with invalid parameters
    private boolean invalidExhibitUpdaterTester(String exhibitId, String infoName, String newValue) {
        try {
            api.updateExhibitInfo(exhibitId, infoName, newValue);
            // If no exceptions thrown by attempting to update exhibit info with invalid stuff:
            System.out.println("FAIL - UpdateExhibit allowed invalid value: " + newValue); // Test has failed
            return false; // False is returned to signify

        } catch(Exception e) { // If an exception happens (which it's supposed to if input is invalid)
            System.out.println("PASS - UpdateExhibit threw exception (expected for invalid input)");
            return true; // True's is returned to signify pass
        }
    }
    // Tester for checking an exhibits variables match what has been given (and that artifact exists)
    private boolean verifyExhibitVaraibles(String testObjectId, String name, String description) {
        // Tests all data in newly created exhibit matches inputted:
        try {
            // Boolean variables to represent if name and description match
            boolean nameMatch = api.getExhibitInfo(testObjectId, "name").equals(name);
            boolean descriptionMatch = api.getExhibitInfo(testObjectId, "description").equals(description);

            // TESTS THE EXHIBIT WAS CREATED IN THE FIRST PLACE (id isn't null):
            if (testObjectId == null) { System.out.println("FAIL - Exhibit creation failed. ID is null"); return false; } // If null test fails

            // TESTS IF DATA INSIDE CREATED EXHIBIT MATCHES:
            if (nameMatch && descriptionMatch) { // If all data is correct
                return true;
            } else {
                System.out.println("FAIL - Not all exhibit data stored matches inputted data.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("FAIL - Exhibit creation threw an exception: " + e.getMessage());
            return false;
        }
    }

	@Override
    public void runAnnualPlanCRUDTests() 
    {
    	
    }

    
	@Override
    public void runMenuTests() 
    {
		String prompt;
	    String result;
	    
	    int numberOfErrors = 0;
	    int numberOfTests = 0;
	    
	    //Update with the actual prompt you expect to have for a the main menu
	    final String MAIN_MENU_PROMPT = "";
	    
	    try {
		    prompt = api.getCurrentPrompt();
		    result = api.processInput("1");
		    if (prompt != null && MAIN_MENU_PROMPT.equals(prompt)) {
		      System.out.println("PASS: Found student correctly");
		    } else {
		      System.out.println("FAIL: Failed to describe menu");
		      numberOfErrors = numberOfErrors+1;
		    }
		    
		    //Test the result is what you expect
		    
		    numberOfTests = numberOfTests+1;
	    } catch (Exception e) {
	    	
	    }

	    //Expand this example to cover a realistic user interaction
	    
	    //Add more end to end tests, starting from a fresh instance
	    api = new QUBMuseum(); 
    }

    // Probably super inefficient and may crash:
	@Override
    public void runFuzzingTests() // Random user input tests
    {
        int exceptionsThrown = 0; // Records amount of exceptions thrown

    	for(int i = 0; i < 5000; i++)
        {
            // Generates a few random number inputs so it actually has a chance to go through menus
            int randomNumberInput1 = (int)(Math.random() * 9);
            int randomNumberInput2 = (int)(Math.random() * 9);
            int randomNumberInput3 = (int)(Math.random() * 9);

            // Cant make random strings using java.utils so can use random chars + combines
            int randomStringLength = (int)(Math.random() * 100);
            String randomlyGeneratedString = "";
            for(int j = 0; j < randomStringLength; j++) {
                int randomCharIndexValue = (int)(Math.random() * 255); // Goes through most of realistic char values (latin alpahbet according to google + special characters)
                char randomlyGeneratedChar = (char)(randomCharIndexValue); // Generates a random character to add to string
                randomlyGeneratedString = randomlyGeneratedString + randomlyGeneratedChar; // Combines existing string with random character
            }
            try {
                // Tries combination of random number inputs + random strings to effectively test programme
                api.processInput(String.valueOf(randomNumberInput1));
                api.processInput(randomlyGeneratedString);
                api.processInput(String.valueOf(randomNumberInput2));
                api.processInput(randomlyGeneratedString);
                api.processInput(String.valueOf(randomNumberInput3));
                api.processInput(randomlyGeneratedString);
            } catch (Exception e) {
                System.out.println("FAIL: Fuzzing failed - programme threw exception due to invalid input");
                exceptionsThrown++;
            }
        }

        // Checks if test passed
        if(exceptionsThrown == 0) {
            System.out.println("PASS: Fuzzing test passed, 0 exceptions thrown");
        } else {
            System.out.println("Fuzzing exceptions thrown: " + exceptionsThrown );
        }
    }
}
