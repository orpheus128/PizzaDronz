package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static uk.ac.ed.inf.Main.main;

public class SystemTest {

    @Test
    public void systemTest(){
        String args[] = {"2025-02-01", "https://ilp-rest-2024.azurewebsites.net/"};
        long start = System.currentTimeMillis();
        main(args);
        long finish = System.currentTimeMillis();
        long elapsed = finish-start;
        assertTrue(elapsed < 60000);
    }
}
