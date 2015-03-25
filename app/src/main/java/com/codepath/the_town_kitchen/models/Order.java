package com.codepath.the_town_kitchen.models;

import android.util.Log;

import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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
        put("email", email);
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

    public boolean getIsDelivered() {
        return getBoolean("isDelivered");
    }

    public void setIsDelivered(boolean isDelivered) {
        put("isDelivered", isDelivered);
    }

    public int getFeedbackRating() {
        return getInt("feedbackRating");
    }

    public void setFeedbackRating(int feedbackRating) {
        put("feedbackRating", feedbackRating);
    }

    public boolean getIsPlaced() {
        return getBoolean("isPlaced");
    }

    public void setIsPlaced(boolean isPlaced) {
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

    public static void getLastOrderByDateWithoutItems(String date, final IOrderReceivedListener orderReceivedListener) {

        ParseQuery<Order> query = ParseQuery.getQuery("Order");
        query.whereEqualTo("date", date);
        query.whereEqualTo("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
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


    public static void getLastOrderByDate(String date, final IOrderReceivedListener orderReceivedListener) {
        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        query.whereEqualTo("date", date);
        query.whereEqualTo("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
        query.orderByDescending("createdAt");
        // Execute query for order asynchronously
        queryOrder(orderReceivedListener, query);
    }

    public static void getUsersLastOrder(final IOrderReceivedListener orderReceivedListener) {

        ParseQuery<Order> query = ParseQuery.getQuery(Order.class);
        query.whereEqualTo("email", TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
        query.orderByDescending("createdAt");
        // Execute query for order asynchronously
        queryOrder(orderReceivedListener, query);


    }

    private static void queryOrder(final IOrderReceivedListener orderReceivedListener, ParseQuery<Order> query) {
        query.getFirstInBackground(new GetCallback<Order>() {
            public void done(final Order order, ParseException e) {
                if (e == null || e.getCode() == 101) {
                    if (order != null) {
                        ParseQuery<OrderItem> query = ParseQuery.getQuery(OrderItem.class);
                        query.whereEqualTo("parent", order.getObjectId());
                        query.orderByDescending("createdAt");
                        query.findInBackground(new FindCallback<OrderItem>() {
                            public void done(List<OrderItem> orderItems, ParseException e) {
                                if (e == null || e.getCode() == 101) {
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

    public interface IOrderReceivedListener {
        void handle(Order order, List<OrderItem> orderItems);
    }


    public static Order createNewOrder(final IOrderReceivedListener orderReceivedListener) {
        Order newOrder = new Order();
        newOrder.setDate(TheTownKitchenApplication.orderDate);
        newOrder.setUser(TheTownKitchenApplication.getCurrentUser().getUser());
        newOrder.setIsPlaced(false);
        newOrder.setIsDelivered(false);
        newOrder.setOrderItems(null);
        newOrder.setEmail(TheTownKitchenApplication.getCurrentUser().getUser().getEmail());
        newOrder.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                getLastOrderByDateWithoutItems(TheTownKitchenApplication.orderDate, orderReceivedListener);
            }
        });
        return newOrder;
    }

    public static OrderItem createNewOrderItem(String parentId, double currentItemCost, int currentItemCount, Meal meal) {

        OrderItem newOrderItem = new OrderItem();

        newOrderItem.setMeal(meal);
        newOrderItem.setQuantity(currentItemCount);
        newOrderItem.setCost(currentItemCost);
        newOrderItem.setParent(parentId);

        newOrderItem.saveInBackground();
        return newOrderItem;
    }


    public void update(Meal meal, int count){
        OrderItem newOrderItem = null;
        if(this.getOrderItems() == null || getOrderItems().size() == 0){
            newOrderItem = createNewOrderItem(this.getObjectId(), meal.getPrice() * count, count, meal);

            List<OrderItem> orderItems = new ArrayList<>();
            orderItems.add(newOrderItem);
            this.setOrderItems(orderItems);
            this.setQuantity(count);
            this.setCost(meal.getPrice() * count);
            return;
        }
        boolean isMealInCart = false;
        double totalCost = meal.getPrice() * count;
        int orderCount = count;
        for(OrderItem item: this.getOrderItems()){
            if (item.getMeal().getObjectId() == meal.getObjectId()) {
                item.setQuantity(count);
                item.setCost(totalCost);
                item.saveInBackground();
                isMealInCart = true;
        }else {
                totalCost += item.getQuantity() * item.getMeal().getPrice();
                orderCount += item.getQuantity();
            }
        }

        if(!isMealInCart){
            newOrderItem = createNewOrderItem(this.getObjectId(), totalCost, orderCount, meal);
            this.getOrderItems().add(newOrderItem);
        }
        this.setQuantity(orderCount);
        this.setCost(totalCost);
    }

    public static void getOrderById(String id, final IOrderReceivedListener orderReceivedListener) {
        ParseQuery<Order> query = ParseQuery.getQuery("Order");
        query.getInBackground(id, new GetCallback<Order>() {
            public void done(Order order, ParseException e) {
                if (e == null) {

                    orderReceivedListener.handle(order, null);
                    
                } else {
                    // something went wrong
                }
            }
        });
    }

    private Order order;

    public void setCurrentOrder(Order order) {
        this.order = order;
    }

    public Order getCurrentOrder() {
        return this.order;

    }
}
