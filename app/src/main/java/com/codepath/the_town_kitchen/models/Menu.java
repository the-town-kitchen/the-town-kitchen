package com.codepath.the_town_kitchen.models;

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

@Table(name = "menu")

public class Menu extends Model implements Parcelable {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;
    @Column(name = "image_url")
    private String imageUrl;

    public Menu(){
        super();

    }
    public Menu(Parcel in) {
        name = in.readString();
        description = in.readString();
        imageUrl = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public static ArrayList<Menu> fromJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() < 1) return null;
        ArrayList<Menu> menus = new ArrayList<>(jsonArray.length());
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
                Menu menu = Menu.fromJson(resultJson);

                if (menu != null) {

                    menus.add(menu);
                }

            }
            ActiveAndroid.setTransactionSuccessful();
            return menus;
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static Menu fromJson(JSONObject jsonObject) {
        Menu menu = new Menu();
        try {
            menu.name = jsonObject.has("name") ? jsonObject.getString("name") : "";
            menu.description =  jsonObject.has("description") ? jsonObject.getString("description") : "";

            menu.imageUrl =  jsonObject.has("imageUrl") ? jsonObject.getString("imageUrl") : "";

            menu.save();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return menu;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(imageUrl);

    }

    public static final Creator CREATOR = new Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
