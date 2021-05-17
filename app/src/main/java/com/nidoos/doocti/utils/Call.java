package com.nidoos.doocti.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.info.AutoDialInfo;
import com.nidoos.doocti.info.CallInfo;
import com.nidoos.doocti.info.CustomerInfo;
import com.nidoos.doocti.info.PbxInfo;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.request.FetchLeadsRequest;
import com.nidoos.doocti.response.FetchLeadsResponse;
import com.nidoos.doocti.retrofit.RetrofitApiClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.sqliteDB.SqliteDB;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

import static com.nidoos.doocti.constants.SharedPrefConstants.AUTO_DIAL_INFO;

public class Call {


    private Socket mSocket;
    private Context context;
    private SharedPreferences sharedpreferences;
    private SqliteDB sqliteDB;
    private Application application;
    private ApplicationInsight applicationInsight;


    private UserInfo userInfo;
    private AutoDialInfo autoDialInfo;
    private CallInfo callInfo;
    private CustomerInfo customerInfo;
    private ApiInfo apiInfo;
    private PbxInfo pbxInfo;

    public Call(Context context, Application application, Socket mSocket) {
        this.context = context;
        this.mSocket = mSocket;
        this.application = application;

        sqliteDB = new SqliteDB(context);
        sharedpreferences = context.getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);

        apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);
        callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
        customerInfo = SharedPrefHelper.getCustomerInfo(sharedpreferences);
        autoDialInfo = SharedPrefHelper.getAutoDialInfo(sharedpreferences);
        pbxInfo = SharedPrefHelper.getPbxInfo(sharedpreferences);


    }

    public void clickToCall(String number, String call_type, String leadID) {
        try {

            userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);

            if (userInfo.getStatus().equals("Available")) {
                JSONObject clickToCallJSON = new JSONObject();
                JSONObject currentCall = new JSONObject();
                try {
                    clickToCallJSON.put("action", "Dial");
                    clickToCallJSON.put("agent", userInfo.getEmail());
                    clickToCallJSON.put("agent_name", userInfo.getUsername());
                    clickToCallJSON.put("station", userInfo.getExtension());
                    clickToCallJSON.put("phone_number", number);
                    clickToCallJSON.put("tenant_id", userInfo.getTenantId());
                    clickToCallJSON.put("call_type", call_type);
                    clickToCallJSON.put("leadID", leadID);
                    clickToCallJSON.put("campaign", sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN));
                    currentCall.put("call_type", call_type);
                    currentCall.put("phone_number", number);
                    currentCall.put("leadID", leadID);
                    if(userInfo.getMissed()){
                        clickToCallJSON.put("missed", true);
                    }
                    currentCall.put("campaign", sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN));
                    userInfo.setMissed(false);
                    SharedPrefHelper.putUserInfo(sharedpreferences,userInfo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("msocket",mSocket + "");

                if (mSocket.connected()) {
                    Log.e("Event: initiate-call :", clickToCallJSON.toString());
                    sharedpreferences.edit().putBoolean("call-initiated", true).apply();
                    sharedpreferences.edit().putString("CurrentCallInfo", currentCall.toString()).apply();

//                    Intent callIntent = new Intent(Intent.ACTION_CALL);
//                    callIntent.putExtra("Incoming", true);
//                    callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    callIntent.setData(Uri.parse("tel:" + number));
//                    context.startActivity(callIntent);

                    Toast.makeText(context, "Call Initiated", Toast.LENGTH_SHORT).show();
                    Log.e("call :", "initiated");

                    applicationInsight = new ApplicationInsight(context, application);
                    applicationInsight.trackEvent("Click To Call", clickToCallJSON.toString());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mSocket.emit("initiate-call", clickToCallJSON);
                            Log.e("event TX","initiate-call,"+ clickToCallJSON.toString());
                        }
                    }, 900);


                } else {
                    Toast.makeText(context, "Disconnected, try after sometime", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startAutoDial() {

        new Handler().postDelayed(new Runnable() {
            public void run() {
                sqliteDB = new SqliteDB(context);

                autoDialInfo = SharedPrefHelper.getAutoDialInfo(sharedpreferences);
                userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);

                Log.d("autodialstatus", autoDialInfo.getStatus());



                if (autoDialInfo.getStatus().equals("start") && userInfo.getStatus().equals("Available")) {
                    String[] leadData = sqliteDB.get_top_lead();
                    String number = leadData[0];


                    if (number != null && !number.isEmpty()) {
//                            if (!number.isEmpty()) {
                        clickToCall(number, "AUTO", leadData[1]);
                    }
                }
            }
        }, 5000);
    }

    public void checkDataSize() {
        sqliteDB = new SqliteDB(context);

        if (sqliteDB.get_leads().size() <= 30 && autoDialInfo.getMoreData()) {
            getLeads();
        }
    }

    private void getLeads() {
        String mainFilterValue = null;
        int skip = 0, take = 0;
        try {
            String name = autoDialInfo.getMainFilterValue();
            mainFilterValue = sqliteDB.get_id_by_name("MAINFILTER", name);
            skip = autoDialInfo.getSkip();
            take = autoDialInfo.getTake();

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<FetchLeadsRequest.SubFilterValue> subFilterValues = sqliteDB.getSubFilterValue("SUBFILTER", autoDialInfo.getSubFilterValue().toString());
        List<FetchLeadsRequest.Callstatus> callStatus = sqliteDB.getCallStatusValue("LEADCALLSTATUS", autoDialInfo.getCallStatusValue().toString());

        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        retrofit2.Call<FetchLeadsResponse> call = services.searchLeads(userInfo.getToken(), userInfo.getCrmDomain(), new FetchLeadsRequest(userInfo.getUserId(), skip, take, mainFilterValue, subFilterValues, callStatus));
        call.enqueue(new Callback<FetchLeadsResponse>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<FetchLeadsResponse> call, @NonNull Response<FetchLeadsResponse> response) {
                if (response.code() == 200) {
                    List<FetchLeadsResponse.Datum> leadViewList = new ArrayList<>();
                    leadViewList = response.body().getData();
                    if (leadViewList.size() != 0) {
                        for (int i = 0; i < leadViewList.size(); i++) {
                            sqliteDB.insert_lead_info(leadViewList.get(i).getLeadID(), leadViewList.get(i).getLeadName(), leadViewList.get(i).getLeadNumber(), leadViewList.get(i).getStatus());
                        }

                        autoDialInfo.setSkip(response.body().getSkip());
                        autoDialInfo.setMoreData(response.body().getMoreData());

                        sharedpreferences.edit().putString(AUTO_DIAL_INFO, new Gson().toJson(autoDialInfo))
                                .apply();

                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<FetchLeadsResponse> call, @NonNull Throwable t) {
                Log.e("error", t.toString());
            }
        });
    }


}
