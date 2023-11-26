package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;
import java.util.*;
import java.util.stream.Collectors;

public class DroneRouter {

    /**
     * getAllPaths assigns paths to each order, if they are valid
     * It does this by looping through the list of orders, checking if the order is valid, then if it is the restaurant
     * is checked against a list of restaurants to see if a path has already been calculated for that restaurant.
     * If there is already a path to that restaurant, that path is retrieved from a list of possible paths.
     * @param orders is the list of orders with validation codes set
     * @param restaurantArray is the list of restaurants which we will use to make pathfinding quicker
     * @param noFlyZones is the list of no-fly zones
     * @param central is the boundary of central
     * @return an ArrayList of the flight paths which will be used to print the output file
     */
    public ArrayList<FlightPath> getAllPaths(Order[] orders, Restaurant[] restaurantArray, NamedRegion[] noFlyZones, NamedRegion central){

        ArrayList<Restaurant> visitedRestaurants = new ArrayList<>();
        ArrayList<FlightPath> flightPaths = new ArrayList<>();
        ArrayList<FlightPath> returnFlightPaths = new ArrayList<>();

        for (Order order: orders) {
            if (order.getOrderValidationCode() == OrderValidationCode.NO_ERROR) {
                Restaurant restaurant = restaurantFromPizza(order, restaurantArray);
                if (visitedRestaurants.contains(restaurant)) {
                    int pathIndex = visitedRestaurants.indexOf(restaurant);
                    order.setOrderStatus(OrderStatus.DELIVERED); //the algorithm always finds a path, so we set any valid order to delivered
                    //the following line assigns an order to an ArrayList of DroneMoves
                    FlightPath flightPath = new FlightPath(order, flightPaths.get(pathIndex).getFlightPath(order));
                    returnFlightPaths.add(flightPath);
                } else {
                    visitedRestaurants.add(restaurant);
                    DroneMove[] flightRoute = getFlightRoute(order, restaurant, noFlyZones, central);
                    order.setOrderStatus(OrderStatus.DELIVERED);
                    FlightPath flightPath = new FlightPath(order, flightRoute);
                    flightPaths.add(flightPath);
                    returnFlightPaths.add(flightPath);
                }
            }
        }

        return returnFlightPaths;
    }

