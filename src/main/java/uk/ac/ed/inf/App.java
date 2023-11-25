package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.ArrayList;
import java.util.List;
import com.mapbox.geojson.*;

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

        todayOrders = validateOrders(todayOrders, restaurants);

        DroneRouter droneRouter = new DroneRouter();

        ArrayList<FlightPath> flightPaths = droneRouter.getAllPaths(todayOrders, restaurants, noFlyZones, centralArea);
        ArrayList<DroneMove> finalPath = new ArrayList<>();
        DroneMove[] paths;

        for (FlightPath flight : flightPaths){
            paths = flight.getFlightPath(flight.getOrder());
            finalPath.addAll(List.of(paths));
        }

        JsonSerialiser jsonSerialiser = new JsonSerialiser();

        jsonSerialiser.createOrderArray(todayOrders);
        jsonSerialiser.createFlightArray(finalPath);

        jsonSerialiser.createResultFiles(args[0]);

//        for (DroneMove move : finalPath){
//            System.out.println(move.toString());
//        }
//        System.out.println(finalPath);
    }

    private static Order[] validateOrders(Order[] orders, Restaurant[] restaurantArray){

        OrderValidator orderValidator = new OrderValidator();
        for (Order order : orders){
            orderValidator.validateOrder(order, restaurantArray);
        }
        return orders;
    }
}
