package com.perfecto.apps.ocr.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.perfecto.apps.ocr.R;
import com.perfecto.apps.ocr.tools.Perfecto;
import com.perfecto.apps.ocr.tools.VolleyClass;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hosam Azzam on 14/08/2017.
 */

public class Register_Fragment extends Fragment {
    private static final int RC_SIGN_IN = 9001;
    EditText email, password, confirm_posword, name, username, phone;
    TextView login;
    ImageView fb_btn, gp_btn, tw_btn;
    CardView register_btn;
    VolleyClass volley;
    String token = "", type = "";
    CallbackManager callbackManager;
    FacebookCallback<LoginResult> FacebookCallback;
    TwitterLoginButton twitterLoginButton;
    ImageView home_ico;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Twitter.initialize(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.register_fragment, container, false);
        volley = VolleyClass.getInstance(getContext());

        home_ico = getActivity().findViewById(R.id.toolbar_home_ico);
        home_ico.setVisibility(View.GONE);

        email = rootview.findViewById(R.id.user_email_txt);
        password = rootview.findViewById(R.id.user_password_txt);
        confirm_posword = rootview.findViewById(R.id.user_confirm_password_txt);
        name = rootview.findViewById(R.id.user_name_txt);
        username = rootview.findViewById(R.id.user_username_txt);
        phone = rootview.findViewById(R.id.user_phone_txt);
        login = rootview.findViewById(R.id.login_btn);

        fb_btn = rootview.findViewById(R.id.fb_btn);
        gp_btn = rootview.findViewById(R.id.google_btn);
        tw_btn = rootview.findViewById(R.id.twitter_btn);

        register_btn = rootview.findViewById(R.id.register_btn);

