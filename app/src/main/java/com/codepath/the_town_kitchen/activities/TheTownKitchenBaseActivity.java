package com.codepath.the_town_kitchen.activities;

import android.support.v7.app.ActionBarActivity;

import com.codepath.the_town_kitchen.R;

public class TheTownKitchenBaseActivity extends ActionBarActivity{

        // onBackPressed is what is called when back is hit, call `overridePendingTransition`
        @Override
        public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }

}
