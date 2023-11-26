package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * JsonSerialiser creates the result files which are to be outputted.
 */
public class JsonSerialiser {

    ArrayList<DroneMove> flightArray = new ArrayList<>();

    Order[] orders;

    public JsonSerialiser(){

    }
    public void createFlightArray(ArrayList<DroneMove> flightPath){
        this.flightArray = flightPath;
    }

    public void createOrderArray(Order[] orders){
        this.orders = orders;
    }

    /**
     * Creates the deliveries output file by looping through all orders and appending them to a string
     * The deliveries are then written to a file in the resultfiles folder.
     * @param date is the date given by the user
     */
    public void createDeliveriesFile(String date){

        String record;
        ArrayList<String> printString = new ArrayList<>();

        for (Order order : orders) {

            record = "{\"orderNo\":\"" + order.getOrderNo() + "\",\"orderStatus\":\"" + order.getOrderStatus() +
                        "\",\"orderValidationCode\":\"" + order.getOrderValidationCode() + "\",\"costInPence\":\"" +
                        order.getPriceTotalInPence() + "\"}";

            printString.add(record);
        }

        try (FileWriter fileWriter = new FileWriter(".\\resultfiles\\deliveries-" + date + ".json")){
            fileWriter.write(String.valueOf(printString));
        }catch(Exception e){
            throw new RuntimeException("Failed to write deliveries to a JSON");
        }

    }

    /**
     * Creates the flightPath output file by appending each droneMove in the flightArray ArrayList to a String, then writing
     * to a JSON file in the resultfile folder.
     * @param date is the date given by the user
     */
    public void createFlightPathFile(String date){

        String record;
        ArrayList<String> printString = new ArrayList<>();

        for (DroneMove move: flightArray) {

            record = "{\"orderNo\":\"" + move.getOrderNo() + "\",\"fromLongitude\":" + move.getFromPosition().lng() +
                    ",\"fromLatitude\":" + move.getFromPosition().lat() + ",\"angle\":" +
                    move.getAngle() + ", \"toLongitude\":" + move.getToPosition().lng() + ", \"toLatitude\": " +
                    move.getToPosition().lat() + "}";

            printString.add(record);
        }

        try (FileWriter fileWriter = new FileWriter(".\\resultfiles\\flightpath-" + date + ".json")){
            fileWriter.write(String.valueOf(printString));
        }catch(Exception e){
            throw new RuntimeException("Failed to write flightpath to a JSON");
        }
    }

    /**
     * Creates the drone file by creating points from the flightArray, which are then fed into a LineString, which is
     * used to make a feature, which then adds to a feature collection, which then writes to an output file in the
     * resultfiles folder
     * @param date is the date given by the user
     */
    public void createDroneFile(String date){

        ArrayList<Point> pointList = new ArrayList<>();

        for (DroneMove move : flightArray){
            double lng = move.getToPosition().lng();
            double lat = move.getToPosition().lat();
            Point position = Point.fromLngLat(lng, lat);
            pointList.add(position);
        }

        Geometry geometry = LineString.fromLngLats(pointList);
        Feature feature = Feature.fromGeometry(geometry);
        FeatureCollection featureCollection = FeatureCollection.fromFeature(feature);
        String fcJson = featureCollection.toJson();

        try (FileWriter fileWriter = new FileWriter(".\\resultfiles\\drone-" + date + ".json")){
            fileWriter.write(fcJson);
        }catch(Exception e){
            throw new RuntimeException("Failed to write flightpath to GeoJSON");
        }
    }

    /**
     * If the resultfiles directory does not exist, it is created, and then the three functions above are run, which
     * creates the output files for this project.
     * @param date is the date given to the user
     */
    public void createResultFiles(String date){
        try {
            Path path = Paths.get("./resultfiles");
            Files.createDirectories(path);
        }catch(Exception e){
            throw new RuntimeException("Error creating/accessing result files directory " + e.getMessage());
        }

        createDeliveriesFile(date);
        createFlightPathFile(date);
        createDroneFile(date);
    }
}
