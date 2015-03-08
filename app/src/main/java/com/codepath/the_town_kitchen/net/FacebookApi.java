package com.codepath.the_town_kitchen.net;

import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONObject;

/**
 * Created by xdai on 3/7/15.
 */
public class FacebookApi {
    private static FacebookApi instance = null;


    public static FacebookApi getInstance() {
        if(instance == null) {
            instance = new FacebookApi();
        }
        return instance;
    }
    
    public void getUser(Session session, final IResponseHandler handler ){
        new Request(
                session,
                "/me",
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        if (response.getGraphObject() != null) {
                            handler.handle(response.getGraphObject().getInnerJSONObject());
                        }
                    }
                }
        ).executeAsync();
        
    }

public LoginButton.UserInfoChangedCallback getUserInfoChangedCallback(final IResponseHandler handler){
   return new LoginButton.UserInfoChangedCallback() {
        @Override
        public void onUserInfoFetched(GraphUser graphUser) {
            if (graphUser != null) {
                if(TheTownKitchenApplication.getCurrentUser().getUser()!=null) {

                    handler.handle(graphUser.getInnerJSONObject());
                }

            } else {

            }
        }
    };
    
}
    public interface IResponseHandler{
        void handle(JSONObject json);
        
    }
}
