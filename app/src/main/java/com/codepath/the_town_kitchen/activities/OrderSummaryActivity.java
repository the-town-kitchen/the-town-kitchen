package com.codepath.the_town_kitchen.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.adapters.OrderItemAdapter;
import com.codepath.the_town_kitchen.models.Order;
import com.codepath.the_town_kitchen.models.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderSummaryActivity extends ActionBarActivity {
    Button bSubmitOrder;
    Button bPaymentInfo;

    private ListView lvOrderItems;
    private OrderItemAdapter orderItemAdapter;
    private ArrayList<OrderItem> orderItems;
    private TextView tvOrderTotal;
    private TextView tvDeliveryTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        bSubmitOrder = (Button) findViewById(R.id.bSubmitOrder);
        bSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OrderSummaryActivity.this, FeedbackActivity.class);
                startActivity(i);
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


        //order items
        lvOrderItems = (ListView) findViewById(R.id.lvOrderItems);
        tvDeliveryTime = (TextView) findViewById(R.id.tvDeliveryTime);
        Order.getOrderByDateFromLocal(TheTownKitchenApplication.orderDate, new Order.IOrderReceivedListener() {
            @Override
            public void handle(Order order, List<OrderItem> orderItems) {
                if (order != null) {
                    tvDeliveryTime.setText(order.getDate() + " " + order.getTime());

                    if (orderItems == null)
                        orderItems = new ArrayList<>();
                    orderItemAdapter = new OrderItemAdapter(OrderSummaryActivity.this, orderItems, null);
                    lvOrderItems.setAdapter(orderItemAdapter);
                    tvOrderTotal = (TextView) findViewById(R.id.tvOrderTotal);
                    tvOrderTotal.setText("$" + order.getCost() + "");
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
}
