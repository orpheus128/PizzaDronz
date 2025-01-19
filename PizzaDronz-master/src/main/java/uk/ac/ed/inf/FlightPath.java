package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;

/**
 * FlightPath is a class that makes it easier to format the DroneMoves into a flightPath, and allows the algorithm to
 * skip calculating a path for every order, and instead store the flightPaths, as we need to change the orderNo
 */
public class FlightPath {
    private final Order order;
    private final DroneMove[] flightPath;
    public FlightPath(Order order, DroneMove[] flightPath){
        this.order = order;
        this.flightPath = flightPath;
    }

    /**
     * getFlightPath updates the orderNo for the stored DroneMoves, this allows the algorithm to assign one path to
     * multiple orders.
     * @param order is the order which is assigned to the flight path
     * @return a list of DroneMoves with the orderNos updated
     */
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
