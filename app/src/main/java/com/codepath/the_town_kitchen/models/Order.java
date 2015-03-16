package com.codepath.the_town_kitchen.models;

/**
 * Created by paulina on 3/7/15.
 */

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.ArrayList;

@ParseClassName("Order")
public class Order extends ParseObject {

    private Long uid; // unique id for an order

    private double cost;

    private String deliveryLocation;

    private User user;

    private String date;

    private String time;

    private Feedback feedback;

    private ArrayList<OrderItem> orderItems;

    public Long getUid() { return getLong("uid"); }

    public double getCost() { return getDouble("cost"); }

    public String getDeliveryLocation() { return getString("deliveryLocation"); }

    public User getUser() { return (User) getParseObject("user"); }

    public String getDate() { return getString("date"); }

    public String getTime() { return getString("time"); }

    public Feedback getFeedback() { return (Feedback) getParseObject("feedback"); }

    public ArrayList<OrderItem> getOrderItems() { return orderItems; }

    public void setUid(Long uid) { put("uid", uid); }

    public void setCost(double cost) { put("cost", cost); }

    public void setDeliveryLocation(String deliveryLocation) { put("deliveryLocation", deliveryLocation); }

    public void setUser(User user) { put("user", user); }

    public void setDate(String date) { put("date", date); }

    public void setTime(String time) { put("time", time); }

    public void setFeedback(Feedback feedback) { put("feedback", feedback); }

    public void setOrderItems(ArrayList<OrderItem> orderItems) { this.orderItems = orderItems; }

    public Order() { super(); }

    private static Order instance;
    public static Order getInstance() {
        if(instance== null){
            instance = new Order();

        }
        return instance;

    }
}
