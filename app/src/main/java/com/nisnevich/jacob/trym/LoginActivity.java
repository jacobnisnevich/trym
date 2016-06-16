package com.nisnevich.jacob.trym;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    TextView loginText;
    TextView registerText;
    LinearLayout registerFields;
    Button loginButton;
    Button registerButton;
    CallbackManager callbackManager;
    LoginButton fbLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Get elements by id
        loginText = (TextView) findViewById(R.id.login_switch);
        registerText = (TextView) findViewById(R.id.register_switch);
        registerFields = (LinearLayout) findViewById(R.id.register_fields);
        loginButton = (Button) findViewById(R.id.login_button);
        registerButton = (Button) findViewById(R.id.register_button);
        fbLoginButton = (LoginButton) findViewById(R.id.fb_login_button);

        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        fbLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        // Check if user already logged in
        if (isLoggedIn()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        fbLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            String email = object.getString("email");
                            Log.v("LoginActivity", email);
                        } catch (JSONException e) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setMessage("JSONException: " + e.getMessage());
                        }
                    }
                });
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException e) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                builder.setMessage("FacebookException: " + e.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void onLoginSwitchClick(android.view.View view) {
        Context context = view.getContext();

        loginText.setBackgroundColor(ContextCompat.getColor(context, R.color.loginActive));
        registerText.setBackgroundColor(ContextCompat.getColor(context, R.color.loginInactive));
        registerFields.setVisibility(View.GONE);
        loginButton.setVisibility(View.VISIBLE);
        registerButton.setVisibility(View.GONE);
    }

    public void onRegisterSwitchClick(android.view.View view) {
        Context context = view.getContext();

        registerText.setBackgroundColor(ContextCompat.getColor(context, R.color.loginActive));
        loginText.setBackgroundColor(ContextCompat.getColor(context, R.color.loginInactive));
        registerFields.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.GONE);
        registerButton.setVisibility(View.VISIBLE);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }
}
