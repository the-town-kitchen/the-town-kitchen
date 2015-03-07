package com.codepath.the_town_kitchen;

import com.codepath.the_town_kitchen.models.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class CurrentUser {
    private static CurrentUser instance = null;

    private User user;
    protected CurrentUser() {
        // Exists only to defeat instantiation.
    }
    public static CurrentUser getInstance() {
        if(instance == null) {
            instance = new CurrentUser();
        }
        return instance;
    }

    public  void setUser(User user) {
        this.user = user;
    }
    
    public  User getUser(){
        return this.user;
        
    }

    public void requestCurrentUser(GoogleApiClient googleApiClient) {
        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
            Person person = Plus.PeopleApi
                    .getCurrentPerson(googleApiClient);
            User user = User.fromGooglePerson(person, googleApiClient);


            TheTownKitchenApplication.getCurrentUser().setUser(user);
        }

    }
}