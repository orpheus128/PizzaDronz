package uk.ac.ed.inf;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertTrue;
import static uk.ac.ed.inf.Main.main;

public class InputHandlingTests {
    @Test
    void correctInput(){
        String args[] = {"2025-02-01", "https://ilp-rest-2024.azurewebsites.net/"};
        main(args);
        assertTrue(App.error_code == "No error");
    }

    @Test
    void incorrectURL(){
        String args[] = {"2024-02-01", "https://ilp-rest-2024.azurewebsites/"};
        try{
            main(args);
        }catch (Exception e){
            assertTrue(RestConnection.error_code == "URL Wrong");
        }
    }

    @Test
    void incorrectDateFormat(){
        String args[] = {"2024-02-0", "https://ilp-rest-2024.azurewebsites.net/"};

        try{
            main(args);
        }catch (Exception e){
            assertTrue(RestConnection.error_code == "Date Wrong");
        }
    }


    //test where i check the main.error thing to see if there are not enough args
    //test where i check the restconnection.error to see what the error is
    //need to introduce a variable or error code thing
    //not for here but need to write a system test
}
