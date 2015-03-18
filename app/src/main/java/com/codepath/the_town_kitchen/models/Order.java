package com.codepath.the_town_kitchen.models;

import android.util.Log;

import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Order")
public class Order extends ParseObject {

    private double cost;

    private String deliveryLocation;

    private int feedbackRating;

    private boolean isDelivered;
    private boolean isPlaced;
    private User user;

    private String date;

    private String time;

    private Feedback feedback;


    private int quantity;

    public String getEmail() {
        return getString("email");
    }

    public void setEmail(String email) {
        put("email",   email);
    }

    private String email;
    private List<OrderItem> orderItems;

    public double getCost() {
        return getDouble("cost");
    }

    public String getDeliveryLocation() {
        return getString("deliveryLocation");
    }

    public User getUser() {
        return (User) getParseObject("user");
    }

    public String getDate() {
        return getString("date");
    }

    public String getTime() {
        return getString("time");
    }

    public int getQuantity() {
        return getInt("quantity");
    }

    public Feedback getFeedback() {
        return (Feedback) getParseObject("feedback");
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setCost(double cost) {
        put("cost", cost);
    }

    public void setDeliveryLocation(String deliveryLocation) {
        put("deliveryLocation", deliveryLocation);
    }

    public void setUser(User user) {
        put("user", user);
    }

    public void setDate(String date) {
        put("date", date);
    }

    public void setTime(String time) {
        put("time", time);
    }

    public void setFeedback(Feedback feedback) {
        put("feedback", feedback);
    }


    public void setQuantity(int quantity) {
        put("quantity", quantity);
    }

    public boolean getIsDelivered(){
        return getBoolean("isDelivered");
    }

    public void setIsDelivered(boolean isDelivered){
        put("isDelivered", isDelivered);
    }

    public int getFeedbackRating() {
        return getInt("feedbackRating");
    }

    public void setFeedbackRating(int feedbackRating) {
        put("feedbackRating", feedbackRating);
    }

    public boolean getIsPlaced(){
        return getBoolean("isDelivered");
    }

    public void setIsPlaced(boolean isPlaced){
        put("isPlaced", isPlaced);
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Order() {
        super();
    }

    private static Order instance;

    public static Order getInstance() {
        if (instance == null) {
            instance = new Order();

        }
        return instance;
    }

    public static void getOrderByDateWithoutItems(String date, final IOrderReceivedListener orderReceivedListener, boolean isPlaced) {

            ParseQuery<Order> query = ParseQuery.getQuery("Order");
            query.whereEqualTo("date", date);
            query.whereEqualTo("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
            query.whereEqualTo("isPlaced", isPlaced);
            query.orderByDescending("createdAt");
            // Execute query for order asynchronously
            query.getFirstInBackground(new GetCallback<Order>() {
            public void done(final Order order, ParseException e) {
                    if (e == null || e.getCode() == 101) {
                        orderReceivedListener.handle(order, null);
                    }

                }
        });
    }

    public static void getOrderByDateWithoutItems(String date, final IOrderReceivedListener orderReceivedListener) {
       getOrderByDateWithoutItems(date, orderReceivedListener, false);
    }

    public static void getOrderByDate(final String date, final IOrderReceivedListener orderReceivedListener, boolean isPlaced){
        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        query.whereEqualTo("date", date);
        query.whereEqualTo("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
        query.whereEqualTo("isPlaced", isPlaced);
        query.orderByDescending("createdAt");
        // Execute query for order asynchronously
        query.getFirstInBackground(new GetCallback<Order>() {
            public void done(final Order order, ParseException e) {
                if (e == null) {
                    if (order != null) {
                        ParseQuery<OrderItem> query = ParseQuery.getQuery(OrderItem.class);
                        query.whereEqualTo("parent", order.getObjectId());
                        query.orderByDescending("createdAt");
                        query.findInBackground(new FindCallback<OrderItem>() {
                            public void done(List<OrderItem> orderItems, ParseException e) {
                                if (e == null) {
                                    orderReceivedListener.handle(order, orderItems);
                                } else {
                                    Log.d("Order", "Error: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        orderReceivedListener.handle(order, null);
                    }

                }
                else if(e.getCode() == 101){
                    //no order for the day, create an empty one
                    Order newOrder = new Order();
                    newOrder.setDate(date);
                    newOrder.setUser(TheTownKitchenApplication.getCurrentUser().getUser());
                    newOrder.setIsPlaced(false);
                    newOrder.setIsDelivered(false);
                    newOrder.setEmail(TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
                    newOrder.saveInBackground();

                    getOrderByDate(date, orderReceivedListener, false);
                }
            }
        });
    }

    public static void getOrderByDate(String date, final IOrderReceivedListener orderReceivedListener) {
        getOrderByDate(date, orderReceivedListener, false);

    }

    public static void getParseOrderByDate(final String date, final IParseOrderReceivedListener orderReceivedListener, boolean isPlaced) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
        query.whereEqualTo("date", date);
        query.whereEqualTo("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
        query.whereEqualTo("isPlaced", isPlaced);
        query.orderByDescending("createdAt");
        // Execute query for order asynchronously
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(final ParseObject order, ParseException e) {
                if (e == null) {
                    if (order != null) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("OrderItem");
                        query.whereEqualTo("parent", order.getObjectId());

                        query.orderByDescending("createdAt");
                        query.findInBackground(new FindCallback<ParseObject>() {
                            public void done(List<ParseObject> orderItems, ParseException e) {
                                if (e == null) {
                                    orderReceivedListener.handle(order, orderItems);
                                } else {
                                    Log.d("Order", "Error: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        orderReceivedListener.handle(order, null);
                    }
                }
                else if(e.getCode() == 101){
                    //no order for the day, create an empty one
                    ParseObject parseOrder = ParseObject.create("Order");
                    parseOrder.put("date", date);
                    parseOrder.put("user", TheTownKitchenApplication.getCurrentUser().getUser());
                    parseOrder.put("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
                    parseOrder.put("isPlaced",false);
                    parseOrder.put("isDelivered",false);
                    parseOrder.saveInBackground();
                    parseOrder.pinInBackground();
                    getParseOrderByDate(date, orderReceivedListener, false);
                }
            }
        });

    }



    public static void getParseOrderByDate(final String date, final IParseOrderReceivedListener orderReceivedListener) {
        getParseOrderByDate(date, orderReceivedListener, false);

    }

    public static void getUsersLastOrder(final IOrderReceivedListener orderReceivedListener) {

        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        query.whereEqualTo("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());

        query.orderByDescending("createdAt");
        // Execute query for order asynchronously
        query.getFirstInBackground(new GetCallback<Order>() {
            public void done(final Order order, ParseException e) {
                if (e == null) {
                    if (order != null) {
                        ParseQuery<OrderItem> query = ParseQuery.getQuery(OrderItem.class);
                        query.whereEqualTo("parent", order.getObjectId());
                        query.orderByDescending("createdAt");
                        query.findInBackground(new FindCallback<OrderItem>() {
                            public void done(List<OrderItem> orderItems, ParseException e) {
                                if (e == null) {
                                    orderReceivedListener.handle(order, orderItems);
                                } else {
                                    Log.d("Order", "Error: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        orderReceivedListener.handle(order, null);
                    }

                }
            }
        });


    }

    public interface IParseOrderReceivedListener {
        void handle(ParseObject order, List<ParseObject> orderItems);
    }

    public interface IOrderReceivedListener {
        void handle(Order order, List<OrderItem> orderItems);
    }

    public interface IOrderUpdatedListener {
        void handle(int count);
    }

/*
    public static void update(final String date, final Meal meal, final int count, final IOrderUpdatedListener orderUpdatedListener) {

        IParseOrderReceivedListener orderReceivedListener = new IParseOrderReceivedListener() {
            @Override
            public void handle(ParseObject parseOrder, List<ParseObject> parseOrderItems) {
                //  ParseObject newOrder;// = ParseObject.create("Order");
                ParseObject newOrderItem;// = ParseObject.create("OrderItem");

                //ArrayList<OrderItem> orderItems;
                boolean isMealInCart = false;

                double currentItemCost =  meal.getPrice() * count;
                int currentItemCount = count;

                double totalCost = currentItemCost;
                int orderCount = currentItemCount;
                if (parseOrder == null || parseOrderItems == null || parseOrderItems.size() == 0) {
                    if (parseOrder == null) {
                        parseOrder = ParseObject.create("Order");
                    }
                    orderUpdatedListener.handle(count);
                    createNewOrderItem(parseOrder, currentItemCost, currentItemCount, meal);
                    updateOrder(parseOrder, totalCost, orderCount, date);
                    return;
                }


                for (ParseObject parseOrderItem : parseOrderItems) {
                    //if meal exists in cart already
                    if (parseOrderItem.getParseObject("meal").getObjectId() == meal.getObjectId()) {
                        parseOrderItem.put("quantity", currentItemCount);
                        parseOrderItem.put("cost", currentItemCost);
                        parseOrderItem.pinInBackground();
                        parseOrderItem.saveInBackground();
                        isMealInCart = true;

                    }else {
                        totalCost += parseOrderItem.getInt("quantity") * parseOrderItem.getParseObject("meal").getDouble("price");//.getQuantity() * orderItem.getMeal().getPrice();/
                        orderCount += parseOrderItem.getInt("quantity");
                    }
                }
                orderUpdatedListener.handle(orderCount);
                if (!isMealInCart) {
                    createNewOrderItem(parseOrder, currentItemCost, currentItemCount, meal);
                }


                updateOrder(parseOrder, totalCost, orderCount, date);

            }
        };
        Order.getOrderByDate(date, orderReceivedListener);

    }

*/
    public static void update(final String date, final Meal meal, final int count, final IOrderUpdatedListener orderUpdatedListener) {

        IOrderReceivedListener orderReceivedListener = new IOrderReceivedListener() {
            @Override
            public void handle(Order parseOrder, List<OrderItem> parseOrderItems) {
                //  ParseObject newOrder;// = ParseObject.create("Order");
                final OrderItem newOrderItem;// = ParseObject.create("OrderItem");

                //ArrayList<OrderItem> orderItems;
                boolean isMealInCart = false;

                double currentItemCost =  meal.getPrice() * count;
                int currentItemCount = count;

                double totalCost = currentItemCost;
                int orderCount = currentItemCount;
                if (parseOrder == null || parseOrderItems == null || parseOrderItems.size() == 0) {
                    if (parseOrder == null) {
                        parseOrder = new Order();// ParseObject.create("Order");
                    }
                    orderUpdatedListener.handle(count);
                    newOrderItem = createNewOrderItem(parseOrder, currentItemCost, currentItemCount, meal);
                    updateOrder(parseOrder, totalCost, orderCount, date);
                    List<OrderItem> orderItems =new ArrayList<>();
                    orderItems.add(newOrderItem);
                    parseOrder.setOrderItems(orderItems);
                    TheTownKitchenApplication.getOrder().setCurrentOrder(parseOrder);
                    return;
                }


                for (OrderItem parseOrderItem : parseOrderItems) {
                    //if meal exists in cart already
                    if (parseOrderItem.getMeal().getObjectId() == meal.getObjectId()) {
                        parseOrderItem.setQuantity(currentItemCount);
                        parseOrderItem.setCost(currentItemCost);
                        //parseOrderItem.put("quantity", currentItemCount);
                       // parseOrderItem.put("cost", currentItemCost);
                       // parseOrderItem.pinInBackground();
                        parseOrderItem.saveInBackground();
                        isMealInCart = true;

                    }else {
                        totalCost += parseOrderItem.getQuantity() * parseOrderItem.getMeal().getPrice();//getInt("quantity") * parseOrderItem.getParseObject("meal").getDouble("price");//.getQuantity() * orderItem.getMeal().getPrice();/
                        orderCount += parseOrderItem.getQuantity();//.getInt("quantity");
                    }
                }
                orderUpdatedListener.handle(orderCount);
                if (!isMealInCart) {
                    createNewOrderItem(parseOrder, currentItemCost, currentItemCount, meal);
                }
                updateOrder(parseOrder, totalCost, orderCount, date);
                parseOrder.setOrderItems(parseOrderItems);
                TheTownKitchenApplication.getOrder().setCurrentOrder(parseOrder);

            }
        };
        Order.getOrderByDate(date, orderReceivedListener);

    }

    private static void updateOrder(Order parseOrder, double totalCost, int orderCount, String date) {
        parseOrder.setCost(totalCost);
        parseOrder.setQuantity(orderCount);
        parseOrder.setDate(date);
        parseOrder.setUser(TheTownKitchenApplication.getCurrentUser().getUser());
        parseOrder.setEmail(TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
        //parseOrder.saveInBackground();
        parseOrder.pinInBackground();
    }

    private static OrderItem createNewOrderItem(Order parseOrder, double currentItemCost, int currentItemCount, Meal meal) {

        OrderItem newOrderItem = new OrderItem();// ParseObject.create("OrderItem");

        newOrderItem.setMeal(meal);
        newOrderItem.setQuantity(currentItemCount);
        newOrderItem.setCost(currentItemCost);
        newOrderItem.setParent(parseOrder.getObjectId());

        //newOrderItem.pinInBackground();
        newOrderItem.saveInBackground();
        return newOrderItem;
    }

    private Order order;

    public  void setCurrentOrder(Order order) {
        this.order = order;
    }

    public Order getCurrentOrder(){
        return this.order;

    }
}
