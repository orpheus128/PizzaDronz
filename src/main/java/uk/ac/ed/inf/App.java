package uk.ac.ed.inf;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        RestConnection newConnection = new RestConnection();

        newConnection.getOrderData();
    }
}
