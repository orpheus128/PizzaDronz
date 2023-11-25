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
        return this.fromPosition;
    }
    public double getAngle() {
        return this.angle;
    }
    public LngLat getToPosition(){
        return this.toPosition;
    }
}
