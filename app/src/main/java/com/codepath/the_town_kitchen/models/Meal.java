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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@Table(name = "meal")

public class Meal extends Model implements Parcelable {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "order", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private Order order;

    @Column(name = "price")
    private double price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "date")
    private String date;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Order getOrder() {
        return order;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDate() {
        return date;
    }

    public Meal() {
        super();
    }

    public Meal(Parcel in) {
        name = in.readString();
        description = in.readString();
        order = in.readParcelable(Order.class.getClassLoader());
        price = in.readDouble();
        imageUrl = in.readString();
        quantity = in.readInt();
        date = in.readString();
    }

    public static ArrayList<Meal> fromJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() < 1) return null;
        ArrayList<Meal> meals = new ArrayList<>(jsonArray.length());
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
                Meal meal = Meal.fromJson(resultJson);

                if (meal != null) {
                    meals.add(meal);
                }

            }
            ActiveAndroid.setTransactionSuccessful();
            return meals;
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static Meal fromJson(JSONObject jsonObject) {
        Meal meal = new Meal();
        try {
            meal.name = jsonObject.has("name") ? jsonObject.getString("name") : "";
            meal.description = jsonObject.has("description") ? jsonObject.getString("description") : "";
            meal.order = jsonObject.has("order") ? Order.fromJson(jsonObject.getJSONObject("order")): null;
            meal.price = jsonObject.has("cost") ? jsonObject.getDouble("cost") : 0.0;
            meal.imageUrl = jsonObject.has("imageUrl") ? jsonObject.getString("imageUrl") : "";
            meal.quantity = jsonObject.has("quantity") ? jsonObject.getInt("quantity") : 0;
            meal.date = jsonObject.has("date") ? jsonObject.getString("date") : "";
            meal.save();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return meal;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeParcelable(order, i);
        dest.writeDouble(price);
        dest.writeString(imageUrl);
        dest.writeInt(quantity);
        dest.writeString(date);
    }

    public static final Parcelable.Creator<Meal> CREATOR
            = new Parcelable.Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };
}

