package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;

public class LngLatHandling implements uk.ac.ed.inf.ilp.interfaces.LngLatHandling {

    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {


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

        region.name();
        region.vertices();


        //Need to have the position lat and long be within bounds
        return false;
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {

        if (angle == 999){return startPosition; }
        else{
            double lng = (0.00015 * Math.cos(angle) + startPosition.lng());
            double lat = (0.00015 * Math.sin(angle) + startPosition.lat());

            return new LngLat(lng,lat);
        }

    }


}
