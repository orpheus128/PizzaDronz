package uk.ac.ed.inf;

public class Main {
    public static void main(String[] args){


        if (args.length < 2){
            System.err.println("User must provide a date and a URL, in this order");
            System.exit(0);
        }
        App.app(args);

    }

}