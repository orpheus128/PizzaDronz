package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * A class which could be used to store constants needed for the system
 * Currently used only for the Appleton Tower coordinates
 */
public class Constants {
    private static final LngLat appletonPoint = new LngLat(-3.186701402841254,55.94462659449748);

    /**
     * @return the location of Appleton Tower
     */
    public static LngLat getAppletonPoint(){
        return appletonPoint;
    }
}
