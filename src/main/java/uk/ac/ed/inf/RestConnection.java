package uk.ac.ed.inf;
import com.google.gson.*;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateSerializer;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class RestConnection {

   public void getOrderData(){
       URI restServer = establishConnection("https://ilp-rest.azurewebsites.net/orders");


       HttpClient client = HttpClient.newHttpClient();
       HttpRequest request = HttpRequest.newBuilder().uri(restServer).build();
       String response;
       try {
           response = client.send(request, HttpResponse.BodyHandlers.ofString()).body().toString();
       } catch (IOException e) {
           throw new RuntimeException(e);
       } catch (InterruptedException e) {
           throw new RuntimeException(e);
       }

       GsonBuilder builder = new GsonBuilder();
       builder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
       Gson gson = builder.create();

       Order[] testObject = gson.fromJson(response, Order[].class);

       System.out.println("/nJSON data in string format");
       System.out.println(testObject[0]);

       //need to deserialise the date specially somehow
   }
       public URI establishConnection(String uri){

        try{
           return new URI(uri);
        }catch(Exception MalformedURLException){
            return null;
        }
   }
}