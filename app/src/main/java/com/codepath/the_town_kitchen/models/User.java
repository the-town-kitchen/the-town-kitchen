package com.codepath.the_town_kitchen.models;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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

    public User() {
        super();
    }


    public String getName() {
        return getString("name");
    }

    public String getEmail() {
        return getString("email");
    }

    public String getProfileImageUrl() {
        return getString("profileImageUrl");
    }

    public String getFacebookId() {
        return getString("facebookId");
    }

    public void setName(String name) {
        put("name", name);
    }

    public void setEmail(String email) {
        put("email", email);
    }

    public void setProfileImageUrl(String profileImageUrl) {
        put("profileImageUrl", profileImageUrl);
    }

    public void setFacebookId(String facebookId) {
        put("facebookId", facebookId);
    }

    public static User fromGooglePerson(Person person, GoogleApiClient mGoogleApiClient) {
        User user = new User();
        user.name = person.getDisplayName();
        user.profileImageUrl = person.getImage().getUrl();
        user.profileImageUrl = user.profileImageUrl.substring(0,
                user.profileImageUrl.length() - 2)
                + PROFILE_PIC_SIZE;
        user.email = Plus.AccountApi.getAccountName(mGoogleApiClient);

        return user;


    }

    public static User fromFacebookJson(JSONObject json) {
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

    public static void updateParseUser(final User user, final IUserLoadedListener userLoadedListener) {
        final ParseObject parseUser = ParseObject.create("User");// new ParseObject("Message");

        parseUser.put("name", user.name);
        parseUser.put("email", user.email);
        if(user.facebookId != null && !user.facebookId.isEmpty()) {
            parseUser.put("facebookId", user.facebookId);
        }
        if(user.profileImageUrl != null && !user.profileImageUrl.isEmpty()) {
            parseUser.put("profileImageUrl", user.profileImageUrl);
        }
        if(parseUser.get("email") != null && !parseUser.get("email").toString().isEmpty()) {
            parseUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    userLoadedListener.handle((User) parseUser);
                }
            });
        }

    }


    public static void getParseUser(User user, final IUserLoadedListener userLoadedListener) {

        ParseQuery<User> query = ParseQuery.getQuery(User.class);

        query.whereEqualTo("email", user.email);
        // Execute query for order asynchronously
        query.getFirstInBackground(new GetCallback<User>() {
            public void done(final User user, ParseException e) {
                if (e == null || e.getCode() == 101) {
                    userLoadedListener.handle(user);
                }
            }
        });
    }
    public interface IUserLoadedListener {
        void handle(User user);
    }

}
