package com.codepath.the_town_kitchen.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import com.codepath.the_town_kitchen.R;

public class TheTownKitchenBaseActivity extends ActionBarActivity{
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(this.getResources().getColor(R.color.dark_primary_red));
    }
        // onBackPressed is what is called when back is hit, call `overridePendingTransition`
        @Override
        public void onBackPressed() {
            finish();
            overridePendingTransition(R.anim.left_in, R.anim.right_out);
        }

}
