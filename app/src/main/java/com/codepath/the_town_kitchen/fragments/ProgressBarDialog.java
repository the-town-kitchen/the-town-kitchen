package com.codepath.the_town_kitchen.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codepath.the_town_kitchen.R;

public class ProgressBarDialog extends DialogFragment {

    public ProgressBarDialog(){

    }

    public static ProgressBarDialog newInstance() {
        ProgressBarDialog frag = new ProgressBarDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_progress_bar, container);
        Toast.makeText(getActivity().getApplicationContext(), "Your order's on its way!", Toast.LENGTH_LONG).show();
        return view;
    }

}
