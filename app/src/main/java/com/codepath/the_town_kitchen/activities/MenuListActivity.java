package com.codepath.the_town_kitchen.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.adapters.MenuAdapter;
import com.codepath.the_town_kitchen.models.User;
import com.facebook.widget.ProfilePictureView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
public class MenuListActivity extends ActionBarActivity {
    private ProfilePictureView profilePictureView;
    private ImageView ivProfile;
    private TextView tvUserName, tvEmail;
    private ListView lvList;
    private MenuAdapter menuAdapter;
    private ArrayList<com.codepath.the_town_kitchen.models.Meal> menus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        toolbar.setLogo(R.mipmap.ic_launcher);
        actionBar.setTitle("");
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        lvList = (ListView) findViewById(R.id.lvList);
        profilePictureView = (ProfilePictureView) findViewById(R.id.ivFacebookProfile);
        //Mock data here;
        readFile();
        if(menus == null) {
            menus = new ArrayList<>();
        }
        menuAdapter = new MenuAdapter(this, menus, null);
        lvList.setAdapter(menuAdapter);
        User currentUser = TheTownKitchenApplication.getCurrentUser().getUser();
        if(currentUser!= null) {
            if(currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
                Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivProfile);
            }
            else if(currentUser.getFacebookId()!= null && !currentUser.getFacebookId().isEmpty()){
            
                profilePictureView.setCropped(true);
                profilePictureView.setProfileId(currentUser.getFacebookId());
                profilePictureView.setVisibility(View.VISIBLE);

            }
            tvUserName.setText(currentUser.getName());
            tvEmail.setText(currentUser.getEmail());
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void readFile(){
            String json = loadJSONFromAsset();
      
        try {
            JSONObject jsonObject  = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("menu");
            if (jsonArray != null && jsonArray.length() > 0) {
                menus = com.codepath.the_town_kitchen.models.Meal.fromJsonArray(jsonArray);
            }
            Log.d("MenuListActivity", "menu list " + menus.size());
        } 
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("menu.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
