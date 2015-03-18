package com.codepath.the_town_kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
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
import com.codepath.the_town_kitchen.models.User;
import com.facebook.widget.ProfilePictureView;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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


        meals = new ArrayList<>();
        mealAdapter = new MealAdapter(this, meals, this);
        lvList.setAdapter(mealAdapter);
        Meal.fromParse(mealsReceived);
        calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

        setupOrderCounts();
    }

    private void setupOrderCounts(){
        Order.getOrderByDateWithoutItems(tvCalendar.getText().toString(), new Order.IParseOrderReceivedListener() {
            @Override
            public void handle(ParseObject order, List<ParseObject> orderItems) {
                if(order != null){
                    tvCount.setText(order.getInt("quantity") + "");
                }
                else
                    tvCount.setVisibility(View.GONE);
            }
        });

    }

    private Meal.IMealsReceivedListener mealsReceived = new Meal.IMealsReceivedListener() {
        @Override
        public void handle(List<Meal> parseMeals) {
            meals.clear();
            meals.addAll(parseMeals);
            mealAdapter.notifyDataSetChanged();

        }
    };

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvCalendar = (TextView) toolbar.findViewById(R.id.tvCalendar);
        java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-d");
        String date = df.format(Calendar.getInstance().getTime());
        tvCalendar.setText(date);
        TheTownKitchenApplication.orderDate = date;
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
                Order order = TheTownKitchenApplication.getOrder();

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
        String newDate = year + "-" + (month + 1) + "-" + day;
        tvCalendar.setText(newDate);
        TheTownKitchenApplication.orderDate = newDate;

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, final int hourOfDay, final int minute) {
        Order.getOrderByDateWithoutItems(tvCalendar.getText().toString(), new Order.IParseOrderReceivedListener() {
            @Override
            public void handle(ParseObject order, List<ParseObject> orderItems) {

                if(order != null){

                    order.put("time", hourOfDay + ":" + minute);
                    order.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            startOrderSummaryActivity();
                        }
                    });
                    order.pinInBackground();
                }

            }

        });

    }

    private void startOrderSummaryActivity() {
        Intent i = new Intent(MealListActivity.this, OrderSummaryActivity.class);
        startActivity(i);
    }

    @Override
    public void onActionClicked(int position, int count) {

        Meal meal = meals.get(position);
        String date = tvCalendar.getText().toString();
        Order.update(date, meal, count);

        tvCount.setText(count + "");
        tvCount.setVisibility(View.VISIBLE);
    }
}