    /**
     * Performs a search algorithm on the given order to create a path to the restaurant included in the order.
     * This path is created from the restaurant's location to Appleton, as this allows it to prevent the drone from
     * leaving central after entering it without making its delivery. This path is then constructed.
     * The algorithm checks points in an 8 point compass around the current position, and chooses the best of them
     * It finds this best point by adding the distance travelled to the distance from the destination, which is weighted
     * to encourage the algorithm to pick points closer to the destination.
     * @param order is the order we are checking
     * @param restaurant is the restaurant we are finding a path to
     * @param noFlyZones is the list of no-fly zones
     * @param central is the central area Appleton Tower is located in
     * @return
     */
    public DroneMove[] getFlightRoute(Order order, Restaurant restaurant, NamedRegion[] noFlyZones, NamedRegion central){

        PriorityQueue<PositionNode> frontier = new PriorityQueue<PositionNode>(Comparator.comparingDouble(p -> p.getF()));
        HashSet<PositionNode> visited = new HashSet<PositionNode>();
        ArrayList<DroneMove> flightPath;

        LngLat startPoint = restaurant.location();

        LngLat endPoint = new Constants().getAppletonPoint(); //LngLat co-ords of Appleton, stored as a constant

        LngLatHandler lngLatHandler = new LngLatHandler();

        double estDistance = lngLatHandler.distanceTo(startPoint, endPoint);

        double[] angles = {0, 45, 90, 135, 180, 225, 270, 315, 360};

        PositionNode currentPosition = new PositionNode(startPoint);
        currentPosition.setParent(null);
        currentPosition.setH(estDistance);
        currentPosition.setG(0);
        currentPosition.setF(currentPosition.getG() + currentPosition.getH());
        currentPosition.setAngle(999);
        PositionNode positionToAdd;
        double tempG;
        PositionNode tempNode;
        boolean enteredCentral = false;

        frontier.add(currentPosition);

        while (!frontier.isEmpty()) {

            currentPosition = frontier.poll();
            visited.add(currentPosition);

            //if the current point is close to the end point, the flight path is constructed and then returned.
            if (lngLatHandler.isCloseTo(currentPosition.getPosition(), endPoint)){
                flightPath = constructPath(currentPosition, startPoint, endPoint, order);
                DroneMove[] flightArray = new DroneMove[flightPath.size()];
                return flightPath.toArray(flightArray);
            }

            boolean inNoFlyZone = false;
            int counter = 0;

            for (Double angle : angles) {
                positionToAdd = new PositionNode(lngLatHandler.nextPosition(currentPosition.getPosition(), angle));

                //if the drone enters central area it cannot leave, so any points generated outside of the central area
                //after this flag is set are ignored
                if (lngLatHandler.isInCentralArea(positionToAdd.getPosition(), central) && !enteredCentral){
                    enteredCentral = true;
                }
                if (enteredCentral && !lngLatHandler.isInCentralArea(positionToAdd.getPosition(), central)){
                    continue;
                }

                //sets a flag if the algorithm generates a point within a no-fly zone
                while (!inNoFlyZone & counter < noFlyZones.length){
                    if (lngLatHandler.isInRegion(positionToAdd.getPosition(), noFlyZones[counter])){
                        inNoFlyZone = true;
                    }
                    counter++;
                }

                //the following code checks if the point is not in a no-fly zone, then checks if the point generated
                //already exists in the frontier, and if the path to that point is better than the previous best path
                //to it. If it is, the point is replaced with this new generated point.
                //If the point is not already visited, then it is added to the frontier.
                if (!inNoFlyZone){
                    double tentativeG = currentPosition.getG() + 0.00015;

                    if (!isPointAlreadyVisited(positionToAdd, visited)){
                        tempNode = findPoint(frontier, positionToAdd.getPosition());
                        if (tempNode != null){
                            tempG = tempNode.getG();

                            if (tentativeG < tempG){
                                tempNode.setParent(currentPosition);
                                tempNode.setG(currentPosition.getG() + 0.00015);
                                tempNode.setH(lngLatHandler.distanceTo(tempNode.getPosition(), endPoint));
                                tempNode.setF(tempNode.getG() + tempNode.getH());
                                tempNode.setAngle(angle);
                                frontier.add(positionToAdd);
                            }
                        }else{
                            positionToAdd.setParent(currentPosition);
                            positionToAdd.setG(currentPosition.getG() + 0.00015);
                            positionToAdd.setH(lngLatHandler.distanceTo(positionToAdd.getPosition(), endPoint));
                            positionToAdd.setF(currentPosition.getG() + currentPosition.getH());
                            positionToAdd.setAngle(angle);
                            frontier.add(positionToAdd);
                        }
                    }
                }
                else{
                }
            }
        }

        return null;
    }

    /**
     * findPoint searches the frontier for the LngLat position passed in, and returns the PositionNode that matches the
     * passed position, if it exists.
     * @param frontier is the PriorityQueue of PositionNodes which have been created to find a path
     * @param position is the coordinates of the current point being passed to findPoint
     * @return
     */
    private PositionNode findPoint(PriorityQueue<PositionNode> frontier, LngLat position){

        Iterator<PositionNode> iterator = frontier.iterator();

        while (iterator.hasNext()){
            PositionNode next = iterator.next();
            if (next.getPosition().lng() == position.lng()
                && next.getPosition().lat() == position.lat()){
                return next;
            }
        }
        return null;
    }

