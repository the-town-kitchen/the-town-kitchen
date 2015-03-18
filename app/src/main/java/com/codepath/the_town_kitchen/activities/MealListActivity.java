package com.codepath.the_town_kitchen.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.FragmentNavigationDrawer;
import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.adapters.MealAdapter;
import com.codepath.the_town_kitchen.fragments.AboutFragment;
import com.codepath.the_town_kitchen.fragments.MealMenuFragment;
import com.codepath.the_town_kitchen.fragments.ProfileFragment;
import com.codepath.the_town_kitchen.models.Meal;
import com.codepath.the_town_kitchen.models.Order;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MealListActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, MealAdapter.IActionClickListener {
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

    private FragmentNavigationDrawer dlDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);
        setupToolbar();
        setupNavDrawer();

        meals = new ArrayList<>();
        mealAdapter = new MealAdapter(this, meals, this);
        lvList.setAdapter(mealAdapter);
        Meal.fromParse(mealsReceived);
        calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);

        // Select default nav drawer item
        if (savedInstanceState == null) {
            dlDrawer.selectDrawerItem(1);
        }

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
        tvCalendar = (TextView) findViewById(R.id.tvCalendar);
        java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-d");
        String date = df.format(Calendar.getInstance().getTime());
        tvCalendar.setText(date);
        tvCount = (TextView) findViewById(R.id.tvCount);
        imgCalendar = (ImageView) findViewById(R.id.icon_calendar);
        imgCart = (ImageView) findViewById(R.id.icon_cart);
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
                timePickerDialog.setVibrate(false);
                timePickerDialog.setCloseOnSingleTapMinute(true);
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);

            }
        });
    }

    private void setupNavDrawer() {
        lvList = (ListView) findViewById(R.id.lvList);

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        dlDrawer = (FragmentNavigationDrawer) findViewById(R.id.drawer_layout);
        // Setup drawer view
        dlDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer), toolbar,
                R.layout.drawer_nav_item, R.id.flContent);

        // Add nav items
        dlDrawer.addNavItem(getResources().getString(R.string.nav_item_profile), getResources().getString(R.string.nav_item_profile), ProfileFragment.class);
        dlDrawer.addNavItem(getResources().getString(R.string.nav_item_meal_menu), getResources().getString(R.string.nav_item_meal_menu), MealMenuFragment.class);
        dlDrawer.addNavItem(getResources().getString(R.string.nav_item_about), getResources().getString(R.string.nav_item_about), AboutFragment.class);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        if (dlDrawer.isDrawerOpen()) {
            // Uncomment to hide menu items
            // menu.findItem(R.id.mi_test).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        // Uncomment to inflate menu items to Action Bar
        // inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (dlDrawer.getDrawerToggle().onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        dlDrawer.getDrawerToggle().syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        dlDrawer.getDrawerToggle().onConfigurationChanged(newConfig);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        tvCalendar.setText(year + "-" + (month + 1) + "-" + day);
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

    }
}
