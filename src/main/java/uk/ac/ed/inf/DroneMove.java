package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * DroneMove stores a move for a drone, with the position it's moving from, the angle it's moving at, and the position
 * it's moving to, as well as storing the orderNo the move is for.
 */
public class DroneMove {

    private final LngLat fromPosition;
    private final double angle;
    private final LngLat toPosition;
    private String orderNo;
    public DroneMove(LngLat fromPosition, double angle, LngLat toPosition, String orderNo){
        this.fromPosition = fromPosition;
        this.angle = angle;
        this.toPosition = toPosition;
        this.orderNo = orderNo;
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
    public String getOrderNo() {
        return orderNo;
    }
    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

//    @Override
//    public String toString() {
//        return "DroneMove{" +
//                "fromLng=" + fromPosition.lng() +
//                ", fromLat=" + fromPosition.lat() +
//                ", angle=" + angle +
//                ", toLng=" + toPosition.lng() +
//                ", toLat=" + toPosition.lat() +
//                ", orderNo='" + orderNo + '\'' +
//                '}';
//    }
}
