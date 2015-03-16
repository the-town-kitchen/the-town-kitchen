package com.codepath.the_town_kitchen.adapters;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import com.codepath.the_town_kitchen.R;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by paulina on 3/16/15.
 */
public class DeliveryPinAdapter implements GoogleMap.InfoWindowAdapter {
    LayoutInflater mInflater;

    public DeliveryPinAdapter(LayoutInflater i){
        mInflater = i;
    }

    // This defines the contents within the info window based on the marker
    @Override
    public View getInfoContents(Marker marker) {
        // Getting view from the layout file
        View v = mInflater.inflate(R.layout.delivery_pin_marker, null);
        // Populate fields
        TextView title = (TextView) v.findViewById(R.id.tvMarkerTitle);
        title.setText(marker.getTitle());

        // Return info window contents
        return v;
    }

    // This changes the frame of the info window; returning null uses the default frame.
    // This is just the border and arrow surrounding the contents specified above
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
}