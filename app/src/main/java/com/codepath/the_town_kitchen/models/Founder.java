package com.codepath.the_town_kitchen.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by xdai on 3/24/15.
 */

@ParseClassName("Founder")
public class Founder extends ParseObject{

  
    private String name;

    private String role;


    private String imageUrl;


    public String getName() { return getString("Name"); }

    public String getRole() { return getString("Role"); }
    public String getImageUrl() { return getString("imageUrl"); }

    public void setName(String name) { put("Name", name); }

    public void setRole(String role) { put("Role", role); }

    public void setImageUrl(String imageUrl) { put("imageUrl", imageUrl); }
}
