package com.codepath.the_town_kitchen.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

@Table(name = "user")

public class User extends Model implements Parcelable {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 40;

    public User(){
        super();

    }
    public User(Parcel in) {
        name = in.readString();
        email = in.readString();
        profileImageUrl = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getEmail() { return email; }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public static User fromGooglePerson(Person person, GoogleApiClient mGoogleApiClient) {
        User user = new User();
        user.name = person.getDisplayName();
        user.profileImageUrl = person.getImage().getUrl();
        user.profileImageUrl = user.profileImageUrl.substring(0,
                user.profileImageUrl.length() - 2)
                + PROFILE_PIC_SIZE;
        user.email = Plus.AccountApi.getAccountName(mGoogleApiClient);
        user.save();
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(profileImageUrl);

    }

    public static final Creator CREATOR = new Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public static void deleteAll() {
        new Delete().from(User.class).execute();
    }
}
