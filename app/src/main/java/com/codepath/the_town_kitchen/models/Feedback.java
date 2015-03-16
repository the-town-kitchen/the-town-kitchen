package com.codepath.the_town_kitchen.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by paulina on 3/7/15.
 */

@ParseClassName("Feedback")
public class Feedback extends ParseObject {

    public int getRating() {
        return getInt("rating");
    }

    public String getComment() { return getString("comment");  }

    public void setRating(int rating) { put("rating", rating); }

    public void setComment(String comment) { put("comment", comment); }

}
