package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

public class DroneMove {

    private LngLat fromPosition;
    private double angle;
    private LngLat toPosition;
    public DroneMove(LngLat fromPosition, double angle, LngLat toPosition){
        this.fromPosition = fromPosition;
        this.angle = angle;
        this.toPosition = toPosition;
    }

    public LngLat getFromPosition() {
        return fromPosition;
    }
    public double getAngle() {
        return angle;
    }
    public LngLat getToPosition(){
        return toPosition;
    }
}
