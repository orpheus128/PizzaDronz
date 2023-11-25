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
import java.util.ArrayList;

public class RestConnection {

   public Order[] getOrderData(String[] args) {

       LocalDate date;
       try {
           date = LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
       } catch (Exception e) {
           throw new RuntimeException("Date is not in the correct format");
       }
       String response = serverResponse(args, "/orders/" + date);
       Gson gson = createBuilder();
       Order[] orderArray = gson.fromJson(response, Order[].class);

       return todaysOrders(orderArray, args);
   }

    public Restaurant[] getRestaurantData(String[] args){

        String response = serverResponse(args, "/restaurants");
        Gson gson = createBuilder();

        return gson.fromJson(response, Restaurant[].class);
    }

    public NamedRegion getCentralArea(String[] args){

        String response = serverResponse(args, "/centralArea");
        Gson gson = createBuilder();

        return gson.fromJson(response, NamedRegion.class);
    }

    public NamedRegion[] getNoFlyZones(String[] args){

        String response = serverResponse(args, "/noFlyZones");
        Gson gson = createBuilder();

        return gson.fromJson(response, NamedRegion[].class);
    }

    public Boolean isAlive(String[] args){
       String response = serverResponse(args, "/isAlive");
       Gson gson = createBuilder();

        return gson.fromJson(response, Boolean.class);
    }
    private URI establishConnection(String uri){
        try{
           return new URI(uri);
        }catch(Exception MalformedURLException){
            return null;
        }
   }

   private String serverResponse(String[] args, String extension){
       URI restServer = establishConnection(args[1].concat(extension));

       HttpClient client = HttpClient.newHttpClient();
       HttpRequest request = HttpRequest.newBuilder().uri(restServer).build();
       String serverResponse;
       try {
           serverResponse = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
       } catch (IOException | InterruptedException e) {
           throw new RuntimeException(e);
       }

       return serverResponse;
   }
   private Gson createBuilder(){
       return new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();
   }

   private Order[] todaysOrders(Order[] orderArray, String[] args){

       ArrayList<Order> orderList = new ArrayList<>();

//       System.out.println("All orders:");
       for (Order order: orderArray) {
           if (order.getOrderDate().equals(LocalDate.parse(args[0], DateTimeFormatter.ofPattern("yyyy-MM-dd")))){
               orderList.add(order);
           }
       }
       Order[] todayOrders = new Order[orderList.size()];
       return orderList.toArray(todayOrders);
   }
}

