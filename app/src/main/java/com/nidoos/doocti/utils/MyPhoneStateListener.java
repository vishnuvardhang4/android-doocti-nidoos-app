package com.nidoos.doocti.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.CallInfo;
import com.nidoos.doocti.info.UserInfo;

import java.util.Calendar;
import java.util.Date;

public class MyPhoneStateListener extends PhoneStateListener {

    public static Boolean phoneRinging = false;
    public static Boolean inCall = false;

    SharedPreferences sharedpreferences;

    Context context;

    private UserInfo userInfo;



    public MyPhoneStateListener(Context context) {
        this.context = context;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onCallStateChanged(int state, String incomingNumber) {

        sharedpreferences = context.getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);

        if (sharedpreferences.getBoolean("call-initiated", false)) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d("callstate", "IDLE");

                    if (inCall) {
                        inCall = false;
                        Date endTime = Calendar.getInstance().getTime();
                        Log.d("endTime", endTime.toString());
                        sharedpreferences.edit()
                                .putString("callEndTime", endTime.toString())
                                .putBoolean("incall",false)
                                .apply();

                    }
                    phoneRinging = false;


                    sharedpreferences.edit().putBoolean("incall",false);


                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d("callstate", "OFFHOOK");

                    inCall = true;
                    if (phoneRinging) {
                        Log.d("callstate", "ANSWERED");
                        sharedpreferences.edit().putBoolean("answered",true).apply();
                        openCallScreenAfterCall();
                    }

                    Date startTime = Calendar.getInstance().getTime();
                    sharedpreferences.edit()
                            .putString("callStartTime", startTime.toString())
                            .putBoolean("incall",true)
                            .apply();

                    phoneRinging = false;

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d("callstate", "RINGING");
                    phoneRinging = true;
                    assert userInfo.getStatus() != null;
                    if (userInfo.getStatus().equals("Available") && sharedpreferences.getBoolean("call-initiated", false)) {
                        openCallScreenAfterCall();
                    }
                    break;


            }
        }
    }

    public void openCallScreenAfterCall() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CallInfo callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);

                if (!callInfo.getUniqueId().isEmpty()) {
                    Log.d("callstate", "run: after answering");
//                    Intent intent = new Intent(context, CallScreenActivity.class);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
                }
            }
        }, 500);
    }


}