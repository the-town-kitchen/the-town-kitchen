package com.codepath.the_town_kitchen.models;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.ParseClassName;
import com.parse.ParseObject;

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

    public String getName() { return getString("name"); }

    public String getEmail() { return getString("email"); }

    public String getProfileImageUrl() { return getString("profileImageUrl"); }

    public String getFacebookId() { return getString("facebookId"); }

    public void setName(String name) { put("name", name); }

    public void setEmail(String email) { put("email", email); }

    public void setProfileImageUrl(String profileImageUrl) { put("profileImageUrl", profileImageUrl); }

    public void setFacebookId(String facebookId) { put("facebookId", facebookId); }

    public static User fromGooglePerson(Person person, GoogleApiClient mGoogleApiClient) {
        ParseObject user = ParseObject.create("User");// new ParseObject("Message");
        user.put("name", person.getDisplayName());
        user.put("email", Plus.AccountApi.getAccountName(mGoogleApiClient));
        String profileImageUrl = person.getImage().getUrl();
        profileImageUrl =profileImageUrl.substring(0,
                profileImageUrl.length() - 2)
                + PROFILE_PIC_SIZE;
        user.put("profileImageUrl", profileImageUrl);
        user.saveInBackground();
        user.pinInBackground();
        return (User) user;
       // User user = new User();
       // user.name = person.getDisplayName();


    }

    public static User fromFacebookJson(JSONObject json) {
        ParseObject user = ParseObject.create("User");// new ParseObject("Message");

        try {
            user.put("name", json.getString("name"));
            user.put("email", json.getString("email"));
            user.put("facebookId", json.getString("id"));
            user.saveInBackground();
            user.pinInBackground();
            return (User) user;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
