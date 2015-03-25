package com.codepath.the_town_kitchen.activities;


import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.codepath.the_town_kitchen.R;
import com.codepath.the_town_kitchen.TheTownKitchenApplication;
import com.codepath.the_town_kitchen.models.User;
import com.codepath.the_town_kitchen.net.FacebookApi;
import com.codepath.the_town_kitchen.net.GoogleApi;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

import java.util.Arrays;

public class LoginActivity extends TheTownKitchenBaseActivity implements OnClickListener,
        ConnectionCallbacks, OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;
    private static final String TAG = LoginActivity.class.getSimpleName();

    private UiLifecycleHelper uiHelper;
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;
    private ConnectionResult mConnectionResult;

    private SignInButton googleLoginBtn;

    LoginButton facebookLoginBtn;
    private boolean isResumed = false;
   
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        /* video background */
        final Uri url = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.cookingbackground);
        final VideoView mVideoView = (VideoView) findViewById(R.id.video);
        mVideoView.setVideoURI(url);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mVideoView.start();
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.resume();
            }
        });

        facebookLoginBtn = (LoginButton) findViewById(R.id.fb_login_button);
        facebookLoginBtn.setApplicationId(getResources().getString(R.string.FACEBOOK_APP_ID));
        facebookLoginBtn.setReadPermissions(Arrays.asList("email", "public_profile"));

        facebookLoginBtn.setUserInfoChangedCallback(TheTownKitchenApplication.getFaceBookApi()
                        .getUserInfoChangedCallback(facebookApiHandler));

        googleLoginBtn = (SignInButton) findViewById(R.id.google_login_button);
        googleLoginBtn.setOnClickListener(this);
        setGooglePlusButtonText(googleLoginBtn);

        mGoogleApiClient = GoogleApi.getGoogleClient(this,this,this);

    }
    
    private GoogleApi.IResponseHandler googleApiHandler = new GoogleApi.IResponseHandler() {
        @Override
        public void handle(User user) {
            Log.d(TAG, "google user " + user);
            setCurrentUser(user);
        }
    };


    private FacebookApi.IResponseHandler facebookApiHandler = new FacebookApi.IResponseHandler() {
        @Override
        public void handle(User user) {
            Log.d(TAG, "facebook user " + user);
            setCurrentUser(user);
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    private void resolveSignInError() {
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (SendIntentException e) {
                mIntentInProgress = false;
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    0).show();
            return;
        }

        if (!mIntentInProgress) {
            // Store the ConnectionResult for later usage
            mConnectionResult = result;

            if (mSignInClicked) {
                resolveSignInError();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        super.onActivityResult(requestCode, requestCode, intent);
        uiHelper.onActivityResult(requestCode, responseCode, intent);
        
        if (requestCode == RC_SIGN_IN) {
            if (responseCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }

           
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        mSignInClicked = false;
       Log.d(TAG, "Google User is connected!");

        // Get user's information
        if (TheTownKitchenApplication.getCurrentUser().getUser() == null) {
            TheTownKitchenApplication.getGoogleApi().getUser(googleApiHandler);
        }
    }


    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
       
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
        signInWithGplus();

    }

    /**
     * Sign-in into google
     * */
    private void signInWithGplus() {
        if (!mGoogleApiClient.isConnecting()) {
            mSignInClicked = true;
            resolveSignInError();
        }
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        // Only make changes if the activity is visible
        if (isResumed) {
            // If the session state is open:
            if (state.isOpened()) {
                Log.i(TAG, "state opended");
                if (TheTownKitchenApplication.getCurrentUser().getUser() == null) {
                    TheTownKitchenApplication.getFaceBookApi().getUser(session, facebookApiHandler);
                }
            }
            else if (state.isClosed()) {
                // If the session state is closed:
                Log.i(TAG, "state closed");
            }
        }
    }

    User.IUserLoadedListener userLoadedListener = new User.IUserLoadedListener() {
        @Override
        public void handle(User user) {
            TheTownKitchenApplication.getCurrentUser().setUser(user);
            Intent intent = new Intent(LoginActivity.this, MealListActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    };
    
    private void setCurrentUser(final User user) {

        User.getParseUser(user, new User.IUserLoadedListener() {
            @Override
            public void handle(User parseUser) {
                if (parseUser == null) {
                    User.updateParseUser(user, userLoadedListener);
                } else {
                    userLoadedListener.handle(parseUser);
                }
            }
        });


    }

    private Session.StatusCallback callback =  new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    protected void setGooglePlusButtonText(SignInButton signInButton) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText("LOG IN WITH GOOGLE+");
                tv.setTextSize(16);
                return;
            }
        }
    }
}