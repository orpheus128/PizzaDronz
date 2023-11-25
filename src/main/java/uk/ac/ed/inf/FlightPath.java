package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;

public class FlightPath {
    private Order order;
    private DroneMove[] flightPath;
    public FlightPath(Order order, DroneMove[] flightPath){
        this.order = order;
        this.flightPath = flightPath;
    }
    public DroneMove[] getFlightPath() {
        return flightPath;
    }
    public Order getOrder() {
        return order;
    }
}
