package com.codepath.the_town_kitchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.adapters.FounderAdapter;
import com.codepath.the_town_kitchen.models.Founder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pramos on 2/25/15.
 */
public class AboutFragment extends Fragment {
    private FounderAdapter mAdapter;
    private  RecyclerView rvContacts;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        // Setup handles to view objects here
        // etFoo = (EditText) view.findViewById(R.id.etFoo);
        rvContacts = (RecyclerView) view.findViewById(R.id.rvContacts);

        // allows for optimizations
        rvContacts.setHasFixedSize(true);

        // Define 2 column grid layout
        final GridLayoutManager layout = new GridLayoutManager(getActivity(), 2);

        // Unlike ListView, you have to explicitly give a LayoutManager to the RecyclerView to position items on the screen.
        // There are three LayoutManager provided at the moment: GridLayoutManager, StaggeredGridLayoutManager and LinearLayoutManager.
        rvContacts.setLayoutManager(layout);
        contacts = new ArrayList<>();

        // Create an adapter
        mAdapter = new FounderAdapter(getActivity(), contacts);

        // Bind adapter to list
        rvContacts.setAdapter(mAdapter);

      
        getFounders();
        return view;

    }
   private  List<Founder> contacts;
    private void getFounders() {
        ParseQuery<Founder> query = ParseQuery.getQuery(Founder.class);
       
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<Founder>() {
            public void done(List<Founder> founders, ParseException e) {
                if (e == null || e.getCode() == 101) {
                    // get data
                    contacts.clear();
                    contacts.addAll(founders);
                    mAdapter.notifyDataSetChanged();
                            
                } else {
                    Log.d("Founder", "Error: " + e.getMessage());
                }
            }
        });
    }
}
