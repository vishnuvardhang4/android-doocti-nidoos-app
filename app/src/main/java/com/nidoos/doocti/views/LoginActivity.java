package com.nidoos.doocti.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.ParameterBuilder;
import com.auth0.android.authentication.storage.SecureCredentialsManager;
import com.auth0.android.authentication.storage.SharedPreferencesStorage;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.info.PermissionInfo;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.R;
import com.nidoos.doocti.retrofit.JWTUtils;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.utils.SharedPrefHelper;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nidoos.doocti.constants.SharedPrefConstants.OTP;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.bt_login)
    Button bt_login;

    @BindView(R.id.et_extension)
    EditText et_extension;

    @BindView(R.id.pb_loader)
    ProgressBar pb_loader;

    @BindView(R.id.rl_login)
    RelativeLayout rl_login;

    @BindView(R.id.ib_info)
    ImageButton ib_info;

    public static final String KEY_CLEAR_CREDENTIALS = "com.auth0.CLEAR_CREDENTIALS";


    private Socket mSocket;
    private Doocti app;
    private SharedPreferences sharedpreferences;
    private String token, login, username = "", email = "", extension, userID = "", tenant_code = "", profile_picture = "";
    private Auth0 auth0;
    private SecureCredentialsManager credentialsManager;
    private AuthenticationAPIClient authentication;

    private UserInfo userInfoObject;
    private ApiInfo apiInfo;
    private PermissionInfo permissionInfoObject;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//                    .detectNonSdkApiUsage()
//                    .penaltyLog()
//                    .build());
//        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        app = (Doocti) getApplication();
        mSocket = app.getSocket();
        bt_login.setEnabled(true);

        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        credentialsManager = new SecureCredentialsManager(this, new AuthenticationAPIClient(auth0), new SharedPreferencesStorage(this));
        authentication = new AuthenticationAPIClient(auth0);

        sharedpreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        login = sharedpreferences.getString(SharedPrefConstants.LOGIN, "");
        String userInfo = sharedpreferences.getString(SharedPrefConstants.USER_INFO, "");
        String permissionInfo = sharedpreferences.getString(SharedPrefConstants.PERMISSION_INFO, "");

//        sharedpreferences.edit().putString("environment", "production").apply();

        if (!userInfo.isEmpty()) {
                userInfoObject = new Gson().fromJson(userInfo, UserInfo.class);
                permissionInfoObject = new Gson().fromJson(sharedpreferences.getString(SharedPrefConstants.PERMISSION_INFO, ""),PermissionInfo.class);
        }

        ib_info.setVisibility(View.GONE);
        Log.i("userInfo", userInfo);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (login.equals("true") && userInfoObject.getStatus().equals("InCall") && permissionInfoObject.getPopup()) {
                    Intent intent = new Intent(LoginActivity.this, CallScreenActivity.class);
                    startActivity(intent);
                } else if (login.equals("true")) {
                    Intent intent = new Intent(LoginActivity.this, DialerActivity.class);
                    startActivity(intent);
                } else {
                    login();
                }

            }
        }, 800);


        // Check if the activity was launched after a logout
        if (getIntent().getBooleanExtra(KEY_CLEAR_CREDENTIALS, false)) {
            credentialsManager.clearCredentials();
        }


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extension = et_extension.getText().toString();
                if (extension.isEmpty()) {
                    et_extension.requestFocus();
                    et_extension.setError("Phone number is required");
                } else {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            login();
                        }
                    }, 1000);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)

    private void login() {

        Log.e("data", getString(R.string.com_auth0_audience));

        pb_loader.setVisibility(View.VISIBLE);
        rl_login.setVisibility(View.GONE);
        ParameterBuilder builder = ParameterBuilder.newBuilder();
        Map<String, Object> parameters = builder
                .set("prompt", "login")
                .asDictionary();

        WebAuthProvider.init(auth0)
                .withScheme("demo")
                .withAudience(getString(R.string.com_auth0_audience))
                .withParameters(parameters)
                .withScope("openid profile offline_access email read:current_user update:current_user_metadata")
                .start(LoginActivity.this, new AuthCallback() {
                    @Override
                    public void onFailure(@NonNull final Dialog dialog) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb_loader.setVisibility(View.GONE);
                                rl_login.setVisibility(View.VISIBLE);
                                dialog.show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(final AuthenticationException exception) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pb_loader.setVisibility(View.GONE);
                                rl_login.setVisibility(View.VISIBLE);
                                Toast.makeText(LoginActivity.this, "Error: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onSuccess(@NonNull final Credentials credentials) {
                        runOnUiThread(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void run() {
                                try {
                                    authentication
                                            .userInfo(Objects.requireNonNull(credentials.getAccessToken()))
                                            .start(new BaseCallback<UserProfile, AuthenticationException>() {
                                                @Override
                                                public void onSuccess(UserProfile information) {
                                                    try {
                                                        token = "Bearer " + credentials.getAccessToken();
                                                        email = information.getEmail();
                                                        profile_picture = information.getPictureURL();
                                                        String decoded_token = JWTUtils.decoded(credentials.getAccessToken());
                                                        JSONObject tokenObject = new JSONObject(decoded_token);
                                                        tenant_code = tokenObject.getString("http://api.doocti.com/claims/tenant_code");



                                                        userInfoObject = new UserInfo();
                                                        userInfoObject.setToken(token);
                                                        userInfoObject.setEmail(email);
                                                        userInfoObject.setTenantId(tenant_code);
                                                        userInfoObject.setProfilePicture(profile_picture);



                                                        String dialerDomain = "https://" + tokenObject.getString("http://api.doocti.com/claims/dialer_domain");
                                                        String apiDomain = "https://" + tokenObject.getString("http://api.doocti.com/claims/api_domain");
                                                        String socketDomain = "https://" + tokenObject.getString("http://api.doocti.com/claims/socket_domain");

                                                        apiInfo = new ApiInfo(
                                                                dialerDomain,
                                                                apiDomain,
                                                                socketDomain
                                                        );


                                                        sharedpreferences.edit()
                                                                .putString(SharedPrefConstants.USER_INFO,new Gson().toJson(userInfoObject))
                                                                .putString(SharedPrefConstants.API_INFO, new Gson().toJson(apiInfo))
                                                                .apply();

                                                        credentialsManager.saveCredentials(credentials);

                                                        startActivity(new Intent(LoginActivity.this, PhoneNumberSipActivity.class));


                                                    } catch (Exception e) {
                                                        runOnUiThread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                pb_loader.setVisibility(View.GONE);
                                                                rl_login.setVisibility(View.VISIBLE);
                                                            }
                                                        });
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(AuthenticationException error) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            pb_loader.setVisibility(View.GONE);
                                                            rl_login.setVisibility(View.VISIBLE);
                                                        }
                                                    });
                                                    //user information request failed
                                                }
                                            });
                                } catch (Exception e) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            pb_loader.setVisibility(View.GONE);
                                            rl_login.setVisibility(View.VISIBLE);
                                        }
                                    });
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });
    }




}


