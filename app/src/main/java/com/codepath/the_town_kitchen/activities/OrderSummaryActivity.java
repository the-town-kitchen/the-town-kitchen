package com.codepath.the_town_kitchen.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.adapters.OrderItemAdapter;
import com.codepath.the_town_kitchen.fragments.ProgressBarDialog;
import com.codepath.the_town_kitchen.models.Order;
import com.codepath.the_town_kitchen.models.OrderItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class OrderSummaryActivity extends ActionBarActivity {
    Button bSubmitOrder;
    Button bPaymentInfo;
    ProgressBarDialog progressBarDialog;

    private ListView lvOrderItems;
    private OrderItemAdapter orderItemAdapter;
    private List<OrderItem> orderItems;
    private TextView tvOrderTotal;
    private TextView tvSubTotal;
    private TextView tvDeliveryTime;
    private TextView tvAddress;
    private TextView tvTax;
    private TextView tvDiscount;
    private Order orderToSave;
    private TextView tvDiscountlabel;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.dark_primary_red));

        bSubmitOrder = (Button) findViewById(R.id.bSubmitOrder);
        bSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPogressBarDialog();
            }
        });

        bPaymentInfo = (Button) findViewById(R.id.bPaymentInfo);
        bPaymentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderSummaryActivity.this, PaymentInfoActivity.class);
                startActivity(i);
            }
        });
        tvOrderTotal = (TextView) findViewById(R.id.tvOrderTotal);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvSubTotal= (TextView) findViewById(R.id.tvSubTotal);
        tvTax= (TextView) findViewById(R.id.tvTax);
        tvDiscount= (TextView) findViewById(R.id.tvDiscount);
        tvDiscountlabel =  (TextView) findViewById(R.id.tvDiscountlabel);
        //order items
        lvOrderItems = (ListView) findViewById(R.id.lvOrderItems);
        tvDeliveryTime = (TextView) findViewById(R.id.tvDeliveryTime);
        Order.getOrderByDate(TheTownKitchenApplication.orderDate, new Order.IOrderReceivedListener() {
            @Override
            public void handle(Order order, List<OrderItem> orderItems) {
                if (order != null) {
                    tvDeliveryTime.setText(order.getDate() + " " + order.getTime());

                    if (orderItems == null)
                        orderItems = new ArrayList<>();
                    orderItemAdapter = new OrderItemAdapter(OrderSummaryActivity.this, orderItems, null);
                    lvOrderItems.setAdapter(orderItemAdapter);
                    double subTotal = order.getCost();
                    double tax = order.getCost() * 0.09;
                    tvSubTotal.setText("$" + subTotal);
                    tvTax.setText("$" + new DecimalFormat("##.##").format(tax));
                    tvOrderTotal.setText("$" +  new DecimalFormat("##.##").format(subTotal + tax));
                    tvAddress.setText(order.getDeliveryLocation());


                    orderToSave = order;
                }
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_summary, menu);
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

    private void showPogressBarDialog() {
        FragmentManager fm = getSupportFragmentManager();
        progressBarDialog = ProgressBarDialog.newInstance();
        progressBarDialog.show(fm, "fragment_progress_bar");


        Handler handler = null;
        handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                saveOrder();
                progressBarDialog.dismiss();
                startMealListActivity();
            }
        }, 1000);
    }

    private void saveOrder() {
        orderToSave.setIsPlaced(true);
        orderToSave.saveInBackground();
    }

    private void startMealListActivity(){
        Intent startIntent = new Intent(this, MealListActivity.class);
        this.startActivity(startIntent);
    }

    public void onCouponCodeSubmit(View view) {
        EditText etCouponCode = (EditText) findViewById(R.id.etCouponCode);
        tvDiscountlabel.setVisibility(View.VISIBLE);
        tvDiscount.setText("$-" + new DecimalFormat("##.##").format(orderToSave.getCost() * 0.15));
        tvDiscountlabel.setVisibility(View.VISIBLE);
        tvDiscount.setVisibility(View.VISIBLE);
        orderToSave.setCost(orderToSave.getCost() * 0.85 );
        tvOrderTotal.setText("$" +  new DecimalFormat("##.##").format(orderToSave.getCost()));

        etCouponCode.setText("");
    }
}
