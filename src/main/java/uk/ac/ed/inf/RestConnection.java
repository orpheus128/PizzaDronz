package uk.ac.ed.inf;
import com.google.gson.*;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RestConnection {

    /**
     * Accesses the given rest server to retrieve the orders for the specified date
     * @param args is the arguments passed in by the user
     * @return a list of the orders for the given date
     */
   public Order[] getOrderData(String[] args) {

       LocalDate date;
       try {
           date = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
       } catch (Exception e) {
           throw new RuntimeException("Date is not in the correct format");
       }
       String response = serverResponse(args, "/orders/" + date);
       Gson gson = createBuilder();
       Order[] orderArray = new Order[0];
        try{
            orderArray = gson.fromJson(response, Order[].class);
        }catch (Exception e){
            System.err.println("Could not find orders.JSON in the provided URL");
            System.exit(0);
        }

       return orderArray;
   }

    public Restaurant[] getRestaurantData(String[] args){

       String response = serverResponse(args, "/restaurants");

        Gson gson = createBuilder();
        try{
            return gson.fromJson(response, Restaurant[].class);
        }catch (Exception e){
            System.err.println("Could not find restaurants.JSON in the provided URL");
            System.exit(0);
        }
        return null;
    }

    public NamedRegion getCentralArea(String[] args){

       String response = serverResponse(args, "/centralArea");

       Gson gson = createBuilder();
       try{
           return gson.fromJson(response, NamedRegion.class);
       }catch (Exception e){
           System.err.println("Could not find centralArea.JSON in the provided URL");
           System.exit(0);
       }
       return null;
    }

    public NamedRegion[] getNoFlyZones(String[] args){

        String response = serverResponse(args, "/noFlyZones");
        Gson gson = createBuilder();
        try {
            return gson.fromJson(response, NamedRegion[].class);
        }catch (Exception e){
            System.err.println("Could not find noFlyZones.JSON in the provided URL");
            System.exit(0);
        }
        return null;
    }

    public Boolean isAlive(String[] args){

       String response = serverResponse(args, "/isAlive");
       Gson gson = createBuilder();
       try {
           return gson.fromJson(response, Boolean.class);
       }catch (Exception e){
           System.err.println("Could not find isAlive.JSON in the provided URL");
           System.exit(0);
       }
       return null;
    }

    /**
     * Creates the URI used to create a connection to the rest server
     * @param uri is the uri given by the user
     * @return a URI object
     */
    private URI establishConnection(String uri){
        try{
           return new URI(uri);
        }catch(Exception MalformedURLException){
            System.err.println("Given URL does not exist");
            System.exit(0);
        }
        return null;
   }

    /**
     * Retrieves the json file from the specified extension of the REST server
     * @param args is the list of arguments given by the user
     * @param extension is the specific file to be retrieved from the REST server
     * @return A string containing information gleamed from the JSON file
     */
   private String serverResponse(String[] args, String extension){
       URI restServer = establishConnection(args[1].concat(extension));

       HttpClient client = HttpClient.newHttpClient();
       HttpRequest request = null;

       try {
            request = HttpRequest.newBuilder().uri(restServer).build();
       }catch (Exception e){
           System.err.println("Given URL does not exist");
           System.exit(0);
       }
       String serverResponse = "";
       try {
           serverResponse = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
       } catch (IOException | InterruptedException e) {
           System.err.println("Given URL does not exist");
           System.exit(0);
       }

       return serverResponse;
   }

   private Gson createBuilder(){
       return new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();
   }
}