        twitterLoginButton = new TwitterLoginButton(getContext());


        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!email.getText().toString().equals("") && !password.getText().toString().equals("") &&
                        !name.getText().toString().equals("") && !phone.getText().toString().equals("") &&
                        !username.getText().toString().equals("")) {
                    if (password.getText().toString().equals(confirm_posword.getText().toString())) {
                        if (token.equals("")) {
                            register("", "normal", "register");
                        } else {
                            register(token, "social", "registerwith");
                        }
                    } else {
                        Toast.makeText(getContext(), getResources().getString(R.string.password_not_matach), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.fill_all_box), Toast.LENGTH_SHORT).show();
                }
            }
        });

        fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile", "email"));
                LoginManager.getInstance().registerCallback(callbackManager, FacebookCallback);
            }
        });

        gp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
                System.out.println("enter");
            }
        });

        tw_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterLoginButton.performClick();
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mGoogleApiClient.stopAutoManage(getActivity());
                    mGoogleApiClient.disconnect();
                    getFragmentManager().popBackStack();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        fbLogin();
        gpLogin();
        twLogin();

        return rootview;
    }

    public void register(final String token, final String apitype, String link) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", getResources().getString(R.string.pls_wait), false, false);
        final StringRequest stringRequest = new StringRequest(Request.Method.POST, Perfecto.BASE_URL + link, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    System.out.println(response);
                    JSONParser jsonParser = new JSONParser();
                    Object obj;
                    JSONObject mainObject;
                    obj = jsonParser.parse(response);
                    mainObject = (JSONObject) obj;

                    JSONObject statusObject = (JSONObject) jsonParser.parse(mainObject.get("status").toString());
                    if (statusObject.get("type").equals("success")) {
                        Toast.makeText(getContext(), mainObject.get("Response").toString() + ", Now you can login", Toast.LENGTH_SHORT).show();
                        try {
                            getFragmentManager().popBackStack();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getContext(), mainObject.get("Response").toString(), Toast.LENGTH_SHORT).show();
                        //  Toast.makeText(getContext(), "Email or Username has already been taken", Toast.LENGTH_SHORT).show();
                    }
                    loading.dismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), getResources().getString(R.string.something_wrong), Toast.LENGTH_SHORT).show();
                    loading.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.dismiss();
                Toast.makeText(getContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (apitype.equals("normal")) {
                    params.put("email", email.getText().toString());
                    params.put("phone", phone.getText().toString());
                    params.put("name", name.getText().toString());
                    params.put("username", username.getText().toString());
                    params.put("password", password.getText().toString());
                } else {
                    params.put("token", token);
                    params.put("email", email.getText().toString());
                    params.put("phone", phone.getText().toString());
                    params.put("name", name.getText().toString());
                    params.put("username", username.getText().toString());
                    params.put("password", password.getText().toString());
                    if (type.equals("fb")) {
                        params.put("is_facebook", "1");
                    } else if (type.equals("gp")) {
                        params.put("is_google", "1");
                    } else if (type.equals("tw")) {
                        params.put("is_twitter", "1");
                    }
                }
                System.out.println(params.toString());

                return params;
            }
        };

        volley.getQueue().add(stringRequest);
    }

    public void fbLogin() {
        callbackManager = CallbackManager.Factory.create();
        FacebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //   finish();
                System.out.println("login done  " + AccessToken.getCurrentAccessToken().getToken());
                Toast.makeText(getContext(), getResources().getString(R.string.fetch_complate), Toast.LENGTH_LONG).show();
                AccessToken.getCurrentAccessToken().getToken();


                System.out.println("onSuccess");
                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getResources().getString(R.string.pls_wait));
                progressDialog.show();
                String accessToken = AccessToken.getCurrentAccessToken().getToken();

                System.out.println("accessToken " + accessToken);

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(org.json.JSONObject object, GraphResponse response) {

                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);
                        System.out.println("result " + bFacebookData.toString());
                        progressDialog.dismiss();
                        name.setText(bFacebookData.getString("first_name") + " " + bFacebookData.getString("last_name"));
                        email.setText(bFacebookData.getString("email"));
                        token = bFacebookData.getString("idFacebook");
                        type = "fb";

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender, birthday, location"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
                // App code
            }

            @Override
            public void onCancel() {
                System.out.println("user cancel");
                Toast.makeText(getContext(), getResources().getString(R.string.user_cancle), Toast.LENGTH_LONG).show();
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println("user error");
                exception.printStackTrace();
                Toast.makeText(getContext(), getResources().getString(R.string.fetch_error), Toast.LENGTH_LONG).show();
                // App code
            }
        };

    }


    private Bundle getFacebookData(org.json.JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");

                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));
            if (object.has("birthday"))
                bundle.putString("birthday", object.getString("birthday"));
            if (object.has("location"))
                bundle.putString("location", object.getJSONObject("location").getString("name"));

            return bundle;
        } catch (JSONException e) {

        }
        return new Bundle();
    }


    public void gpLogin() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestId()
                    .requestIdToken("1000027373491-63hkaeknmc5colpb5c84noj27j3f4s27.apps.googleusercontent.com")
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .enableAutoManage(getActivity() /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        }
                    } /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();
        }


    }


    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            name.setText(acct.getDisplayName());
            email.setText(acct.getEmail());
            token = acct.getId();
            type = "gp";
            System.out.println("google token: " + token);
            Toast.makeText(getContext(), getResources().getString(R.string.fetch_complate), Toast.LENGTH_LONG).show();
            //  Toast.makeText(getContext(), "google token: " + token, Toast.LENGTH_LONG).show();
        }
    }

    private void twLogin() {
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                name.setText(result.data.getUserName());
                token = String.valueOf(result.data.getUserId());
                System.out.println("twitter : " + token);
                type = "tw";
                Toast.makeText(getContext(), getResources().getString(R.string.fetch_complate), Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getContext(), getResources().getString(R.string.fetch_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //  Toast.makeText(getContext(), "requested", Toast.LENGTH_LONG).show();
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);

    }
}
