package com.codepath.the_town_kitchen.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_list);
        setupToolbar();


        getSupportActionBar().setDisplayUseLogoEnabled(false);

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
        Order.getLastOrderByDate(tvCalendar.getText().toString(), new Order.IOrderReceivedListener() {
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
                        Intent i = new Intent(MealListActivity.this, FeedbackActivity.class);
                        i.putExtra("orderId", order.getObjectId());
                        startActivity(i);
                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    }
                    else {//order is placed but not delivered,TODO show the order
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
        fragment.setTime(hourOfDay, minute);
        startDeliveryLocationActivity(TheTownKitchenApplication.getOrder().getCurrentOrder().getObjectId());
       
    }

    private void startDeliveryLocationActivity(String orderId) {
        Intent i = new Intent(MealListActivity.this, DeliveryLocationActivity.class);
        Log.d(TAG, "orderId " + orderId);
        i.putExtra("orderId", orderId);
        startActivity(i);
        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }



}