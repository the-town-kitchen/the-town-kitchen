package com.codepath.the_town_kitchen.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;


@ParseClassName("OrderItem")
public class OrderItem extends ParseObject {

    private Long uid; // unique id for an order

    public Long getUid() {
        return uid;
    }

    public double getCost() {
        return cost;
    }

    public Meal getMeal() {
        return meal;
    }

    public int getQuantity() {
        return quantity;
    }

    private double cost;

    private Meal meal;

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Column(name = "quantity")
    private int quantity;

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
