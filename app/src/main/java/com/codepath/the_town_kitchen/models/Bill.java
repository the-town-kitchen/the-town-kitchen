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
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@Table(name = "bill")

public class Bill extends Model implements Parcelable {

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

    public Bill() {
        super();

    }

    public Bill(Parcel in) {
        uid = in.readLong();
        cost = in.readDouble();
        deliveryLocation = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
        date = in.readString();
        time = in.readString();
        feedback = in.readParcelable(Feedback.class.getClassLoader());
    }

    public static ArrayList<Bill> fromJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() < 1) return null;
        ArrayList<Bill> orders = new ArrayList<>(jsonArray.length());
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
                Bill order = Bill.fromJson(resultJson);

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

    public static Bill fromJson(JSONObject jsonObject) {
        Bill order = new Bill();
        try {
            Long currentOrderId = jsonObject.getLong("uid");
            Bill existingOrder = new Select().from(Bill.class)
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

    public static final Parcelable.Creator<Bill> CREATOR
            = new Parcelable.Creator<Bill>() {
        @Override
        public Bill createFromParcel(Parcel in) {
            return new Bill(in);
        }

        @Override
        public Bill[] newArray(int size) {
            return new Bill[size];
        }
    };

    public static ArrayList<Bill> fromCache() {
        ArrayList<Bill> alOrders = new ArrayList<>();
        List<Bill> orders = new Select()
                .from(Bill.class)
                .execute();
        alOrders.addAll(orders);
        return alOrders;
    }

    public static void deleteAll() {
        new Delete().from(Bill.class).execute();
    }
}
