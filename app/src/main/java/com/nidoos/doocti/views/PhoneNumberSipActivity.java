package com.nidoos.doocti.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.info.AutoDialInfo;
import com.nidoos.doocti.info.CallInfo;
import com.nidoos.doocti.info.CustomerInfo;
import com.nidoos.doocti.info.PbxInfo;
import com.nidoos.doocti.info.PermissionInfo;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.R;
import com.nidoos.doocti.request.LoginRequest;
import com.nidoos.doocti.response.CrmInfoResponse;
import com.nidoos.doocti.response.LoginResponse;
import com.nidoos.doocti.constants.RetrofitConstants;
import com.nidoos.doocti.retrofit.RetrofitAsterClient;
import com.nidoos.doocti.retrofit.RetrofitCommonClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.utils.SipManager;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nidoos.doocti.constants.SharedPrefConstants.AUTO_DIAL_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.CALL_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.CUSTOMER_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.LOGIN;
import static com.nidoos.doocti.constants.SharedPrefConstants.PBX_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.SHARED_PREF_NAME;
import static com.nidoos.doocti.constants.SharedPrefConstants.USER_INFO;

public class PhoneNumberSipActivity extends AppCompatActivity {

    final static String SHARED_PREF_SIP = "SHARED_PREF_SIP";
    final static String USERNAME_SIP = "USERNAME_SIP";
    final static String PASSWORD_SIP = "PASSWORD_SIP";
    final static String DOMAIN_SIP = "DOMAIN_SIP";

    EditText userNameSip, passwordSip, domainSip;
    Button registerDetailsSip;
    SipManager sipManager;
    EditText et_extension;
    Button bt_login;
    Button bt_send_otp;
    EditText et_otp;

    private SharedPreferences sharedpreferences;


    private UserInfo userInfo;
    private PermissionInfo permissionInfo;
    private PbxInfo pbxInfo;
    private AutoDialInfo autoDialInfo;
    private CallInfo callInfo;
    private CustomerInfo customerInfo;
    private ApiInfo apiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number_sip);

        userNameSip = findViewById(R.id.et_username_sip);
        passwordSip = findViewById(R.id.et_password_sip);
        domainSip = findViewById(R.id.et_domain_sip);
        registerDetailsSip = findViewById(R.id.bt_register_details_sip);
        ProgressBar progressBar = findViewById(R.id.pb_register);
        et_extension = findViewById(R.id.et_extension);
        bt_login = findViewById(R.id.bt_login);
        bt_send_otp = findViewById(R.id.bt_send_otp);
        et_otp = findViewById(R.id.et_otp);

        sipManager = new SipManager(getApplicationContext());


        sharedpreferences = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);


        Log.e("token", userInfo.getToken());


        registerDetailsSip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userNameSip.getText().toString().equals("") || passwordSip.getText().toString().equals("") || domainSip.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter all the details", Toast.LENGTH_SHORT).show();
                } else {

                    progressBar.setVisibility(View.VISIBLE);

                    getSharedPreferences(SHARED_PREF_SIP, MODE_PRIVATE).edit()
                            .putString(USERNAME_SIP, userNameSip.getText().toString())
                            .putString(PASSWORD_SIP, passwordSip.getText().toString())
                            .putString(DOMAIN_SIP, domainSip.getText().toString())
                            .apply();


                    userInfo.setExtension(userNameSip.getText().toString());
                    getCrmInfo();

                    if (sipManager.register()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                sharedpreferences.edit().putBoolean(SharedPrefConstants.SIP_REGISTERED, true).apply();
                                progressBar.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }
                }
            }
        });

