package com.codepath.the_town_kitchen.models;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.json.JSONException;
import org.json.JSONObject;

@ParseClassName("User")
public class User extends ParseObject {

    private String name;

    private String email;

    private String profileImageUrl;

    private String facebookId;
    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 40;

    public User(){
        super();
    }

    public String getName() {
        return name;
    }

    public String getEmail() { return email; }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public String getFacebookId() {
        return facebookId;
    }
    public static User fromGooglePerson(Person person, GoogleApiClient mGoogleApiClient) {
        User user = new User();
        user.name = person.getDisplayName();
        user.profileImageUrl = person.getImage().getUrl();
        user.profileImageUrl = user.profileImageUrl.substring(0,
                user.profileImageUrl.length() - 2)
                + PROFILE_PIC_SIZE;
        user.email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        try {
            user.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static User fromJson(JSONObject json) {
        User user = new User();
        try {
            user.email = json.getString("email");
            user.name = json.getString("name");
            user.facebookId = json.getString("id");
            user.save();
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
