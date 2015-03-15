package com.codepath.the_town_kitchen.models;

/**
 * Created by paulina on 3/7/15.
 */

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Order")
public class Order extends ParseObject {

    private Long uid; // unique id for an order

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setDeliveryLocation(String deliveryLocation) {
        this.deliveryLocation = deliveryLocation;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    private double cost;

    private String deliveryLocation;

    private User user;

    private String date;

    private String time;

    private Feedback feedback;

    private ArrayList<OrderItem> orderItems;

    public Long getUid() {
        return uid;
    }

    public double getCost() {
        return cost;
    }

    public String getDeliveryLocation() {
        return deliveryLocation;
    }

    public User getUser() {
        return user;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(ArrayList<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Order() {
        super();

    }

    public static ArrayList<Order> fromJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() < 1) return null;
        ArrayList<Order> orders = new ArrayList<>(jsonArray.length());
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject resultJson = null;
                try {
                    resultJson = jsonArray.getJSONObject(i);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                Order order = Order.fromJson(resultJson);

                if (order != null) {
                    orders.add(order);
                }

            }
            ActiveAndroid.setTransactionSuccessful();
            return orders;
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static Order fromJson(JSONObject jsonObject) {
        Order order = new Order();
        try {
            order.uid = jsonObject.getLong("uid");
            order.uid = jsonObject.has("uid") ? jsonObject.getLong("uid") : 0;
            order.cost = jsonObject.has("cost") ? jsonObject.getDouble("cost") : 0.0;
            order.deliveryLocation = jsonObject.has("deliveryLocation") ? jsonObject.getString("deliveryLocation") : "";
            // TODO: get Google Person object?
//            order.user = jsonObject.has("user") ? User.fromJson(jsonObject.getJSONObject("user")): null;
            order.date = jsonObject.has("date") ? jsonObject.getString("date") : "";
            order.time = jsonObject.has("time") ? jsonObject.getString("time") : "";
            order.feedback = jsonObject.has("feedback") ? Feedback.fromJson(jsonObject.getJSONObject("feedback")): null;
            order.save();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return order;
    }

    private static Order instance;
    public static Order getInstance() {
        if(instance== null){
            instance = new Order();

        }
        return instance;

    }
}
