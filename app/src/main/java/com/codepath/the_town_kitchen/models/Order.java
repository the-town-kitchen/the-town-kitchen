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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Table(name = "orders")

public class Order extends Model {

    @Column(name = "uid", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
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

    @Column(name = "cost")
    private double cost;

    @Column(name = "delivery_location")
    private String deliveryLocation;

    @Column(name = "user", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private User user;

    @Column(name = "date")
    private String date;

    @Column(name = "time")
    private String time;

    @Column(name = "feedback", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
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
            Long currentOrderId = jsonObject.getLong("uid");
            Order existingOrder = new Select().from(Order.class)
                    .where("uid = ?", currentOrderId)
                    .executeSingle();
            if (existingOrder != null) {
                order = existingOrder;
            }
            order.uid = currentOrderId;
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
        }
        return order;
    }

    public static ArrayList<Order> fromCache() {
        ArrayList<Order> alOrders = new ArrayList<>();
        List<Order> orders = new Select()
                .from(Order.class)
                .execute();
        alOrders.addAll(orders);
        return alOrders;
    }


    public static Order fromCacheByDate(String date) {
        
        Order order = (Order) new Select()
                .from(Order.class)
                .where("Date = ?", date)
                .executeSingle();
       
        return order;
    }

    public static void deleteAll() {
        new Delete().from(Order.class).execute();
    }
}
