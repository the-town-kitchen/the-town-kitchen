package com.codepath.the_town_kitchen.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.models.Order;
import com.codepath.the_town_kitchen.models.OrderItem;

import java.util.List;

public class FeedbackActivity extends TheTownKitchenBaseActivity {
    RatingBar rbFeedback;
    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        orderId = getIntent().getStringExtra("orderId");

        rbFeedback = (RatingBar) findViewById(R.id.rbFeedback);
        rbFeedback.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {


            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                Order.getOrderById(orderId, new Order.IOrderReceivedListener() {
                    @Override
                    public void handle(Order order, List<OrderItem> orderItems) {
                        order.setFeedbackRating(Math.round(rating));
                        order.saveInBackground();
                        finishActivity();
                    }
                });

            }
        });
    }

    private void finishActivity(){
        this.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
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
