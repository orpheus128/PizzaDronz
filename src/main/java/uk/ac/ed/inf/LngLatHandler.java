package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.CentralRegionVertexOrder;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

import java.awt.geom.Line2D;
import java.util.ArrayList;

public class LngLatHandler implements uk.ac.ed.inf.ilp.interfaces.LngLatHandling {

    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        /**
         * Calculate distance from one point to another using the pythagorean theorem
         * @param startPosition is the position you are calculating the distance from
         * @param endPosition is the position you are calculating the distance to
         */

        //Here, I store the longitude and latitude from the start and end positions in variables as it prevents the
        //final calculation being messy
        double startLng = startPosition.lng();
        double startLat = startPosition.lat();

        double endLng = endPosition.lng();
        double endLat = endPosition.lat();

        double innerCalc = ((startLng - endLng)*(startLng - endLng)) + ((startLat - endLat)*(startLat - endLat));

        return Math.sqrt(innerCalc);
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        /**
         * as per the specification, if a point is within 0.00015 of another point, it is considered close
         * @param startPosition is the first position
         * @param otherPosition is the position you are checking is close to the first position
         */

        return distanceTo(startPosition, otherPosition) < 0.00015;
    }

    @Override
    public boolean isInCentralArea(LngLat point, NamedRegion centralArea) {
        if (centralArea == null) {
            throw new IllegalArgumentException("the named region is null");
        } else if (!centralArea.name().equals("central")) {
            throw new IllegalArgumentException("the named region: " + centralArea.name() + " is not valid - must be: central");
        } else {
            return this.isInRegion(point, centralArea);
        }
    }

    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        /**
         * Uses the ray casting algorithm to test if there are any intersections,
         * if the number of intersections is even, the point lies outwith the region
         * if the number of intersections is odd, the point lies within the region
         * @param position is the current position you are checking
         * @param region is the region that the position may or may not be in
         */

        LngLat[] vertices = region.vertices();
        double x1;
        double x2;
        double y1;
        double y2;
        int numIntersections = 0;

        for (int i = 0; i < vertices.length; i++){
            x1 = vertices[i].lng();
            x2 = vertices[((i+1) % vertices.length)].lng();
            y1 = vertices[i].lat();
            y2 = vertices[((i+1) % vertices.length)].lat();

            if ((y1 > position.lat()) != (y2 > position.lat()) &&
                    ((position.lng() < (x2 - x1) * (position.lat() - y1) / (y2 - y1) + x1))){
                numIntersections++;
            }
        }

        return numIntersections % 2 == 1;
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        /**
         * returns the next position the drone will move to when given an angle which represents one of the 16 major
         * compass directions
         * @param startPosition is the initial position
         * @param angle is the direction the drone will move in (East is 0, North is 90, etc)
         */

        if (angle == 999){return startPosition;}
        else if(angle % 22.5 != 0){return startPosition;}
        else{
            double lng = (0.00015 * Math.cos(angle) + startPosition.lng());
            double lat = (0.00015 * Math.sin(angle) + startPosition.lat());

            return new LngLat(lng,lat);
        }

    }


}
