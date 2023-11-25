package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.*;
import java.util.*;
import java.util.stream.Collectors;

public class DroneRouter {

    public ArrayList<DroneMove[]> getAllPaths(Order[] orders, Restaurant[] restaurantArray, NamedRegion[] noFlyZones, NamedRegion central){

        orders = validateOrders(orders, restaurantArray);
        ArrayList<Restaurant> visitedRestaurants = new ArrayList<>();
        ArrayList<DroneMove[]> flightPaths = new ArrayList<>();
        ArrayList<DroneMove[]> returnFlightPaths = new ArrayList<>();


        for (Order order: orders) {
            Restaurant restaurant = restaurantFromPizza(order, restaurantArray);
            if (visitedRestaurants.contains(restaurant)){
                int pathIndex = visitedRestaurants.indexOf(restaurant);
                returnFlightPaths.add(flightPaths.get(pathIndex));
            }else {
                visitedRestaurants.add(restaurant);
                DroneMove[] flightPath = getFlightPath(order, restaurant, noFlyZones, central);
                flightPaths.add(flightPath);
                returnFlightPaths.add(flightPath);
            }
        }

        return returnFlightPaths;
    }

    private Order[] validateOrders(Order[] orders, Restaurant[] restaurantArray){

        OrderValidator orderValidator = new OrderValidator();
        ArrayList<Order> validOrders = new ArrayList<>();
        for (Order order : orders){
            orderValidator.validateOrder(order, restaurantArray);
            if (order.getOrderValidationCode() == OrderValidationCode.NO_ERROR){
                validOrders.add(order);
            }
        }

        Order[] orderArray = new Order[validOrders.size()];

        return validOrders.toArray(orderArray);
    }

    public DroneMove[] getFlightPath(Order order, Restaurant restaurant, NamedRegion[] noFlyZones, NamedRegion central){

        PriorityQueue<PositionNode> frontier = new PriorityQueue<PositionNode>(Comparator.comparingDouble(p -> p.getF()));
        HashSet<PositionNode> visited = new HashSet<PositionNode>();
        ArrayList<DroneMove> flightPath;

        LngLat startPoint = restaurant.location();

        LngLat endPoint = new Constants().getAppletonPoint(); //LngLat co-ords of Appleton, stored as a constant

        LngLatHandler lngLatHandler = new LngLatHandler();

        double estDistance = lngLatHandler.distanceTo(startPoint, endPoint);

        List<PositionNode> tempFrontierList = new ArrayList<>();

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

            if (lngLatHandler.isCloseTo(currentPosition.getPosition(), endPoint)){

                flightPath = constructPath(currentPosition, startPoint, endPoint);
                DroneMove[] flightArray = new DroneMove[flightPath.size()];

                return flightPath.toArray(flightArray);
            }

            boolean inNoFlyZone = false;
            int counter = 0;

            //calculates the next 16 points to travel to, and if those points exist already in the frontier
            //then calculates if
            for (Double angle : angles) {
                positionToAdd = new PositionNode(lngLatHandler.nextPosition(currentPosition.getPosition(), angle));
                if (lngLatHandler.isInCentralArea(positionToAdd.getPosition(), central)){
                    enteredCentral = true;
                }
                if (enteredCentral && !lngLatHandler.isInCentralArea(positionToAdd.getPosition(), central)){
                    continue;
                }
                //the above code is meant to stop the drone from reentering the central area, but currently doesnt work.
                while (!inNoFlyZone & counter < noFlyZones.length){
                    if (lngLatHandler.isInRegion(positionToAdd.getPosition(), noFlyZones[counter])){
                        inNoFlyZone = true;
                    }
                    counter++;
                }
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

    private ArrayList<DroneMove> constructPath(PositionNode currentPosition, LngLat startPoint, LngLat endPoint){

        ArrayList<DroneMove> flightPath = new ArrayList<>();
        ArrayList<DroneMove> returnPath = new ArrayList<>();

        flightPath = partialPath(currentPosition, flightPath);
        flightPath.add(new DroneMove(startPoint, 999, startPoint)); //set angle to 999
        returnPath = partialPath(currentPosition, returnPath);
        returnPath.add(new DroneMove(endPoint, 999, endPoint));
        Collections.reverse(returnPath);

        flightPath.addAll(returnPath);

        return flightPath;
    }

    private ArrayList<DroneMove> partialPath(PositionNode currentPosition, ArrayList<DroneMove> flightPath){

        while (currentPosition != null) {
            if (currentPosition.getParent() != null){
                flightPath.add(new DroneMove(currentPosition.getParent().getPosition(), currentPosition.getAngle(), currentPosition.getPosition()));
                currentPosition = currentPosition.getParent();
            }else{
                currentPosition = null;
            }
        }
        return flightPath;
    }
}
