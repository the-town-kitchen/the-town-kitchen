package com.codepath.the_town_kitchen.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.FragmentNavigationDrawer;
import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.fragments.AboutFragment;
import com.codepath.the_town_kitchen.fragments.ProfileFragment;
import com.codepath.the_town_kitchen.utilities.UIUtility;
import com.codepath.the_town_kitchen.fragments.MealListFragment;
import com.codepath.the_town_kitchen.models.Order;
import com.codepath.the_town_kitchen.models.OrderItem;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MealListActivity extends TheTownKitchenBaseActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
  
    private TextView tvCount;
    private TextView tvCalendar;
    private ImageView imgCalendar;
    private ImageView imgCart;
    private static String TAG = MealListActivity.class.getSimpleName();

    private Calendar calendar;
    private MealListFragment fragment;

    public static final String DATEPICKER_TAG = "datepicker";
    public static final String TIMEPICKER_TAG = "timepicker";
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private FragmentNavigationDrawer dlDrawer;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);
        setupToolbar();

        setupNavDrawer();


//        getSupportActionBar().setDisplayUseLogoEnabled(false);

        fragment = new  MealListFragment(new MealListFragment.ICountUpdateListener() {
            @Override
            public void handle(final int count) {
                UIUtility.grow(tvCount, new UIUtility.IAnimationEndListener() {
                    @Override
                    public void handle(View v) {
                        ((TextView) v).setText(count + "");
                        UIUtility.shrink(v);
                    }
                });
            }
        });
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_placeholder, fragment)
                .commit();

        calendar = Calendar.getInstance();
        datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);
        timePickerDialog = TimePickerDialog.newInstance(this, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false, false);
        setupOrderCounts();
    }

    private void setupOrderCounts(){
        Order.getLastOrderByDate(tvCalendar.getText().toString(),
                new Order.IOrderReceivedListener() {
                    @Override
                    public void handle(Order order, List<OrderItem> orderItems) {
                        if (order != null) {
                            if (!order.getIsPlaced()) {
                                order.setOrderItems(orderItems);
                                TheTownKitchenApplication.getOrder().setCurrentOrder(order);

                                tvCount.setText(order.getQuantity() + "");
                                fragment.getMeals(order);

                            } else if (order.getIsDelivered() && order.getFeedbackRating() == 0) {
                                createNewOrder();
                                Intent i = new Intent(MealListActivity.this,
                                        FeedbackActivity.class);
                                i.putExtra("orderId", order.getObjectId());
                                startActivity(i);
                                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                            } else {//order is placed but not delivered,TODO show the order
                                createNewOrder();
                            }
                        } else {
                            createNewOrder();
                        }
                    }
                });

    }

    private void createNewOrder() {
        Order.createNewOrder(new Order.IOrderReceivedListener() {
            @Override
            public void handle(Order order, List<OrderItem> orderItems) {
                TheTownKitchenApplication.getOrder().setCurrentOrder(order);
                fragment.getMeals(order);
            }
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        tvCalendar = (TextView) toolbar.findViewById(R.id.tvCalendar);
        java.text.DateFormat df = new SimpleDateFormat("yyyy-MM-d");
        String date = df.format(Calendar.getInstance().getTime());
        tvCalendar.setText(date);
        TheTownKitchenApplication.orderDate = date;
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();

        actionBar.setTitle("");
        tvCount = (TextView) toolbar.findViewById(R.id.tvCount);
        tvCount.setText("0");
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
                timePickerDialog.setVibrate(false);
                timePickerDialog.setCloseOnSingleTapMinute(true);
                timePickerDialog.show(getSupportFragmentManager(), TIMEPICKER_TAG);

            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        String newDate = year + "-" + (month + 1) + "-" + day;
        tvCalendar.setText(newDate);
        TheTownKitchenApplication.orderDate = newDate;

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, final int hourOfDay, final int minute) {
        fragment.setTime(hourOfDay, minute);
        startDeliveryLocationActivity(
                TheTownKitchenApplication.getOrder().getCurrentOrder().getObjectId());
       
    }

    private void startDeliveryLocationActivity(String orderId) {
        Intent i = new Intent(MealListActivity.this, DeliveryLocationActivity.class);
        Log.d(TAG, "orderId " + orderId);
        i.putExtra("orderId", orderId);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void setupNavDrawer() {

        // Set a Toolbar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find our drawer view
        dlDrawer = (FragmentNavigationDrawer) findViewById(R.id.drawer_layout);
        // Setup drawer view
        dlDrawer.setupDrawerConfiguration((ListView) findViewById(R.id.lvDrawer), toolbar,
                R.layout.drawer_nav_item, R.id.fragment_placeholder);

        // Add nav items
        dlDrawer.addNavItem("Profile", R.mipmap.ic_launcher, "Profile", ProfileFragment.class);
        dlDrawer.addNavItem("Current Menu",  R.id.icon_calendar, "Current Menu", MealListFragment.class);
        dlDrawer.addNavItem("About",  R.id.icon_calendar, "About", AboutFragment.class);
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

}