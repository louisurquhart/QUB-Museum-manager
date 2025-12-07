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
// Example safe test artifacts
//    String id2 = api.createArtifact("Sculpture", "Acropolis Statues", "Cool statues", 5);
//    String id3 = api.createArtifact("Digital", "Incas Interactive", "Digital artifact to learn about incas", 10);
//    String id4 = api.createArtifact("Tactile", "TouchIt", "Weird electric thingy which you touch (i assume)", 8);


    // Check this isn't just for advanced JSON funcitonality saving stuff
	@Override
    public void runArtifactCRUDTests()  {
        // Records tests done and test passes
        int testPasses = 0;
        int testFailures = 0;

        System.out.println("Running Artifact CRUD tests...");
        // --- CREATE ARTIFACT TESTS + READ ARTIFACT INFO TESTING (BY PROXY) ---
        // --- SAFE CASES - All valid case testing (does 2 of example artifacts) ---

        System.out.println("--- ARTIFACT CREATION TESTS --- ");
        System.out.println("- Valid cases: ");
        // Safe case A:
        if(validArtifactCreationTester("Painting", "Delaware Landscape","Landscape of delaware in the US", 1)) {
            testPasses++;
        } else { testFailures++; }
        // Safe case B:
        if(validArtifactCreationTester("Sculpture", "Acropolis Statues", "Cool statues", 5)) {
            testPasses++;
        } else { testFailures++; }

        System.out.println(" - Invalid cases:  ");
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

        // TODO: MAYBE FIND + SORT ARTIFACT TESTS MAYBE

        // --- UPDATE ARTIFACT TESTS ---
        System.out.println("--- ARTIFACT UPDATION TESTS ---- ");
        // Creates a new artifact for testing:
        String testArtifactid = null;
        try {
             testArtifactid = api.createArtifact("Painting", "Delaware Landscape","Landscape of delaware in the US", 1);
        } catch(Exception e) {
            System.out.println("Setup for update artifacts failed." + e.getMessage());
        }

        if(testArtifactid != null) { // Only runs if setup was successful
            // - SAFE VALID CASES:
            System.out.println("- Valid cases: ");
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
            System.out.println("- Invalid cases: ");
            // Tests whitespace (newValue can't be empty)
            if (invalidArtifactUpdaterTester(testArtifactid, "name", "   ")) {
                testPasses++;
            } else { testFailures++; }
            if (invalidArtifactUpdaterTester(testArtifactid, "engagement_minutes", "  ")) {
                testPasses++;
            } else { testFailures++; }

            System.out.println("invalid infonames...");
            // Tests invalid infoname
            if (invalidArtifactUpdaterTester(testArtifactid, "Invalid name", "Test value")) {
                testPasses++;
            } else { testFailures++; }
            if(invalidArtifactUpdaterTester(testArtifactid, "   ", "Test value")) {
                testPasses++;
            } else { testFailures++; }
            System.out.println("invalid engagement minute infonames...");
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
        System.out.println("--- ARTIFACT DELETION TESTS --- ");
        // Creates a new artifact for testing:
        try {
            testArtifactid = api.createArtifact("DeleteTestType", "TestName","", 1);
        } catch(Exception e) {
            System.out.println("Setup for update artifacts failed." + e.getMessage());
        }
        if(testArtifactid != null) {
            try {
                System.out.println("Attempting to delete artifact...");
                api.deleteArtifact(testArtifactid);
                if(api.getArtifactInfo(testArtifactid, "name") == null){ // Tries to get info about artifact.
                    System.out.println("PASS - Artifact deletion presumed success as it can no longer be found");
                    testPasses++;
                } else {
                    System.out.println("FAIL -  Artifact deletion failed as values can still be pulled using its ID");
                    testFailures++;
                }
            } catch(Exception e) { // If an exceptions caused somehow
                System.out.println("Delete artifact failed for an unknown reason." + e.getMessage());
                testFailures++;
            }
        } else {
            System.out.println("Delete artifact test setup failed, test artifact ID is null");
            testFailures++;
        }

        // TODO: OUTPUT OF ALL TESTING RESULTS:

        System.out.println("\nARTIFACT CRUD TESTS:\nTests done: " + (testPasses + testFailures) + "Tests failed" + testFailures + "\n\n"); // Outputs test results like in menu tests

    }

    // TODO: COULD PROBABLY MERGE VALID + INVALID METHODS INTO ONE WITH SOME IF STATEMENTS FOR NEATER CODE
    // Tester for creating artifacts with valid parameters
    private boolean validArtifactCreationTester(String type, String name, String description, int engagementMinutes) {
        try {
            String testObjectId = api.createArtifact(type, name, description, engagementMinutes); // Creates artifact with inputted parameters

            // Verifies newly created object exists + variables all match using verfiyArtifactVariables()
            if(verifyArtifactVaraibles(testObjectId, type, name, description, engagementMinutes)) {
                System.out.println("PASS - Artifact creation successful, stored values match inputted");
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
            System.out.println("PARTIAL FAIL - object creation threw exception instead of returning null when given invalid parameters");
            return false;
        }
    }

    // TODO: COULD PROBABLY MERGE VALID + INVALID METHODS INTO ONE WITH SOME IF STATEMENTS FOR NEATER CODE
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
        // Saves test results
        int testPasses = 0;
        int testFailures = 0;

        // --- CREATE EXHIBIT TESTS + READ EXHIBIT INFO TESTING (BY PROXY) ---
        // --- SAFE CASES - All valid case testing (does 2 of example exhibits) ---
        System.out.println("\n--- EXHIBIT CREATION TESTS --- ");
        System.out.println("- Valid cases: ");
        // Safe case A:
        if(validExhibitCreationTester("Global warming", "Warming of the planet due to greenhouse gasses")) {
            testPasses++;
        } else { testFailures++; }
        // Safe case B:
        if(validExhibitCreationTester("Brexit", "The leaving of the european union")) {
            testPasses++;
        } else { testFailures++; }

        // --- INVALID CASES: Invalid cases to test if validation for methods catch them with null/exception
        // Can only test for whitespace/no value
        System.out.println("- Invalid cases: ");
        // Invalid case A:
        if(invalidExhibitCreationTester(" ", "Warming of the planet due to greenhouse gasses")) { // whitespace name
            testPasses++;
        } else { testFailures++; }
        // Invalid case B:
        if(invalidExhibitCreationTester("Brexit", " ")) {// Whitespace description
            testPasses++;
        } else { testFailures++; }

        // TODO: FIND EXHIBITS MAYBE (kinda already done by proxy with create/update though)

        // --- UPDATE EXHIBIT TESTS ---
        System.out.println("--- EXHIBIT UPDATION TESTS --- ");
        // Creates a new artifact for testing:
        String testExhibitid = null;
        try {
            testExhibitid = api.createExhibit("Global warming", "Warming of the planet due to greenhouse gasses");
        } catch(Exception e) {
            System.out.println("Setup for update exhibits failed (couldn't create exhibit to update)" + e.getMessage());
        }

        if(testExhibitid != null) { // Only runs if setup was successful
            System.out.println("- Valid cases: ");
            // - SAFE VALID CASES:
            if (validExhibitUpdaterTester(testExhibitid, "name", "Test Name")) {
                testPasses++;
            } else { testFailures++; }
            if (validExhibitUpdaterTester(testExhibitid, "description", "Test Description")) {
                testPasses++;
            } else { testFailures++; }

            System.out.println("- Invalid cases: ");
            // - INVALID CASES:
            System.out.println("invalid newValue testing (whitespace)...");
            // Tests whitespace (newValue can't be empty)
            if (invalidExhibitUpdaterTester(testExhibitid, "name", "   ")) {
                testPasses++;
            } else { testFailures++; }
            if (invalidExhibitUpdaterTester(testExhibitid, "description", "  ")) {
                testPasses++;
            } else { testFailures++; }

            // Tests invalid infoname
            System.out.println("invalid infoName testing...");
            if (invalidExhibitUpdaterTester(testExhibitid, "Invalid name", "Test value")) {
                testPasses++;
            } else { testFailures++; }
            if(invalidExhibitUpdaterTester(testExhibitid, "   ", "Test value")) {
                testPasses++;
            } else { testFailures++; }
        }


        // -- DELETE EXHIBIT TESTS ---
        // Creates a new exhibit for testing:
        System.out.println("--- EXHIBIT DELETION TESTS --- ");
        try {
            testExhibitid = api.createExhibit("DeleteTestName", "Test description");
        } catch(Exception e) {
            System.out.println("Setup for update exhibits failed (couldn't create exhibit)" + e.getMessage());
        }
        if(testExhibitid != null) {
            try {
                System.out.println("testing exhibit deletion...");
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


        // TEST RESULTS OUTPUT
        System.out.println("\nEXHIBIT CRUD TESTS:\nTests done: " + (testPasses + testFailures) + "Tests failed" + testFailures + "\n\n"); // Outputs test results like in menu tests

    }

    // ------------------------ EXTRA ARTIFACT TESTING SUBMETHODS: ---------------------------------

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


    // TODO: -------------------------------- ANNUAL PLAN CRUD TESTS ---------------------------------------
    @Override
    public void runAnnualPlanCRUDTests()
    {
        // --- CREATE ANNUAL PLAN TESTS + READ ANNUAL PLAN INFO TESTING (BY PROXY) ---
        // --- SAFE CASES - All valid case testing (does 2 of example annual plans) ---

        System.out.println("--- ANNUAL PLAN CREATION TESTS --- ");
        // Safe case A:
        if(validAnnualPlanCreationTester(2025)) {
            testPasses++;
        } else { testFailures++; }
        // Safe case B:
        if(validAnnualPlanCreationTester(2026)) {
            testPasses++;
        } else { testFailures++; }

        // --- INVALID CASES - Invalid cases to test if validation for methods catch them with null return/exception
        // Invalid case A: (Testing duplicate year which should fail)
        if(invalidAnnualPlanCreationTester(2025)) {
            testPasses++;
        } else { testFailures++; }

        // TODO: FIND ANNUAL PLAN TESTS MAYBE

        // --- UPDATE ANNUAL PLAN TESTS ---

        System.out.println("\n--- ANNUAL PLAN UPDATION TESTS --- ");
        // Creates a new annual plan for testing:
        String testPlanId = null;
        try {
            testPlanId = api.createAnnualPlan(2027);
        } catch(Exception e) {
            System.out.println("Setup for update annual plans failed." + e.getMessage());
        }

        if(testPlanId != null) { // Only runs if setup was successful
            System.out.println("- Valid updation tests");
            // - SAFE VALID CASES:
            if (validAnnualPlanUpdaterTester(testPlanId, "year", "2028")) {
                testPasses++;
            } else { testFailures++; }

            // - INVALID CASES:
            System.out.println("- Invalid updation tests");
            // Tests whitespace (newValue can't be empty)
            System.out.println("invalid newValue whitespace testing...");
            if (invalidAnnualPlanUpdaterTester(testPlanId, "year", "   ")) {
                testPasses++;
            } else { testFailures++; }

            // Tests invalid infoname
            System.out.println("invalid infoName testing...");
            if (invalidAnnualPlanUpdaterTester(testPlanId, "Invalid name", "2029")) {
                testPasses++;
            } else { testFailures++; }
            if(invalidAnnualPlanUpdaterTester(testPlanId, "   ", "2029")) {
                testPasses++;
            } else { testFailures++; }

            // Tests invalid year values
            System.out.println("invalid year value testing...");
            if (invalidAnnualPlanUpdaterTester(testPlanId, "year", "Invalid value")) { // Tests non integer
                testPasses++;
            } else { testFailures++; }
            if (invalidAnnualPlanUpdaterTester(testPlanId, "year", "2025")) { // Tests duplicate year (already created)
                testPasses++;
            } else { testFailures++; }
        }

        // -- DELETE ANNUAL PLAN TESTS ---
        System.out.println("\n--- ANNUAL PLAN DELETION TESTS --- ");
        // Creates a new annual plan for testing:
        try {
            testPlanId = api.createAnnualPlan(2030);
        } catch(Exception e) {
            System.out.println("Setup for update annual plans failed." + e.getMessage());
        }
        if(testPlanId != null) {
            try {
                System.out.println("testing annualPlan deletion...");
                api.deleteAnnualPlan(testPlanId);
                if(api.getAnnualPlanInfo(testPlanId, "year") == null){ // Tries to get info about annual plan.
                    System.out.println("PASS - Annual plan deletion presumed success as it can no longer be found");
                    testPasses++;
                } else {
                    System.out.println("FAIL - Annual plan deletion failed as values can still be pulled using its ID");
                    testFailures++;
                }
            } catch(Exception e) { // If an exceptions caused somehow
                System.out.println("Delete annual plan failed for an unknown reason." + e.getMessage());
                testFailures++;
            }
        } else {
            System.out.println("Delete annual plan test setup failed. Test plan ID is null");
            testFailures++;
        }

    }

    // Tester for creating annual plans with valid parameters
    private boolean validAnnualPlanCreationTester(int year) {
        try {
            String testObjectId = api.createAnnualPlan(year); // Creates annual plan with inputted parameters

            // Verifies newly created object exists + variables all match using verfiyAnnualPlanVariables()
            if(verifyAnnualPlanVariables(testObjectId, year)) {
                System.out.println("PASS - Annual plan creation success. All stored values match inputted");
                return true; // If pass true is returned
            } else { return false; } // if fails it returns false

        } catch (Exception e) {
            System.err.println("Annual plan creation failure:" + e.getMessage());
            return false; // Code failed somehow. false returned
        }
    }

    // Tester for creating annual plans with invalid parameters
    private boolean invalidAnnualPlanCreationTester(int year) {
        try {
            String testObjectId = api.createAnnualPlan(year); // Creates annual plan with inputted parameters

            if(testObjectId != null) { // If object was actually created (given an ID and stuff, not null)
                System.out.println("FAIL - Annual plan created when given invalid parameters");
                return false;
            } else {
                System.out.println("PASS - Object creation didn't complete when given invalid parameters");
                return true;
            }
        } catch (Exception e) {
            System.out.println("PASS - Object creation threw exception (expected for invalid parameters)");
            return true; // We expect exception for duplicate years
        }
    }
    // Tester for updating annual plans with valid parameters
    private boolean validAnnualPlanUpdaterTester(String planId, String infoName, String newValue) {
        try {
            api.updateAnnualPlanInfo(planId, infoName, newValue); // Attempts to update annual plan info with given infoName + newValue
            if(api.getAnnualPlanInfo(planId, infoName).equals(newValue)) { // Checks stored value to see if it has been updated
                System.out.println("PASS - UpdateAnnualPlan successfully updated stored value for annual plan correctly"); // If so pass is output
                return true; // Returns true to signify pass
            } else {
                System.out.println("FAIL - UpdateAnnualPlan didn't update stored values");
                return false; // Returns false to signify failure
            }
        } catch(Exception e) { // If an exception occurs (which shouldn't happen for valid inputs)
            System.out.println("FAIL - UpdateAnnualPlan threw exception for valid input values. Exception: " + e.getMessage()); // Test is failed
            return false; // Returns false to signify failure
        }
    }
    // Tester for updating annual plans with invalid parameters
    private boolean invalidAnnualPlanUpdaterTester(String planId, String infoName, String newValue) {
        try {
            api.updateAnnualPlanInfo(planId, infoName, newValue);
            // If no exceptions thrown by attempting to update artifact info with invalid stuff:
            System.out.println("FAIL - UpdateAnnualPlan allowed invalid value: " + newValue); // Test has failed
            return false; // False is returned to signify

        } catch(Exception e) { // If an exception happens (which it's supposed to if input is invalid)
            System.out.println("PASS - UpdateAnnualPlan threw exception (expected for invalid input)");
            return true; // True's is returned to signify pass
        }
    }
    // Tester for checking an annual plans variables match what has been given (and that annual plan exists)
    private boolean verifyAnnualPlanVariables(String testObjectId, int year) {
        // Tests all data in newly created annual plan matches inputted:
        try {
            // Boolean variables to represent if year matches
            boolean yearMatch = Integer.parseInt(api.getAnnualPlanInfo(testObjectId, "year")) == year;

            // TESTS THE ANNUAL PLAN WAS CREATED IN THE FIRST PLACE (id isn't null):
            if (testObjectId == null) { System.out.println("FAIL - Annual plan creation failed. ID is null"); return false; } // If null test fails

            // TESTS IF DATA INSIDE CREATED ANNUAL PLAN MATCHES:
            if (yearMatch) { // If all data is correct
                return true;
            } else {
                System.out.println("FAIL - Not all annual plan data stored matches inputted data.");
                return false;
            }
        } catch (Exception e) {
            System.err.println("FAIL - Annual plan creation threw an exception: " + e.getMessage());
            return false;
        }
    }

    // TODO: REFERENCE CODE HERE pretty sure qub end testing methods are needed for this though
//	    //Update with the actual prompt you expect to have for a the main menu
//	    final String MAIN_MENU_PROMPT = "";
//
//	    try {
//		    prompt = api.getCurrentPrompt();
//		    result = api.processInput("1");
//		    if (prompt != null && MAIN_MENU_PROMPT.equals(prompt)) {
//		      System.out.println("PASS: Found student correctly");
//		    } else {
//		      System.out.println("FAIL: Failed to describe menu");
//		      numberOfErrors = numberOfErrors+1;
//		    }
//		    //Test the result is what you expect
//		    numberOfTests = numberOfTests+1;
//	    } catch (Exception e) {
//	    }

	@Override
    public void runMenuTests() 
    {
        QUBMuseum api = new QUBMuseum(); // Resets QUB museum for fresh testing unaffected by CRUD tests

        // TODO: figure out what these do
		String prompt;
	    String result;
	    
	    int numberOfErrors = 0;
	    int numberOfTests = 0;

        System.out.println("---- RUNNING MENU TESTS ---- ");



        // TODO: ACTUAL TESTING CODE:

        // EXAMPLE ARTIFACT:
        //"Sculpture";"Acropolis Statues";"Cool statues";5

        // MANAGE ARTIFACT TESTING

        // Add artifact test
        System.out.println("- Manage artifacts tests");
        System.out.println("add an artifact...");
        String[] addArtifactInputs = {"1", "1", "Sculpture;Acropolis Statues;Cool statues;5"};
        if(!doSpecificMenuTest(api, addArtifactInputs, "Artifact added successfully", "Add artifact test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // View artifact test
        System.out.println("view an artifact...");
        String[] viewArtifactInputs = {"1", "2", "Sculpture;Acropolis Statues;Cool statues;5"};
        if(!doSpecificMenuTest(api, viewArtifactInputs, "Artifact added successfully", "Add artifact test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // Update artifact test
        System.out.println("update an artifact...");
        String[] updateArtifactInputs = {"4", "0;name;Updated Name"};
        if(!doSpecificMenuTest(api, updateArtifactInputs, "Artifact updated successfully", "Update artifact test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // Delete artifact test
        System.out.println("delete an artifact...");
        String[] deleteArtifactInputs = {"3", "0"};
        if(!doSpecificMenuTest(api, deleteArtifactInputs, "Artifact deleted successfully", "Delete artifact test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // Go Back to Main Menu
        System.out.println("returning to main menu...");
        String[] goBackInputs = {"5"};
        if(!doSpecificMenuTest(api, goBackInputs, "Returning to main menu", "Go back test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // MANAGE EXHIBITS TESTING
        System.out.println("- Manage exhibits tests");

        // Add exhibit test
        System.out.println("add an exhibit...");
        String[] addExhibitInputs = {"2", "1", "Space;A collection of space stuff"};
        if(!doSpecificMenuTest(api, addExhibitInputs, "Exhibit added successfully", "Add exhibit test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // Update exhibit test
        System.out.println("update an exhibit...");
        String[] updateExhibitInputs = {"4", "0;name;Cosmos"};
        if(!doSpecificMenuTest(api, updateExhibitInputs, "Exhibit updated successfully", "Update exhibit test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // Delete exhibit test
        System.out.println("delete an exhibit...");
        String[] deleteExhibitInputs = {"3", "0"};
        if(!doSpecificMenuTest(api, deleteExhibitInputs, "deleted successfully", "Delete exhibit test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // Go Back to Main Menu
        System.out.println("returning to main menu...");
        String[] goBackExhibitInputs = {"8"};
        if(!doSpecificMenuTest(api, goBackExhibitInputs, "Returning to main menu", "Go back test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // MANAGE ANNUAL PLANS TESTING
        System.out.println("- Manage annual plans tests");

        // Add annual plan test
        System.out.println("add an annual plan...");
        String[] addPlanInputs = {"3", "1", "2025"};
        if(!doSpecificMenuTest(api, addPlanInputs, "Annual plan added successfully", "Add annual plan test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // Update annual plan test
        System.out.println("update an annual plan...");
        String[] updatePlanInputs = {"4", "0;year;2026"};
        if(!doSpecificMenuTest(api, updatePlanInputs, "Annual plan updated successfully", "Update annual plan test"))
        { numberOfErrors++; }
        numberOfTests ++;

        // Delete annual plan test
        System.out.println("delete an annual plan...");
        String[] deletePlanInputs = {"3", "0"};
        if(!doSpecificMenuTest(api, deletePlanInputs, "Annual plan deleted successfully", "Delete annual plan test"))
        { numberOfErrors++; }
        numberOfTests ++;


        // TODO: OUTPUT OF ALL TESTING RESULTS
        System.out.println("\nMENU TEST RESULTS:\nTests done: " + numberOfTests + "Tests failed" + numberOfErrors); // Outputs test results

    }

    // Sub method to significantly reduce code rewriting -> just put in inputs + expected outputs and all processing
    // and checking final inputs done in this method. takes tests name for debugging using console logs later
    // could probably do expectedEndTest as an array but that would use so much space and would only be cosmetic bugs anyway
    // Takes API as input so testings all in 1 big thing and previous menu tests count so its not super isolated
    private boolean doSpecificMenuTest(QUBMuseum api, String[] inputs, String expectedResultText, String testName) {
        try { // Tries it so tester wont crash if it throws exception for some reason
            String resultText = "Unknown";
            for (String input : inputs) {
                resultText = api.processInput(input);
            }
            if (resultText.equals(expectedResultText)) {
                System.out.println("PASS - " + testName + " passed.");
                return true;
            } else {
                System.out.println("FAIL - " + testName + " threw exception: " + expectedResultText + " end text:" + resultText);
                return false;
            }
        } catch(Exception e) {
            System.out.println("FAIL - " + testName + " threw exception: " + e.getMessage());
            return false;
        }
    }

    // TODO: probably will need to make this more efficient will see
	@Override
    public void runFuzzingTests() // Random user input tests
    {
        int exceptionsThrown = 0; // Records amount of exceptions thrown
        int totalLoops = 20000; // amount of fuzzing loops done. TODO: MAY NEED TO BE MORE AS CHANCE OF SEMICOLON IS 1/255, MAY NEED TO ADD EXTRA SEMICOLON CHANCES OR MORE LOOPS
    	for(int i = 0; i < totalLoops; i++)
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
                char randomlyGeneratedChar = (char)(randomCharIndexValue); // Generates a random char to add to strig
                randomlyGeneratedString = randomlyGeneratedString + randomlyGeneratedChar; // Combines existing string with random character
            }
            try {
                // Tries combination of random number inputs + random strings to test programme. Mixes in random numbers so it actually goes through the menus
                // (instead of doing 99.99999% of testing on main menu)
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
            System.out.println("FUZZING TEST RESULTS\nFuzzing test passed, 0 exceptions were thrown over: " + totalLoops +  "loops");
        } else {
            System.out.println("Fuzzing exceptions thrown: " + exceptionsThrown );
        }
    }
}