    /**
     * isPointAlreadyVisited checks the HashSet visited to see if the algorithm has already visited the PositionNode
     * that is passed in
     * @param positionNode is the point which is being checked
     * @param visited is the HashSet of visited PositionNodes
     * @return
     */
    private Boolean isPointAlreadyVisited(PositionNode positionNode, HashSet<PositionNode> visited){

        boolean isVisited = !visited.stream()
                .filter(e -> e.getPosition().lng() == positionNode.getPosition().lng() &
                        e.getPosition().lat() == positionNode.getPosition().lng())
                .collect(Collectors.toList()).isEmpty();
        if (isVisited){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Matches a restaurant to an order, and then returns it
     * @param order is the order to match to a restaurant
     * @param restaurantArray is the list of possible restaurants that could be ordered from
     * @return the restaurant that the passed order is ordering from
     */
    private Restaurant restaurantFromPizza(Order order, Restaurant[] restaurantArray){
        Pizza restaurantPizza = order.getPizzasInOrder()[0];
        //order is validated before flight path is calculated, so all pizzas in the order will be from the same restaurant
        List<Restaurant> restaurantList = Arrays.asList(restaurantArray);
        List<Restaurant> listFromStream = restaurantList.stream()
                .filter(e -> Arrays.asList(e.menu())
                        .contains(restaurantPizza))
                .collect(Collectors.toList());
        //This stream creates a list with one element, the restaurant we wish to navigate to

        return listFromStream.get(0);
    }

    /**
     * constructPath creates two paths from the given currentPosition, and then appends them into one flightPath
     * the returningPath is reversed as it is a list of DroneMoves from Appleton to the restaurant
     * @param currentPosition is the final position of the algorithm
     * @param startPoint is the coordinates that the algorithm begins searching from (the restaurant)
     * @param endPoint is the coordinates that the algorithm finishes close to (Appleton Tower)
     * @param order is the order which the path is being constructed for
     * @return an ArrayList of DroneMoves which makes a flightPath
     */
    private ArrayList<DroneMove> constructPath(PositionNode currentPosition, LngLat startPoint, LngLat endPoint, Order order){

        ArrayList<DroneMove> flightPath = new ArrayList<>();
        ArrayList<DroneMove> returningPath = new ArrayList<>();

        flightPath = partialPath(currentPosition, flightPath, order);
        flightPath.add(new DroneMove(startPoint, 999, startPoint, order.getOrderNo())); //set angle to 999
        returningPath.add(new DroneMove(endPoint, 999, endPoint, order.getOrderNo()));
        returningPath = partialReturningPath(currentPosition, returningPath, order);

        Collections.reverse(returningPath);

        flightPath.addAll(returningPath);

        return flightPath;
    }

    /**
     * Due to the algorithm finding a path from the restaurant to Appleton Tower, partialPath travels backwards from
     * currentPosition, which is the final position (which is close to Appleton Tower). Because of this, the angles
     * stored in the PositionNodes must be flipped (by adding 180 to the degree and then applying modulo 360)
     * This information is then stored in an ArrayList of DroneMoves, with the orderNo
     * @param currentPosition
     * @param flightPath
     * @param order
     * @return
     */
    private ArrayList<DroneMove> partialPath(PositionNode currentPosition, ArrayList<DroneMove> flightPath, Order order){

        double angle;
        while (currentPosition != null) {
            if (currentPosition.getParent() != null){
                angle = (currentPosition.getAngle()+180)%360; //flips the angle calculated by the original routing
                String orderNo = order.getOrderNo();
                flightPath.add(new DroneMove(currentPosition.getParent().getPosition(), angle, currentPosition.getPosition(), orderNo));
                currentPosition = currentPosition.getParent();
            }else{
                currentPosition = null;
            }
        }
        return flightPath;
    }

    /**
     * The partialReturningPath is constructed by simply tracing back through the PositionNodes and creating DroneMoves
     * from them.
     * @param currentPosition is the PositionNode close to Appleton
     * @param returningPath is an empty ArrayList of DroneMoves that will be returned
     * @param order is the order which this path is calculated for
     * @return a constructed path
     */
    private ArrayList<DroneMove> partialReturningPath(PositionNode currentPosition, ArrayList<DroneMove> returningPath, Order order){

        while (currentPosition != null) {
            if (currentPosition.getParent() != null){
                String orderNo = order.getOrderNo();
                returningPath.add(new DroneMove(currentPosition.getParent().getPosition(),
                        currentPosition.getAngle(), currentPosition.getPosition(), orderNo));

                currentPosition = currentPosition.getParent();
            }else{
                currentPosition = null;
            }
        }
        return returningPath;
    }
}
