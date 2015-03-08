package com.codepath.the_town_kitchen.models;

/**
 * Created by paulina on 3/7/15.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.codepath.the_town_kitchen.models.Feedback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Table(name = "order")

public class Order extends Model implements Parcelable {

    @Column(name = "uid", index = true, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private Long uid; // unique id for an order

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

    public Order() {
        super();

    }

    public Order(Parcel in) {
        uid = in.readLong();
        cost = in.readDouble();
        deliveryLocation = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        date = in.readString();
        time = in.readString();
        feedback = in.readParcelable(Feedback.class.getClassLoader());
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
            order.uid = jsonObject.has("uid") ? jsonObject.getLong("uid") : 0;
            order.cost = jsonObject.has("cost") ? jsonObject.getDouble("cost") : 0.0;
            order.deliveryLocation = jsonObject.has("deliveryLocation") ? jsonObject.getString("deliveryLocation") : "";
            // TODO: get Google Person object?
            order.user = jsonObject.has("user") ? User.fromJson(jsonObject.getJSONObject("user")): null;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeLong(uid);
        dest.writeDouble(cost);
        dest.writeString(deliveryLocation);
        dest.writeParcelable(user, i);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeParcelable(feedback, i);
    }

    public static final Parcelable.Creator<Order> CREATOR
            = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
