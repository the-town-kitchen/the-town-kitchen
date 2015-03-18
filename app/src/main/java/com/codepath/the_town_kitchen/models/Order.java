package com.codepath.the_town_kitchen.models;

/**
 * Created by paulina on 3/7/15.
 */

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

    private Long uid; // unique id for an order

    private double cost;

    private String deliveryLocation;

    private User user;

    private String date;

    private String time;

    private Feedback feedback;


    private int quantity;

    private ArrayList<OrderItem> orderItems;

    public Long getUid() {
        return getLong("uid");
    }

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

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setUid(Long uid) {
        put("uid", uid);
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

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
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
    public static void getOrderByDateWithoutItems(String date, final IParseOrderReceivedListener orderReceivedListener) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
            query.whereEqualTo("date", date);
            query.whereEqualTo("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
            query.orderByDescending("createdAt");
            // Execute query for order asynchronously
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                public void done(final ParseObject order, ParseException e) {
                    if (e == null || e.getCode() == 101) {

                        orderReceivedListener.handle(order, null);

                    }

                }

        });


    }


    public static void getOrderByDate(String date, final IOrderReceivedListener orderReceivedListener) {

        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        query.whereEqualTo("date", date);
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

    public static void getOrderByDate(final String date, final IParseOrderReceivedListener orderReceivedListener) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Order");
        query.whereEqualTo("date", date);
        query.whereEqualTo("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
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
                    parseOrder.saveInBackground();
                    parseOrder.pinInBackground();
                    getOrderByDate(date, orderReceivedListener);
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

    public static void update(final String date, final Meal meal, final int count) {

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

                if (!isMealInCart) {
                    createNewOrderItem(parseOrder, currentItemCost, currentItemCount, meal);
                }


                updateOrder(parseOrder, totalCost, orderCount, date);

            }
        };
        Order.getOrderByDate(date, orderReceivedListener);

    }

    private static void updateOrder(ParseObject parseOrder, double totalCost, int orderCount, String date) {
        parseOrder.put("cost", totalCost);
        parseOrder.put("quantity", orderCount);
        parseOrder.put("date", date);
        parseOrder.put("user", TheTownKitchenApplication.getCurrentUser().getUser());
        parseOrder.put("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
        parseOrder.saveInBackground();
        parseOrder.pinInBackground();
    }

    private static void createNewOrderItem(ParseObject parseOrder, double currentItemCost, int currentItemCount, Meal meal) {
        ParseObject newOrderItem = ParseObject.create("OrderItem");
        newOrderItem.put("meal", meal);
        newOrderItem.put("quantity", currentItemCount);
        newOrderItem.put("cost", currentItemCost);
        newOrderItem.put("parent", parseOrder.getObjectId());

        newOrderItem.pinInBackground();
        newOrderItem.saveInBackground();
    }
}
