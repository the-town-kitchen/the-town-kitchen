package com.codepath.the_town_kitchen;

import com.codepath.the_town_kitchen.models.User;

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


}