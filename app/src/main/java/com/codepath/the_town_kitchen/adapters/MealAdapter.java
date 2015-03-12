package com.codepath.the_town_kitchen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.DeviceDimensionsHelper;
import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.models.Meal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MealAdapter extends ArrayAdapter<Meal> {

    public interface IActionClickListener{
        void onActionClicked(int position, String action);
    }
    
    private IActionClickListener actionClickListener;
    private static class ViewHolder {
        public ImageView ivImage;
        public TextView tvName;
        public TextView tvDescription;
        public TextView tvPrice;
        public ImageButton ibMinus;
        public ImageButton ibPlus;
        public TextView tvCounts;
                
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
            convertView = inflater.inflate(R.layout.item_meal, parent, false);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tvName);
            viewHolder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);
            viewHolder.tvPrice =  (TextView)convertView.findViewById(R.id.tvPrice);
            viewHolder.ibMinus = (ImageButton) convertView.findViewById(R.id.ibMinus);
            viewHolder.ibPlus = (ImageButton) convertView.findViewById(R.id.ibPlus);
            viewHolder.tvCounts = (TextView) convertView.findViewById(R.id.tvCounts);
            convertView.setTag(viewHolder);
            
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(meal.getName());
        viewHolder.tvDescription.setText(meal.getDescription());
        viewHolder.tvPrice.setText("$" + meal.getPrice());
                
        viewHolder.ibMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int counts = Integer.parseInt(viewHolder.tvCounts.getText().toString());
                if(counts > 0){
                    viewHolder.tvCounts.setText((counts -1) + "");
                }
            }
        });

        viewHolder.ibPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int counts = Integer.parseInt(viewHolder.tvCounts.getText().toString());

                viewHolder.tvCounts.setText((counts + 1)+"" );

            }
        });
        viewHolder.ivImage.setImageResource(0);

        int deviceWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
        Picasso.with(getContext()).load(meal.getImageUrl()).resize(deviceWidth, 0).into(viewHolder.ivImage);
        return convertView;
    }


}
