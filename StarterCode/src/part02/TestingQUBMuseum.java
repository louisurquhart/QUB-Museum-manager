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
    
	@Override
    public void runArtifactCRUDTests() 
    {
    }
    
	@Override
    public void runExhibitCRUDTests() 
    {
    	
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

    
	@Override
    public void runFuzzingTests() 
    {
    	
    }
}