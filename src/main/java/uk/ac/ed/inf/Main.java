package uk.ac.ed.inf;

public class Main {

    public static String error;
    public static void main(String[] args){

        if (args.length < 2){
            error = "User must provide a date and a URL, in this order";
            System.err.println(error);
            System.exit(0);
        }
        App.app(args);

    }
}