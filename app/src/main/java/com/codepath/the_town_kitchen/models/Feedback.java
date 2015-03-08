package com.codepath.the_town_kitchen.models;

/**
 * Created by paulina on 3/7/15.
 */

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

@Table(name = "feedback")

public class Feedback extends Model implements Parcelable {

    @Column(name = "rating")
    private int rating;

    @Column(name = "comment")
    private String comment;

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Feedback() {
        super();
    }

    public Feedback(Parcel in) {
        rating = in.readInt();
        comment = in.readString();
    }

    public static ArrayList<Feedback> fromJsonArray(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.length() < 1) return null;
        ArrayList<Feedback> feedbacks = new ArrayList<>(jsonArray.length());
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
                Feedback feedback = Feedback.fromJson(resultJson);

                if (feedback != null) {
                    feedbacks.add(feedback);
                }

            }
            ActiveAndroid.setTransactionSuccessful();
            return feedbacks;
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static Feedback fromJson(JSONObject jsonObject) {
        Feedback feedback = new Feedback();
        try {
            feedback.rating = jsonObject.has("rating") ? jsonObject.getInt("rating") : 0;
            feedback.comment = jsonObject.has("comment") ? jsonObject.getString("comment") : "";
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return feedback;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeInt(rating);
        dest.writeString(comment);
    }

    public static final Creator<Feedback> CREATOR
            = new Creator<Feedback>() {
        @Override
        public Feedback createFromParcel(Parcel in) {
            return new Feedback(in);
        }

        @Override
        public Feedback[] newArray(int size) {
            return new Feedback[size];
        }
    };
}
