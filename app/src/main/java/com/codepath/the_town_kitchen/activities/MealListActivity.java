package com.codepath.the_town_kitchen.activities;

import android.content.Intent;
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
import android.widget.Toast;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.adapters.MealAdapter;
import com.codepath.the_town_kitchen.models.Meal;
import com.codepath.the_town_kitchen.models.Order;
import com.codepath.the_town_kitchen.models.OrderItem;
import com.codepath.the_town_kitchen.models.User;
import com.facebook.widget.ProfilePictureView;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class MealListActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, MealAdapter.IActionClickListener {
    private ProfilePictureView profilePictureView;
    private ImageView ivProfile;
    private TextView tvUserName, tvEmail;
    private ListView lvList;
    private MealAdapter mealAdapter;
    private TextView tvCount;

    private TextView tvCalendar;
    private ImageView imgCalendar;
    private ImageView imgCart;
    private ArrayList<com.codepath.the_town_kitchen.models.Meal> meals;
    private static String TAG = MealListActivity.class.getSimpleName();

    private Calendar calendar;

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);
        setupToolbar();
        setupProfile();

        //Mock data here;
        meals = new ArrayList<>();
        mealAdapter = new MealAdapter(this, meals, this);
        lvList.setAdapter(mealAdapter);
        readFile("meal.json");

        calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvCalendar = (TextView) toolbar.findViewById(R.id.tvCalendar);
        tvCalendar.setText(getString(R.string.today));
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        toolbar.setLogo(R.mipmap.ic_launcher);
        actionBar.setTitle("");
        tvCount = (TextView) toolbar.findViewById(R.id.tvCount);
        imgCalendar = (ImageView) toolbar.findViewById(R.id.icon_calendar);
        imgCart = (ImageView) toolbar.findViewById(R.id.icon_cart);
        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.setVibrate(false);
                datePickerDialog.setYearRange(calendar.get(Calendar.YEAR), 2028);
                datePickerDialog.setCloseOnSingleTapDay(true);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);

            }
        });

        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Order order = TheTownKitchenApplication.getOrder().getOrderByDate(tvCalendar.getText().toString());

                if (order != null) {
                    if (order.getTime() == null || order.getTime().isEmpty()) {

                        timePickerDialog.setVibrate(false);
                        timePickerDialog.setCloseOnSingleTapMinute(true);
                        timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);

                    } else {
                        startOrderSummaryActivity();
                    }
                }

            }
        });
    }

    private void setupProfile() {
        ivProfile = (ImageView) findViewById(R.id.ivProfile);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvEmail = (TextView) findViewById(R.id.tvEmail);
        lvList = (ListView) findViewById(R.id.lvList);
        profilePictureView = (ProfilePictureView) findViewById(R.id.ivFacebookProfile);

        User currentUser = TheTownKitchenApplication.getCurrentUser().getUser();
        if (currentUser != null) {
            if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
                Picasso.with(this).load(currentUser.getProfileImageUrl()).into(ivProfile);
            } else if (currentUser.getFacebookId() != null && !currentUser.getFacebookId().isEmpty()) {

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
        getMenuInflater().inflate(R.menu.menu_meal_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        tvCalendar.setText(year + "-" + (month + 1) + "-" + day);
        readFile("meal2.json");
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        Order order = TheTownKitchenApplication.getOrder().getOrderByDate(tvCalendar.getText().toString());

        if (order != null) {
            if (order.getTime() == null || order.getTime().isEmpty()) {
                order.setTime(hourOfDay + "-" + minute);
                order.save(); 
                startOrderSummaryActivity();
            }

        } else
            Toast.makeText(this, "new time:" + hourOfDay + "-" + minute, Toast.LENGTH_LONG).show();
    }

    private void startOrderSummaryActivity() {
        Intent i = new Intent(MealListActivity.this, OrderSummaryActivity.class);
        startActivity(i);
    }

    private void readFile(String fileName) {
        String json = loadJSONFromAsset(fileName);

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("meal");
            if (jsonArray != null && jsonArray.length() > 0) {
                meals.clear();
                meals.addAll(com.codepath.the_town_kitchen.models.Meal.fromJsonArray(jsonArray));
                mealAdapter.notifyDataSetChanged();
            }
            Log.d(TAG, "meal list " + meals.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset(String fileName) {
        String json = null;
        try {
            InputStream is = getAssets().open(fileName);
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

    @Override
    public void onActionClicked(int position, int count) {
        Meal meal = meals.get(position);
        String date = tvCalendar.getText().toString();

        Order order = null; //Order.fromCacheByDate(date);
        ArrayList<OrderItem> orderItems;
        boolean isMealInCart = false;
        double totalCost = 0;
        int orderCount = 0;
        TheTownKitchenApplication.orderDate = date;
        //User singleton of Order
        order = TheTownKitchenApplication.getOrder().getOrderByDate(date);
        if (order == null) {
            order = new Order();
            orderItems = new ArrayList<>();
        } else {
            orderItems = order.getOrderItems();
        }

        if (orderItems == null || orderItems.size() == 0) {
            orderItems = new ArrayList<>();

            orderItems.add(OrderItem.orderItemFromClick(meal, count));
            totalCost = meal.getPrice() * count;
            orderCount = count;
        } else {
            for (OrderItem orderItem : orderItems) {
                if (orderItem.getMeal().getUid() == meal.getUid()) {
                    orderItem.setQuantity(count);
                    orderItem.save();

                    isMealInCart = true;

                    //break;
                }
                totalCost += orderItem.getQuantity() * orderItem.getMeal().getPrice();
                orderCount += orderItem.getQuantity();
            }
            if (!isMealInCart) {
                orderItems.add(OrderItem.orderItemFromClick(meal, count));
                totalCost += meal.getPrice() * count;
                orderCount += count;
            }
        }
        order.setOrderItems(orderItems);

        order.setDate(date);
        order.setUser(TheTownKitchenApplication.getCurrentUser().getUser());

        order.setCost(totalCost);


        order.save();
        tvCount.setText(orderCount + "");
    }
}
