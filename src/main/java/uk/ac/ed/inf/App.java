package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args)
    {
        RestConnection restConnection = new RestConnection();

        Order[] todayOrders = restConnection.getOrderData(args);
        Restaurant[] restaurants = restConnection.getRestaurantData(args);
        NamedRegion centralArea = restConnection.getCentralArea(args);
        NamedRegion[] noFlyZones = restConnection.getNoFlyZones(args);
        Boolean isAlive = restConnection.isAlive(args);



        DroneRouter droneRouter = new DroneRouter();

        droneRouter.getAllPaths(todayOrders, restaurants, noFlyZones, centralArea);
//        System.out.println(droneRouter.getFlightPath(validOrders.get(0), restaurants, noFlyZones, centralArea));
    }
}
