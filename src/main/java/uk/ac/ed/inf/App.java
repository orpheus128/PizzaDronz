package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class App 
{
    /**
     * Creates a rest connection, then retrieves the orders, restaurants, centralArea, and noFlyZones, if it is alive
     * Orders are then validated, then passed into a DroneRouter class to create all the paths for valid orders
     * All paths are then consolidated into one flight path to be printed as a JSON file
     * Three output files are then generated using methods from the JsonSerialiser class
     * @param args contains the date and the name of the rest server
     */
    public static void app(String[] args)
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
    }

    /**
     * validateOrders takes in a list of orders and validates them using a function in the OrderValidator class.
     * @param orders is the list of orders to be validated
     * @param restaurantArray is the list of restaurants that need to be passed into validateOrder from orderValidator
     * @return a list of orders which have their validation code changed to either NO_ERROR or a code which reflects the
     * issue with it
     */
    private static Order[] validateOrders(Order[] orders, Restaurant[] restaurantArray){

        OrderValidator orderValidator = new OrderValidator();
        for (Order order : orders){
            orderValidator.validateOrder(order, restaurantArray);
        }
        return orders;
    }
}
