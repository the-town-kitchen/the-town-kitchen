package com.codepath.the_town_kitchen;

import android.content.Context;

import com.codepath.the_town_kitchen.models.Feedback;
import com.codepath.the_town_kitchen.models.Founder;
import com.codepath.the_town_kitchen.models.Meal;
import com.codepath.the_town_kitchen.models.Order;
import com.codepath.the_town_kitchen.models.OrderItem;
import com.codepath.the_town_kitchen.models.User;
import com.codepath.the_town_kitchen.net.FacebookApi;
import com.codepath.the_town_kitchen.net.GoogleApi;
import com.parse.Parse;
import com.parse.ParseObject;

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     RestClient client = RestApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class TheTownKitchenApplication extends com.activeandroid.app.Application {

    public static final String YOUR_APPLICATION_ID = "i2RZZvSfSfdxhCQkLY8CDa5IzqyLy10kMA3m6yDh";
    public static final String YOUR_CLIENT_KEY = "9kUiDfwDw6toVN7Wgw0PjmfnlhhLjzEuG5Vp1q8V";

    private static Context context;
    public static String orderDate;

    @Override
    public void onCreate() {
        super.onCreate();

        // Register parse models
        ParseObject.registerSubclass(Order.class);
        ParseObject.registerSubclass(OrderItem.class);
        ParseObject.registerSubclass(Meal.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Feedback.class);
        ParseObject.registerSubclass(Founder.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, YOUR_APPLICATION_ID, YOUR_CLIENT_KEY);

//        ActiveAndroid.initialize(this);
    }

    public static FacebookApi getFaceBookApi() {
        return (FacebookApi) FacebookApi.getInstance();
    }
    public static GoogleApi getGoogleApi() {
        return (GoogleApi) GoogleApi.getInstance();
    }

    public static CurrentUser getCurrentUser() {
        return (CurrentUser) CurrentUser.getInstance();
    }

    public static Order getOrder() {
        return (Order) Order.getInstance();
    }

}