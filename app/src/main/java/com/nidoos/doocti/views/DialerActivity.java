package com.nidoos.doocti.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import com.github.nkzawa.socketio.client.Socket;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nidoos.doocti.R;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.info.AutoDialInfo;
import com.nidoos.doocti.info.CallInfo;
import com.nidoos.doocti.info.CustomerInfo;
import com.nidoos.doocti.info.PbxInfo;
import com.nidoos.doocti.info.PermissionInfo;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.response.CallStatusResponse;
import com.nidoos.doocti.response.MainFilterResponse;
import com.nidoos.doocti.retrofit.RetrofitApiClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.socket.EventListener;
import com.nidoos.doocti.sqliteDB.SqliteDB;
import com.nidoos.doocti.utils.ApplicationInsight;
import com.nidoos.doocti.utils.Internet;
import com.nidoos.doocti.utils.MyPhoneStateListener;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.utils.SipManager;
import com.nidoos.doocti.views.Fragments.AutoDialerFragment;
import com.nidoos.doocti.views.Fragments.ContactsFragment;
import com.nidoos.doocti.views.Fragments.DialerFragment;
import com.nidoos.doocti.views.Fragments.ProfileFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DialerActivity extends AppCompatActivity {


    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_PERMISSIONS = 123;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fb_call_screen)
    FloatingActionButton fb_call_screen;
    @BindView(R.id.nav_view)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.pb_loader_dialer)
    ProgressBar pb_loader_dialer;

    private Doocti app;
    private Socket mSocket;

    private SharedPreferences sharedpreferences;
    private boolean internetDisconnected = false;
    private boolean doubleBackToExitPressedOnce = false;

    private SqliteDB sqliteDB;
    private ApplicationInsight applicationInsight;

    private ImageView iv_create_call_activity, iv_call;
    private EditText et_phone_number;

    private UserInfo userInfo;
    private PermissionInfo permissionInfo;
    private AutoDialInfo autoDialInfo;
    private CallInfo callInfo;
    private CustomerInfo customerInfo;
    private ApiInfo apiInfo;
    private PbxInfo pbxInfo;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialer);
        ButterKnife.bind(this);

        pb_loader_dialer.setVisibility(View.VISIBLE);

        iv_create_call_activity = toolbar.findViewById(R.id.iv_create_call_activity);
        iv_call = toolbar.findViewById(R.id.iv_call);
        et_phone_number = toolbar.findViewById(R.id.et_phone_number);



        Log.d("dialeractivity","dialeractivity");

        SipManager sipManager = new SipManager(getApplicationContext());
        sipManager.register();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneStateListener(getApplicationContext()), PhoneStateListener.LISTEN_CALL_STATE);


        app = (Doocti) getApplicationContext();

        app.socketConnect();
        mSocket = app.getSocket();

        try {
            if (!mSocket.connected()) {
                Intent intent = new Intent(DialerActivity.this, EventListener.class);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        applicationInsight = new ApplicationInsight(getApplicationContext(), getApplication());
        applicationInsight.trackPageView("DialerActivity", "");

        sqliteDB = new SqliteDB(this);

        getInfosFromSharedPreferences();

        updateUI();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pb_loader_dialer.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commitAllowingStateLoss();
            }
        },800);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_dialer:
                        FragmentManager dialer1 = getSupportFragmentManager();
                        dialer1.beginTransaction().replace(R.id.content_frame, new DialerFragment()).commit();
                        return true;
                    case R.id.navigation_contacts:
                        FragmentManager contacts = getSupportFragmentManager();
                        contacts.beginTransaction().replace(R.id.content_frame, new ContactsFragment()).commit();
                        return true;
                    case R.id.navigation_auto_dial:
                        FragmentManager auto_dial = getSupportFragmentManager();
                        auto_dial.beginTransaction().replace(R.id.content_frame, new AutoDialerFragment()).commit();
                        return true;
                    case R.id.navigation_profile:
                        FragmentManager profile = getSupportFragmentManager();
                        profile.beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
                        return true;
                }
                return false;
            }
        });


        Log.i("userInfo", userInfo.toString());
        Log.i("permissionInfo", permissionInfo.toString());
        Log.i("pbxInfoObj", pbxInfo.toString());
        Log.i("autoDialInfo", autoDialInfo.toString());
        Log.i("callInfo", callInfo.toString());
        Log.i("customerInfo", customerInfo.toString());
        Log.i("apiInfo", apiInfo.toString());


        iv_create_call_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DialerActivity.this, CreateCallActivity.class);
                startActivity(intent);
            }
        });

        Handler handler = new Handler();
        handler.post(getSocketConnectionContinuousRunnable(handler));
        handler.post(getSipRegisterRunnable(handler));

        iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = et_phone_number.getText().toString();
                if (!number.isEmpty()) {
                    userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
                    Log.e("agentStatus", userInfo.getStatus());

                    if (userInfo.getStatus().equals("Available")) {
                        com.nidoos.doocti.utils.Call call = new com.nidoos.doocti.utils.Call(getApplicationContext(), getApplication(), mSocket);
                        call.clickToCall(number, "MANUAL", "");
                    } else {
                        Toast.makeText(DialerActivity.this, "Unable to make call", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    et_phone_number.setError("Enter Mobile Number");
                    et_phone_number.requestFocus();
                }
            }
        });

        fb_call_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DialerActivity.this, CallScreenActivity.class));
                finish();
                applicationInsight.trackEvent("CallPopup", "Re-initiated");
            }
        });


        if (sharedpreferences.getBoolean(SharedPrefConstants.DOMAIN_SELECT, false)) {
            try {
                getMainFilter();
                getCallStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        checkPermissions();

    }

    private Runnable getSipRegisterRunnable(Handler handler) {
        return new Runnable() {
            @Override
            public void run() {
                boolean sipAccount = sharedpreferences.getBoolean(SharedPrefConstants.SIP_REGISTERED, false);
                if (sipAccount) {

                    SipManager sipManager1 = new SipManager(getApplicationContext());
                    Log.e("sipRegister", "" + sipManager1.isRegistered());
                    if (!sipManager1.isRegistered()) {
                        sipManager1.register();
                    }

                    handler.postDelayed(this, 5000);

                }
            }
        };
    }

    private Runnable getSocketConnectionContinuousRunnable(Handler handler) {
        return new Runnable() {
            @Override
            public void run() {
                boolean error = false;


                if(SharedPrefHelper.getUserInfo(sharedpreferences) == null)
                    error = true;

                if (sharedpreferences.getBoolean(SharedPrefConstants.FORCE_LOGOUT, false)
                        && !userInfo.getStatus().equals("InCall") && !autoDialInfo.getStatus().equals("start")) {

                    getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ProfileFragment()).commit();
                }

                if (!Internet.isConnected(getApplicationContext()) && (userInfo.getStatus().equals("Available") || userInfo.getStatus().equals("InCall"))) {

                    Toast.makeText(DialerActivity.this, "Please check your network connection", Toast.LENGTH_SHORT).show();
                    internetDisconnected = true;

                } else if (internetDisconnected) {

                    internetDisconnected = false;

                    if (!mSocket.connected()) {
                        mSocket.connect();
                    }
//                    Toast.makeText(DialerActivity.this, "Re-connecting...", Toast.LENGTH_SHORT).show();

                    syncQueue();
                }

                if(mSocket == null) {
                    app.socketConnect();
                }

                if (!mSocket.connected() && !userInfo.getStatus().equals("")) {
                        internetDisconnected = true;
                }

                if(!error) {
                    handler.postDelayed(this, 3000);
                }
            }
        };
    }

    private void syncQueue() {
        mSocket = app.getSocket();
        JSONObject queueSync = new JSONObject();
        try {
            queueSync.put("tenant_id", userInfo.getTenantId());
            queueSync.put("station", userInfo.getExtension());
            queueSync.put("uniqueid", callInfo.getUniqueId());
            queueSync.put("action", "check-call-status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket.connected() && (userInfo.getStatus().equals("Available") || userInfo.getStatus().equals("InCall"))) {
            mSocket.emit("check-call-status", queueSync);
            Log.e("event TX","check-call-status,"+ queueSync.toString());
            ApplicationInsight applicationInsight = new ApplicationInsight(getApplicationContext(), getApplication());
            applicationInsight.trackEvent("Click To Call", queueSync.toString());
        }
    }

    private void updateUI() {
        if (!permissionInfo.getViewCallLog()) {
            bottomNavigationView.getMenu().removeItem(R.id.navigation_dialer);
        }
        if (!permissionInfo.getViewContacts()) {
            bottomNavigationView.getMenu().removeItem(R.id.navigation_contacts);
        }
        if (!permissionInfo.getAutoDial()) {
            bottomNavigationView.getMenu().removeItem(R.id.navigation_auto_dial);
        }
        if (!permissionInfo.getClickToCall()) {
            et_phone_number.setVisibility(View.INVISIBLE);
            iv_call.setVisibility(View.INVISIBLE);
        }
        if (!permissionInfo.getCrmEnable()) {
            iv_create_call_activity.setVisibility(View.INVISIBLE);
        }

        if (userInfo.getStatus().equals("InCall")) {
            fb_call_screen.setVisibility(View.VISIBLE);
        }

        bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
    }

    private void getInfosFromSharedPreferences() {
        sharedpreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
        permissionInfo = SharedPrefHelper.getPermissionInfo(sharedpreferences);
        apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);
        callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
        customerInfo = SharedPrefHelper.getCustomerInfo(sharedpreferences);
        autoDialInfo = SharedPrefHelper.getAutoDialInfo(sharedpreferences);
        pbxInfo = SharedPrefHelper.getPbxInfo(sharedpreferences);
    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void checkPermissions() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }


    private void getCallStatus() {
        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<CallStatusResponse> call = services.getCallStatus(userInfo.getToken(), userInfo.getCrmDomain());
        call.enqueue(new Callback<CallStatusResponse>() {
            @Override
            public void onResponse(@NonNull Call<CallStatusResponse> call, @NonNull Response<CallStatusResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        List<CallStatusResponse.Datum> group;
                        if (response.isSuccessful()) {
                            group = response.body().getData();
                            if (group.size() > 0) {
                                sqliteDB.delete_rows("LEADCALLSTATUS");
                            }
                            for (int i = 0; i < group.size(); i++) {
                                sqliteDB.insert_values("LEADCALLSTATUS", group.get(i).getName(), group.get(i).getValue());
                            }
                            if (group.size() > 0) {
                                sqliteDB.delete_rows("CONTACTCALLSTATUS");
                            }
                            for (int i = 0; i < group.size(); i++) {
                                sqliteDB.insert_values("CONTACTCALLSTATUS", group.get(i).getName(), group.get(i).getValue());
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<CallStatusResponse> call, @NonNull Throwable t) {
                applicationInsight.trackEvent("CallStatus Exception", t.toString());
            }
        });
    }

    private void getMainFilter() {
        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<MainFilterResponse> call = services.getMainFilter(userInfo.getToken(), userInfo.getCrmDomain());

        call.enqueue(new Callback<MainFilterResponse>() {
            @Override
            public void onResponse(@NonNull Call<MainFilterResponse> call, @NonNull Response<MainFilterResponse> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.code() == 200) {
                            List<MainFilterResponse.Datum> group;
                            if (response.isSuccessful()) {
                                if(response.body().getData() != null) {
                                    group = response.body().getData();
                                    if (group.size() > 0) {
                                        sqliteDB.delete_rows("MAINFILTER");
                                    }
                                    for (int i = 0; i < group.size(); i++) {
                                        sqliteDB.insert_values("MAINFILTER", group.get(i).getName(), group.get(i).getValue());
                                    }
                                } else {
                                    sqliteDB.delete_rows("MAINFILTER");
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MainFilterResponse> call, @NonNull Throwable t) {
                applicationInsight.trackEvent("MainFilter Exception", t.toString());
            }
        });
    }


    public void clickToCall(String number, String call_type) {
            userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
            if (userInfo.getStatus().equals("Available")) {
                com.nidoos.doocti.utils.Call call = new com.nidoos.doocti.utils.Call(getApplicationContext(), getApplication(), mSocket);
                call.clickToCall(number, call_type, "");
            } else {
                Toast.makeText(DialerActivity.this, "Unable to make call", Toast.LENGTH_SHORT).show();
            }
    }


}
