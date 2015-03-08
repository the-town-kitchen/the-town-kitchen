package com.codepath.the_town_kitchen.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.the_town_kitchen.DeviceDimensionsHelper;
import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.models.Meal;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MenuAdapter extends ArrayAdapter<Meal> {

private Context context;

    public interface IActionClickListener{
        void onActionClicked(int position, String action);
    }
    
    private IActionClickListener actionClickListener;
    private static class ViewHolder {
        public ImageView ivImage;
        public TextView tvName;
        public TextView tvDescription;
    }

    public MenuAdapter(Context context, List<Meal> menus, IActionClickListener actionClickListener) {
        super(context, 0, menus);
        this.context = context;
        this.actionClickListener = actionClickListener;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Meal menu = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_menu, parent, false);
            viewHolder.ivImage = (ImageView)convertView.findViewById(R.id.ivImage);
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tvName);
            viewHolder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);
            convertView.setTag(viewHolder);
            
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvName.setText(menu.getName());
        viewHolder.tvDescription.setText(menu.getDescription());

        viewHolder.ivImage.setImageResource(0);

        int deviceWidth = DeviceDimensionsHelper.getDisplayWidth(getContext());
        Picasso.with(getContext()).load(menu.getImageUrl()).resize(deviceWidth, 0).into(viewHolder.ivImage);
        return convertView;
    }


}
