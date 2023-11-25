package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

public class App 
{
    public static void main(String[] args)
    {
        RestConnection restConnection = new RestConnection();

        Boolean isAlive = restConnection.isAlive(args);

        if (!isAlive){
            throw new RuntimeException("Provided REST Server is not alive");
        }

        Order[] todayOrders = restConnection.getOrderData(args);
        Restaurant[] restaurants = restConnection.getRestaurantData(args);
        NamedRegion centralArea = restConnection.getCentralArea(args);
        NamedRegion[] noFlyZones = restConnection.getNoFlyZones(args);

        DroneRouter droneRouter = new DroneRouter();

        System.out.println(droneRouter.getAllPaths(todayOrders, restaurants, noFlyZones, centralArea));
    }
}
