package com.codepath.the_town_kitchen.net;

import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.models.User;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class FacebookApi {
    private static FacebookApi instance = null;


    public static FacebookApi getInstance() {
        if (instance == null) {
            instance = new FacebookApi();
        }
        return instance;
    }

    public void getUser(Session session, final IResponseHandler handler) {
        Request.newMeRequest(session, new Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser graphUser, Response response) {
                        if (response.getGraphObject() != null) {
                           User user = User.fromFacebookJson(response.getGraphObject().getInnerJSONObject());
                            handler.handle(user);
                        }
                    }
                }
        ).executeAsync();
    }

    public LoginButton.UserInfoChangedCallback getUserInfoChangedCallback(final IResponseHandler handler) {
        return new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser graphUser) {
                if (graphUser != null) {
                    if (TheTownKitchenApplication.getCurrentUser().getUser() != null) {
                           User user = User.fromFacebookJson(graphUser.getInnerJSONObject());
                           handler.handle(user);
                       // handler.handle(graphUser.getInnerJSONObject());
                    }

                } else {

                }
            }
        };

    }

    public interface IResponseHandler {
        void handle(User user);

    }
}
