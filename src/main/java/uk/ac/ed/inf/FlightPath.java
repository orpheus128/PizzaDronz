package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;

public class FlightPath {
    private final Order order;
    private final DroneMove[] flightPath;
    public FlightPath(Order order, DroneMove[] flightPath){
        this.order = order;
        this.flightPath = flightPath;
    }
    public DroneMove[] getFlightPath(Order order) {

        for (DroneMove move : flightPath){
            move.setOrderNo(order.getOrderNo());
        }
        return flightPath;
    }
    public Order getOrder() {
        return order;
    }
}
