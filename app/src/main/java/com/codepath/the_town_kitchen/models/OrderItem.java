package com.codepath.the_town_kitchen.models;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;


    @Table(name = "order_item")

    public class OrderItem extends Model {

        @Column(name = "uid", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
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

        @Column(name = "cost")
        private double cost;


        @Column(name = "meal", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
        private Meal meal;


        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        @Column(name = "quantity")
        private int quantity;

        public static OrderItem orderItemFromClick(Meal meal, int quantity){
            OrderItem orderItem = new OrderItem();
            orderItem.meal = meal;
            orderItem.quantity = quantity;
            orderItem.save();
            return orderItem;
        }
    }
