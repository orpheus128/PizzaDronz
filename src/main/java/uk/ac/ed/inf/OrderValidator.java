package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class OrderValidator implements uk.ac.ed.inf.ilp.interfaces.OrderValidation {
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        /**
         * validates an order by checking specific aspects of it
         * if any of these aspects are erroneous an associated error code will be returned which indicates the error
         * @param orderToValidate is the order which we are inspecting
         * @param definedRestaurants is a list of restaurants which are participating in the PizzaDronz service
         */

        CreditCardInformation creditCard = orderToValidate.getCreditCardInformation();

        if (checkCCNInvalid(creditCard)){
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            return orderToValidate;
        }

        if (checkCVVInvalid(creditCard)){
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            return orderToValidate;
        }

        if (checkExpiryDateInvalid(orderToValidate, creditCard)){
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            return orderToValidate;
        }

        OrderValidationCode pizzaCode = checkPizza(orderToValidate, definedRestaurants);
        if (pizzaCode != OrderValidationCode.UNDEFINED){
            orderToValidate.setOrderValidationCode(pizzaCode);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);

        return orderToValidate;
    }

    public Boolean checkCCNInvalid(CreditCardInformation creditCard){
        /**
         * returns true if there are issues with the credit card number, and false if there are not
         * @param creditCard is the credit card from the order which we extract the credit card number from
         */
        String ccn = creditCard.getCreditCardNumber();
        if (ccn == null){
            return true;
        }
        ccn = ccn.replaceAll("\\s", "");

        if (ccn.length() != 16){
            return true;
        }
        for (char c : ccn.toCharArray()){
            if (!Character.isDigit(c)){
                return true;
            }
        }
        return false;
    }

    public Boolean checkCVVInvalid(CreditCardInformation creditCard){
        /**
         * returns true if there are issues with the credit card's cvv, and false if there are not
         * @param creditCard is the credit card which we extract the cvv from
         */
        String cvv = creditCard.getCvv();

        if (cvv == null){
            return true;
        }
        cvv = cvv.replaceAll("//s", "");

        if (cvv.length() != 3){
            return true;
        }
        for (char c : cvv.toCharArray()) {
            if (!Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    public Boolean checkExpiryDateInvalid(Order orderToValidate, CreditCardInformation creditCard){
        /**
         * returns true if the expiry date is invalid for any reason, such as being in the past or
         * being in the wrong format. returns false if there are no issues with the cvv
         * @param orderToValidate is the order with a date which we are checking the expiry date against
         * @param creditCard is the credit card we are extracting the expiry date from
         */

        String expiry = creditCard.getCreditCardExpiry();
        if (expiry == null){
            return true;
        }
        if (!expiry.matches("(0[1-9]|1[0-2])/[0-9][0-9]")){
            return true;
        }

        String[] splitExpiry = expiry.split("/");

        String orderYear = String.valueOf(orderToValidate.getOrderDate().getYear());
        int orderYearInt = parseInt(orderYear.substring(2));
        int orderMonth = java.time.LocalDate.now().getMonth().getValue();

        if (parseInt(splitExpiry[1]) < orderYearInt){
            return true;
        }else if(parseInt(splitExpiry[0]) < orderMonth & parseInt(splitExpiry[1]) == orderYearInt){
            return true;
        }

        return false;
    }

    
    //The following code 
    public OrderValidationCode checkPizza(Order orderToValidate, Restaurant[] definedRestaurants){
        /**
         * loops through the menus of every restaurant until finding the first pizza from an order
         * once this pizza is found, the restaurant is saved, and then the other pizzas are searched for
         * if no pizzas are found, then an error is thrown
         * if the pizzas come from different restaurants, a separate error is thrown.
         * if neither of these are true, a further function is used to check for more errors
         * if no errors are found, the default code is returned to the main function
         * @param orderToValidate is the order the pizzas are taken from
         * @param definedRestaurant is the list of restaurants which menus are taken from
         */

        uk.ac.ed.inf.ilp.data.Pizza[] pizzasOrdered = orderToValidate.getPizzasInOrder();
        Restaurant restaurant = null;
        boolean pizzaFound;
        int menuCount;
        int restCount;
        int pizzaCount = 0;
        Restaurant tempRestaurant = null;
        int total = 100;
        uk.ac.ed.inf.ilp.data.Pizza[] menu;

        while (pizzaCount < pizzasOrdered.length) {
            pizzaFound = false;
            restCount = 0;
            while (restCount < definedRestaurants.length & !pizzaFound) {
                menu = definedRestaurants[restCount].menu();
                menuCount = 0;
                while (!pizzaFound & menuCount < menu.length) {

                    if (pizzasOrdered[pizzaCount].equals(menu[menuCount])){
                        pizzaFound = true;
                        tempRestaurant = definedRestaurants[restCount];
                        total += pizzasOrdered[pizzaCount].priceInPence();
                        if (pizzaCount == 0){
                            restaurant = tempRestaurant;
                        }
                    }
                    menuCount++;
                }
                restCount++;
            }
            if (!pizzaFound){
                return OrderValidationCode.PIZZA_NOT_DEFINED;
            }
            if (!restaurant.equals(tempRestaurant)){
                return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
            }
            pizzaCount++;
        }

        OrderValidationCode extraPizzaCode = checkPizzaFurther(restaurant, orderToValidate,pizzasOrdered, total);

        if (extraPizzaCode != OrderValidationCode.UNDEFINED){
            return extraPizzaCode;
        }

        //the function will return UNDEFINED to show that no errors have been found within the pizza information
        return OrderValidationCode.UNDEFINED;
    }

    OrderValidationCode checkPizzaFurther(Restaurant restaurant, Order orderToValidate, Pizza[] pizzasOrdered, int total){
        /**
         * checks for more errors which could occur
         * checks if the restaurant was open when the order was placed
         * checks if there are no more than 4 pizzas ordered
         * checks if the total amount is correct
         * if all of these checks pass, then the default code is returned
         * @param restaurant is the restaurant that is ordered from
         * @param orderToValidate is the order we extract the orderDate from
         * @param pizzasOrdered is a list of the pizzas ordered
         * @param total is a computed running total of the pizza's prices from the menu
         */
        if (!Arrays.asList(restaurant.openingDays()).contains(orderToValidate.getOrderDate().getDayOfWeek())){
            return OrderValidationCode.RESTAURANT_CLOSED;
        }
        if (pizzasOrdered.length > 4){
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        }
        if (total != orderToValidate.getPriceTotalInPence()){
            return OrderValidationCode.TOTAL_INCORRECT;
        }
        return OrderValidationCode.UNDEFINED;
    }

}
