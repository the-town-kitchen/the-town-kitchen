package com.codepath.the_town_kitchen.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

@ParseClassName("OrderItem")
public class OrderItem extends ParseObject {

    private Long uid; // unique id for an order

    private double cost;

    private Meal meal;

    private int quantity;

    public Long getUid() { return getLong("uid"); }

    public double getCost() { return getDouble("cost"); }

    public Meal getMeal() { return (Meal) getParseObject("meal"); }

    public int getQuantity() { return getInt("quantity"); }

    public void setUid(Long uid) { put("uid", uid); }

    public void setCost(double cost) { put("cost", cost); }

    public void setMeal(Meal meal) { put("meal", meal); }

    public void setQuantity(int quantity) { put("quantity", quantity); }

    public static OrderItem orderItemFromClick(Meal meal, int quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.meal = meal;
        orderItem.quantity = quantity;
        try {
            orderItem.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return orderItem;
    }
}
