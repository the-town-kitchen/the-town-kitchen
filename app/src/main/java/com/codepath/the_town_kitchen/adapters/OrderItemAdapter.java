package com.codepath.the_town_kitchen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.models.OrderItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OrderItemAdapter extends ArrayAdapter<OrderItem> {

    public interface IActionClickListener{
        void onActionClicked(int position, int count);
    }

    private IActionClickListener actionClickListener;
    private static class ViewHolder {
        public ImageView ivMealImage;
        public TextView tvMealName;
        public TextView tvQuantity;
        public TextView tvPrice;

    }

    public OrderItemAdapter(Context context, List<OrderItem> items, IActionClickListener actionClickListener) {
        super(context, 0, items);
        this.actionClickListener = actionClickListener;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OrderItem orderItem = getItem(position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_order, parent, false);
            viewHolder.ivMealImage = (ImageView)convertView.findViewById(R.id.ivMealImage);
            viewHolder.tvMealName = (TextView)convertView.findViewById(R.id.tvMealName);
            viewHolder.tvQuantity = (TextView)convertView.findViewById(R.id.tvQuantity);
            viewHolder.tvPrice =  (TextView)convertView.findViewById(R.id.tvPrice);
           
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvMealName.setText(orderItem.getMeal().getDescription());
        viewHolder.tvQuantity.setText(orderItem.getQuantity() + "");
        viewHolder.tvPrice.setText("$" + orderItem.getMeal().getPrice());
        viewHolder.ivMealImage.setImageResource(0);

        //int deviceWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
        Picasso.with(getContext()).load(orderItem.getMeal().getImageUrl()).into(viewHolder.ivMealImage);
        return convertView;
    }



}
