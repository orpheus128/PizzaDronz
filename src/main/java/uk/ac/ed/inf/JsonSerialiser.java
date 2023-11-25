package uk.ac.ed.inf;

import com.mapbox.geojson.*;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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