//        bt_send_otp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if(et_extension.getText().toString().isEmpty()) {
//                    et_extension.requestFocus();
//                    et_extension.setError("Phone number is required");                }
//                else if(et_extension.getText().toString().length() != 10) {
//                    et_extension.requestFocus();
//                    et_extension.setError("Phone number must be 10 digits");
//                }
//                else {
//                    Random r = new Random();
//                    String otp = String.format("%04d%n", r.nextInt(10000));
//                    otp = otp.substring(0,4);
//
//
//
//                    sharedpreferences.edit()
//                            .putString(SharedPrefConstants.OTP,otp)
//                            .apply();
//
//
//                    sendSMS(et_extension.getText().toString(),"OTP for Doocti is " + otp);
//
//                }
//
//            }
//        });


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (et_extension.getText().toString().isEmpty()) {
                    et_extension.requestFocus();
                    et_extension.setError("Phone number is required");
                }
                else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }, 100);

                    userInfo.setExtension(et_extension.getText().toString());
                    sharedpreferences.edit()
                            .putBoolean(SharedPrefConstants.SIP_REGISTERED, false).apply();


                    getCrmInfo();
                }
            }
        });



    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // sipManager.cancelTimer();
    }

    public void radioButtonClicked(View view) {

        if (view.getId() == R.id.sip) {

            findViewById(R.id.ll_username).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_password).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_domain).setVisibility(View.VISIBLE);
            findViewById(R.id.bt_register_details_sip).setVisibility(View.VISIBLE);

            findViewById(R.id.et_extension).setVisibility(View.GONE);
            findViewById(R.id.bt_login).setVisibility(View.GONE);
            findViewById(R.id.bt_send_otp).setVisibility(View.GONE);
            findViewById(R.id.et_otp).setVisibility(View.GONE);

        } else {

            findViewById(R.id.ll_username).setVisibility(View.GONE);
            findViewById(R.id.ll_password).setVisibility(View.GONE);
            findViewById(R.id.ll_domain).setVisibility(View.GONE);
            findViewById(R.id.bt_register_details_sip).setVisibility(View.GONE);

            findViewById(R.id.et_extension).setVisibility(View.VISIBLE);
            findViewById(R.id.bt_login).setVisibility(View.VISIBLE);
            findViewById(R.id.bt_send_otp).setVisibility(View.VISIBLE);
            findViewById(R.id.et_otp).setVisibility(View.VISIBLE);

        }

    }


    private void getCrmInfo() {

        Services services = RetrofitCommonClient.getClient(RetrofitConstants.COMMON_API_URL).create(Services.class);
        Call<CrmInfoResponse> call = services.getCrmInfo(userInfo.getToken(), userInfo.getTenantId(), userInfo.getEmail());
        call.enqueue(new Callback<CrmInfoResponse>() {
            @Override
            public void onResponse(@NonNull Call<CrmInfoResponse> call, @NonNull Response<CrmInfoResponse> response) {
                int code = response.code();
                if (code == 200) {
                    try {

                        setUserInfo(response);
                        setPermissionInfo(response);

                        pbxInfo = new PbxInfo();

                        for (int i = 0; i < response.body().getProjects().get(0).getQueue().size(); i++) {
                            pbxInfo.getQueueArray().add(new PbxInfo.Queue(response.body().getProjects().get(0).getQueue().get(i).getName(),
                                    response.body().getProjects().get(0).getQueue().get(i).getCode()));
                        }

                        SharedPrefHelper.putPermissionInfo(sharedpreferences,permissionInfo);

                        Log.e("popup", permissionInfo.getPopup().toString());

                        asterLogin();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    et_extension.requestFocus();
                    et_extension.setError("Login Failed");
                }
            }

            @Override
            public void onFailure(@NonNull Call<CrmInfoResponse> call, @NonNull Throwable t) {
                et_extension.requestFocus();
                et_extension.setError("Login Failed");
            }
        });
    }

    private void setPermissionInfo(@NonNull Response<CrmInfoResponse> response) {
        permissionInfo = new PermissionInfo(
                response.body().getProjects().get(0).getUserPermission().getAgentStatus(),
                response.body().getProjects().get(0).getUserPermission().getShowStatusChange(),
                response.body().getProjects().get(0).getUserPermission().getAgentTranferCall(),
                response.body().getProjects().get(0).getUserPermission().getAgentTranferStatus(),
                response.body().getProjects().get(0).getUserPermission().getAgentTransfer(),
                response.body().getProjects().get(0).getUserPermission().getAutoDial(),
                response.body().getProjects().get(0).getUserPermission().getAutoDispo(),
                response.body().getProjects().get(0).getUserPermission().getClickToCall(),
                response.body().getProjects().get(0).getUserPermission().getCreateMeeting(),
                response.body().getProjects().get(0).getUserPermission().getCreateNote(),
                response.body().getProjects().get(0).getUserPermission().getCrmEnable(),
                response.body().getProjects().get(0).getUserPermission().getCrmTab(),
                response.body().getProjects().get(0).getUserPermission().getDidTranfer(),
                response.body().getProjects().get(0).getUserPermission().getOtherTransfer(),
                response.body().getProjects().get(0).getUserPermission().getPopup(),
                response.body().getProjects().get(0).getUserPermission().getQueue(),
                response.body().getProjects().get(0).getUserPermission().getQueueTransfer(),
                response.body().getProjects().get(0).getUserPermission().getRecordingPlay(),
                response.body().getProjects().get(0).getUserPermission().getViewCallLog(),
                response.body().getProjects().get(0).getUserPermission().getViewContacts(),
                response.body().getProjects().get(0).getUserPermission().getViewDialerScreen(),
                response.body().getProjects().get(0).getUserPermission().getViewMeeting(),
                response.body().getProjects().get(0).getUserPermission().getViewPark(),
                response.body().getProjects().get(0).getUserPermission().getViewTimeline(),
                response.body().getProjects().get(0).getUserPermission().getViewTransfer(),
                response.body().getProjects().get(0).getUserPermission().getViewUserInfo(),
                response.body().getProjects().get(0).getUserPermission().getAgentConference(),
                response.body().getProjects().get(0).getUserPermission().getEnableConference(),
                response.body().getProjects().get(0).getUserPermission().getAutoDialMainFilter(),
                response.body().getProjects().get(0).getUserPermission().getAutoDialSubFilter(),
                response.body().getProjects().get(0).getUserPermission().getAutoDialStatusFilter(),
                response.body().getProjects().get(0).getUserPermission().getIframePopup(),
                response.body().getProjects().get(0).getUserPermission().getNoDispo(),
                response.body().getProjects().get(0).getUserPermission().getCrmApi(),
                response.body().getProjects().get(0).getUserPermission().getWaitingCalls()
        );

        SharedPrefHelper.putPermissionInfo(sharedpreferences,permissionInfo);
    }

    private void setUserInfo(@NonNull Response<CrmInfoResponse> response) {
        userInfo.setUserId(response.body().getProjects().get(0).getUserInfo().get(0).getUserId());
        userInfo.setUsername(response.body().getProjects().get(0).getUserInfo().get(0).getUserName());
        userInfo.setCrmDomain(response.body().getProjects().get(0).getConfig().get(0).getCrmDomain());
        userInfo.setRingApiUrl((String) response.body().getProjects().get(0).getConfig().get(0).getRingApiUrl());
        userInfo.setRingApiParameters((String) response.body().getProjects().get(0).getConfig().get(0).getRingApiParameter());

        for(int i=0; i<response.body().getProjects().get(0).getUserInfo().get(0).getQueue().size(); i++) {

            userInfo.getDefaultUserQueue().add(new UserInfo.Queue(response.body().getProjects().get(0).getUserInfo().get(0).getQueue().get(i).getQueue(),
                    response.body().getProjects().get(0).getUserInfo().get(0).getQueue().get(i).getUserRank()));
        }
    }

    private void asterLogin() {

        apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);

        sharedpreferences.edit().putBoolean(SharedPrefConstants.FORCE_LOGOUT, false).apply();

        Services services = RetrofitAsterClient.getClient(apiInfo.getDialerDomain()).create(Services.class);
        Call<LoginResponse> call = services.login(userInfo.getToken(), new LoginRequest(userInfo.getUsername(),
                userInfo.getEmail(),
                userInfo.getExtension(),
                userInfo.getProfilePicture(),
                userInfo.getUserId()));

        call.enqueue(new Callback<LoginResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                int code = response.code();
                if (code == 200) {



                    userInfo.setStatus("Unavailable");

                    pbxInfo.setPauseCodeArray(response.body().getPauseCode());
                    pbxInfo.setDidArray(response.body().getDidNo());
                    pbxInfo.setQueueStatus("pause");
                    pbxInfo.setQueueShow(false);

                    autoDialInfo = new AutoDialInfo(
                            "stop",
                            0,
                            50,
                            false,
                            "",
                            new ArrayList<>(),
                            new ArrayList<>()
                    );


                    callInfo = new CallInfo(); // Initializes every variable with empty strings

                    customerInfo = new CustomerInfo(); // Initializes every variable with empty strings


                    sharedpreferences.edit().putString(LOGIN, "true")
                            .putString(USER_INFO, new Gson().toJson(userInfo))
                            .putString(PBX_INFO, new Gson().toJson(pbxInfo))
                            .putString(AUTO_DIAL_INFO, new Gson().toJson(autoDialInfo))
                            .putString(CALL_INFO, new Gson().toJson(callInfo))
                            .putString(CUSTOMER_INFO, new Gson().toJson(customerInfo))
                            .apply();

//                    Intent intent = new Intent(PhoneNumberSipActivity.this, EventListener.class);
//                    startService(intent);



                    moveToNextActivity();
                } else {

                    Toast.makeText(getApplicationContext(), "User Already Exist", Toast.LENGTH_SHORT).show();
                    try {
                        String errorBody = response.errorBody().string();
                        JSONObject jsonObject = new JSONObject(errorBody.trim());
                        String msg = String.valueOf(jsonObject.getString("message"));
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        et_extension.requestFocus();
                        et_extension.setError(msg);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(PhoneNumberSipActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 1500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Log.e("error", t.toString());
//                pb_loader.setVisibility(View.GONE);
            }
        });
    }

    private void moveToNextActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PhoneNumberSipActivity.this, DomainSelectActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);
    }


    public void sendSMS(String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage().toString(),
                    Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }
    }
}
