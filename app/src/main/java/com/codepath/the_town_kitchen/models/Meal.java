package com.codepath.the_town_kitchen.models;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Meal")
public class Meal extends ParseObject {
    private static final int MAX_MEALS_TO_SHOW = 4;

    private Long uid;

    private String name;

    private String description;

    private double price;

    private String imageUrl;

    private int quantity;

    private String date;

    public Long getUid() { return getLong("uid"); }

    public String getName() { return getString("name"); }

    public String getDescription() { return getString("description"); }

    public double getPrice() { return getDouble("price"); }

    public String getImageUrl() { return getString("imageUrl"); }

    public int getQuantity() { return getInt("quantity"); }

    public String getDate() { return getString("date"); }

    public void setUid(Long uid) { put("uid", uid); }

    public void setName(String name) { put("name", name); }

    public void setDescription(String description) { put("description", description); }

    public void setPrice(double price) { put("price", price); }

    public void setImageUrl(String imageUrl) { put("imageUrl", imageUrl); }

    public void setQuantity(int quantity) { put("quantity", quantity); }

    public void setDate(String date) { put("date", date); }

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
            meal.uid = jsonObject.getLong("uid");
            meal.name = jsonObject.has("name") ? jsonObject.getString("name") : "";
            meal.description = jsonObject.has("description") ? jsonObject.getString("description") : "";
            meal.price = jsonObject.has("cost") ? jsonObject.getDouble("cost") : 0.0;
            meal.imageUrl = jsonObject.has("imageUrl") ? jsonObject.getString("imageUrl") : "";
            meal.quantity = jsonObject.has("quantity") ? jsonObject.getInt("quantity") : 0;
            meal.date = jsonObject.has("date") ? jsonObject.getString("date") : "";
            meal.save();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return meal;
    }
    // Query messages from Parse so we can load them into the chat adapter
    public static void fromParse(final IMealsReceivedListener mealsReceived) {
        // Construct query to execute
        ParseQuery<Meal> query = ParseQuery.getQuery(Meal.class);
        query.setLimit(MAX_MEALS_TO_SHOW);
        query.orderByDescending("createdAt");
        // Execute query for messages asynchronously
        query.findInBackground(new FindCallback<Meal>() {
            public void done(List<Meal> parseMeals, ParseException e) {
                if (e == null) {
                    mealsReceived.handle(parseMeals);
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });

    }

    public interface IMealsReceivedListener{
        void handle(List<Meal> meals);
    }
}

