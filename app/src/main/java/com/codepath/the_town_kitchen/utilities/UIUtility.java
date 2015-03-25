package com.codepath.the_town_kitchen.utilities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

public class UIUtility {
    // DeviceDimensionsHelper.getDisplayWidth(context) => (display width in pixels)
    public static int getDisplayWidth(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    // DeviceDimensionsHelper.getDisplayHeight(context) => (display height in pixels)
    public static int getDisplayHeight(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    // DeviceDimensionsHelper.convertDpToPixel(25f, context) => (25dp converted to pixels)
    public static float convertDpToPixel(float dp, Context context){
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    // DeviceDimensionsHelper.convertPixelsToDp(25f, context) => (25px converted to dp)
    public static float convertPixelsToDp(float px, Context context){
        Resources r = context.getResources();
        DisplayMetrics metrics = r.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }


    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        v.getLayoutParams().height = 0;
        v.setVisibility(View.VISIBLE);
        ObjectAnimator animAlpha = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        int duration = 600;//(int)((targetHeight / v.getContext().getResources().getDisplayMetrics().density))* 4;
        a.setDuration(duration);
        v.startAnimation(a);

        animAlpha.setDuration(duration);
        animAlpha.start();
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();
        ObjectAnimator animAlpha = ObjectAnimator.ofFloat(v, "alpha", 1f, 0f);

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        int duration = 600;//((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density)) * 4
        a.setDuration(duration);
        v.startAnimation(a);

        animAlpha.setDuration(duration);
        animAlpha.start();
    }


    public static void grow(final View v, final IAnimationEndListener animationEndListener) {
        final ScaleAnimation growAnim = new ScaleAnimation(1.0f, 1.25f, 1.0f, 1.25f);
        growAnim.setDuration(200);


        growAnim.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationStart(Animation animation){}

            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation) {
              animationEndListener.handle(v);
            }
        });
        v.setAnimation(growAnim);
        v.startAnimation(growAnim);
    }

    public static void shrink(final View v) {
        final ScaleAnimation shrinkAnim = new ScaleAnimation(1.25f, 1.0f, 1.25f, 1.0f);
        shrinkAnim.setDuration(200);

        v.setAnimation(shrinkAnim);
        shrinkAnim.start();
    }

     public interface IAnimationEndListener{
         void handle(View v);
         
     }

}