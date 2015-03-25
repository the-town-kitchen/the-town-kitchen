package com.codepath.the_town_kitchen.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.utilities.AnimationFactory;
import com.codepath.the_town_kitchen.utilities.UIUtility;
import com.codepath.the_town_kitchen.models.Meal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealAdapter extends ArrayAdapter<Meal> {

    public interface IActionClickListener{
        void onActionClicked(int position, int count);
    }
    
    private IActionClickListener actionClickListener;
    private static class ViewHolder {
        public ImageView ivImage;
        public TextView tvDescription;
        public TextView tvPrice;
        public ImageButton ibMinus;
        public ImageButton ibPlus;
        public TextView tvCounts;
        public TextView tvTest;
        public ViewAnimator viewAnimator;
                
    }

    public MealAdapter(Context context, List<Meal> meals, IActionClickListener actionClickListener) {
        super(context, 0, meals);
        this.actionClickListener = actionClickListener;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Meal meal = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.flip_item_meal, parent, false);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
            viewHolder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);
            viewHolder.tvPrice =  (TextView)convertView.findViewById(R.id.tvPrice);
            viewHolder.ibMinus = (ImageButton) convertView.findViewById(R.id.ibMinus);
            viewHolder.ibPlus = (ImageButton) convertView.findViewById(R.id.ibPlus);
            viewHolder.tvCounts = (TextView) convertView.findViewById(R.id.tvCounts);
            viewHolder.tvTest = (TextView) convertView.findViewById(R.id.tvTest);
            viewHolder.viewAnimator = (ViewAnimator) convertView.findViewById(R.id.vfFlipper);
            convertView.setTag(viewHolder);
            
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvDescription.setText(meal.getDescription());
        viewHolder.tvPrice.setText("$" + meal.getPrice());

        viewHolder.tvCounts.setText(meal.quantityOrdered + "");


        SetCountColor(meal.quantityOrdered > 0, viewHolder);
        viewHolder.ibMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconClicked(viewHolder, position, false);
            }
        });

        viewHolder.ibPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iconClicked(viewHolder, position, true);
            }
        });

        viewHolder.ivImage.setImageResource(0);

        viewHolder.ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationFactory.flipTransition(viewHolder.viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT);
            }
        });

        viewHolder.tvTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationFactory.flipTransition(viewHolder.viewAnimator, AnimationFactory.FlipDirection.LEFT_RIGHT);
            }
        });

        int deviceWidth = UIUtility.getDisplayWidth(getContext());
        Picasso.with(getContext()).load(meal.getImageUrl()).resize(deviceWidth, 0).into(viewHolder.ivImage);
        return convertView;
    }

    private void iconClicked(ViewHolder viewHolder, int position, boolean isAdded) {
        int counts = Integer.parseInt(viewHolder.tvCounts.getText().toString());
        if(isAdded)  counts = counts + 1;
        else if (counts > 0) {
            counts = counts -1;
        }
        SetCountColor(counts > 0, viewHolder);
        viewHolder.tvCounts.setText(counts + "");
        actionClickListener.onActionClicked(position, counts);
    }

    private void SetCountColor(boolean isEmpty, ViewHolder viewHolder){

        if (isEmpty) {
            viewHolder.tvCounts.setTextColor(Color.parseColor("#009688"));
        } else {
            viewHolder.tvCounts.setTextColor(Color.parseColor("#727272"));
        }
        
    }

}
