package com.codepath.the_town_kitchen.net;


import android.content.Context;

import com.codepath.the_town_kitchen.models.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class GoogleApi {

    private static GoogleApi instance = null;

    public static GoogleApi getInstance() {
        if(instance == null) {
            instance = new GoogleApi();
        }
        return instance;
    }
    private static GoogleApiClient googleApiClient;

    public static GoogleApiClient getGoogleClient(Context context, GoogleApiClient.ConnectionCallbacks connectionCallback, OnConnectionFailedListener onConnectionFailedListener) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(connectionCallback)
                .addOnConnectionFailedListener(onConnectionFailedListener)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        return googleApiClient;
    }

    public void getUser(final IResponseHandler googleApiHandler) {
        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
            Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            User user = User.fromGooglePerson(person, googleApiClient);
            googleApiHandler.handle(user);
        }
    }

    public interface IResponseHandler {
            void handle(User user);

    }
}
