package com.codepath.the_town_kitchen;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.codepath.the_town_kitchen.net.FacebookApi;

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
	private static Context context;

	@Override
	public void onCreate() {
		super.onCreate();

        ActiveAndroid.initialize(this);
	}


    public static FacebookApi getFaceBookApi() {
        return (FacebookApi) FacebookApi.getInstance();
    }
    public static CurrentUser getCurrentUser() {
        return (CurrentUser) CurrentUser.getInstance();
    }


}