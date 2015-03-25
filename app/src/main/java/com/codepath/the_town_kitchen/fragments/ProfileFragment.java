package com.codepath.the_town_kitchen.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.activities.LoginActivity;
import com.codepath.the_town_kitchen.models.User;
import com.codepath.the_town_kitchen.net.GoogleApi;
import com.facebook.widget.ProfilePictureView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by pramos on 2/25/15.
 */
public class ProfileFragment extends Fragment implements 
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener { 

    private ProfilePictureView profilePictureView;
    private RoundedImageView ivProfile;
    private TextView tvUserName, tvEmail;
    private Button btLogout;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mGoogleApiClient = GoogleApi.getGoogleClient(getActivity(), this, this);
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ICountUpdateListener");
        }
        
      
    }
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupProfile(view);
        mGoogleApiClient = GoogleApi.getGoogleClient(getActivity(), this, this);
        return view;
    }

    private void setupProfile(View v) {
        ivProfile = (RoundedImageView) v.findViewById(R.id.ivProfile);
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        profilePictureView = (ProfilePictureView) v.findViewById(R.id.ivFacebookProfile);
        btLogout = (Button) v.findViewById(R.id.btLogout);
        User currentUser = TheTownKitchenApplication.getCurrentUser().getUser();
        if (currentUser != null) {
            btLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logoutClicked();
                }
            });
            if (currentUser.getProfileImageUrl() != null && !currentUser.getProfileImageUrl().isEmpty()) {
                Picasso.with(getActivity()).load(currentUser.getProfileImageUrl()).fit().into(ivProfile);
                ivProfile.setVisibility(View.VISIBLE);
                profilePictureView.setVisibility(View.GONE);

            } else if (currentUser.getFacebookId() != null && !currentUser.getFacebookId().isEmpty()) {

                profilePictureView.setCropped(true);
                profilePictureView.setProfileId(currentUser.getFacebookId());
                profilePictureView.setVisibility(View.VISIBLE);
                ivProfile.setVisibility(View.GONE);
            }
            tvUserName.setText(currentUser.getName());
            tvEmail.setText(currentUser.getEmail());
        }
    }

    private GoogleApiClient mGoogleApiClient;
    /**
     * Sign-out from google
     * */
    private void logoutClicked() {
        if(!mGoogleApiClient.isConnected()){

            mGoogleApiClient.connect();
        }else{
            logoutGooglePlus();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        logoutGooglePlus();
    }

    private void logoutGooglePlus() {
        if (mGoogleApiClient.isConnected()) {
        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
        mGoogleApiClient.disconnect();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
            getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
