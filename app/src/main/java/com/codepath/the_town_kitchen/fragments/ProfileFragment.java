package com.codepath.the_town_kitchen.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.models.User;
import com.facebook.widget.ProfilePictureView;
import com.makeramen.RoundedImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by pramos on 2/25/15.
 */
public class ProfileFragment extends Fragment {

    private ProfilePictureView profilePictureView;
    private RoundedImageView ivProfile;
    private TextView tvUserName, tvEmail;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Defines the xml file for the fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        setupProfile(view);
        return view;
    }

    private void setupProfile(View v) {
        ivProfile = (RoundedImageView) v.findViewById(R.id.ivProfile);
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        profilePictureView = (ProfilePictureView) v.findViewById(R.id.ivFacebookProfile);

        User currentUser = TheTownKitchenApplication.getCurrentUser().getUser();
        if (currentUser != null) {
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
}
