package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.util.Arrays;

import static java.lang.Integer.parseInt;

public class OrderValidator implements uk.ac.ed.inf.ilp.interfaces.OrderValidation {
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {

        //credit card number (16 digits, use regex to strip down to just numbers) - done
        //cvv (3 digits) - done
        //expiration date
        //the menu items selected in the order
        //the involved restaurants
        //if the maximum count is exceeded
        //if the order is valid on the given date for the involved restaurants (opening days)

        CreditCardInformation creditCard = orderToValidate.getCreditCardInformation();

        String ccn = creditCard.getCreditCardNumber();
        ccn = ccn.replaceAll("\\s", "");

        if (ccn.length() != 16){
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        for (char c : ccn.toCharArray()){
            if (!Character.isDigit(c)){
                orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                return orderToValidate;
            }
        }

        String cvv = creditCard.getCvv();
        cvv = cvv.replaceAll("//s", "");

        if (cvv.length() != 3){
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        for (char c : cvv.toCharArray()) {
            if (!Character.isDigit(c)) {
                orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
                orderToValidate.setOrderStatus(OrderStatus.INVALID);
                return orderToValidate;
            }
        }

        String expiry = creditCard.getCreditCardExpiry();
        if (!expiry.matches("(0[1-9]|1[0-2])/[0-9][0-9]")){
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        String[] splitExpiry = expiry.split("/");

        String orderYear = String.valueOf(orderToValidate.getOrderDate().getYear());
        int orderYearInt = parseInt(orderYear.substring(2));
        int orderMonth = java.time.LocalDate.now().getMonth().getValue();

        if (parseInt(splitExpiry[1]) < orderYearInt){
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }else if(parseInt(splitExpiry[0]) < orderMonth & parseInt(splitExpiry[1]) == orderYearInt){
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        //checkPizza will check for pizza related errors, such as the maximum count being exceeded, or the pizza being
        //undefined, or the restaurant being closed
        OrderValidationCode code = checkPizza(orderToValidate, definedRestaurants);
        if (code != OrderValidationCode.UNDEFINED){
            orderToValidate.setOrderValidationCode(code);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }


        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);

        return orderToValidate;
    }

    public OrderValidationCode checkPizza(Order orderToValidate, Restaurant[] definedRestaurants){

        uk.ac.ed.inf.ilp.data.Pizza[] pizzasOrdered = orderToValidate.getPizzasInOrder();
        Restaurant restaurant = definedRestaurants[0];
        boolean pizzaFound = false;
        int menuCount = 0;
        int restCount = 0;
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

                    if (pizzasOrdered[pizzaCount].equals(menu[menuCount])) {
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
            if (restaurant != tempRestaurant){
                return OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;
            }
            pizzaCount++;
        }

        if (!Arrays.asList(restaurant.openingDays()).contains(orderToValidate.getOrderDate().getDayOfWeek())){
            return OrderValidationCode.RESTAURANT_CLOSED;
        }
        if (pizzasOrdered.length > 4){
            return OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;
        }
        if (total != orderToValidate.getPriceTotalInPence()){
            return OrderValidationCode.TOTAL_INCORRECT;
        }
        //the function will return UNDEFINED to show that no errors have been found within the pizza information
        return OrderValidationCode.UNDEFINED;
    }
}
