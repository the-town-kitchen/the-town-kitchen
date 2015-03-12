package com.codepath.the_town_kitchen.models;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by paulina on 3/7/15.
 */

@Table(name = "feedback")

public class Feedback extends Model {

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

    public static void deleteAll() {
        new Delete().from(Feedback.class).execute();
    }
}
