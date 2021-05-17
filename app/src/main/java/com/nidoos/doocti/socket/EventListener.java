package com.nidoos.doocti.socket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.nidoos.doocti.R;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.info.CallInfo;
import com.nidoos.doocti.info.CustomerInfo;
import com.nidoos.doocti.info.PbxInfo;
import com.nidoos.doocti.info.PermissionInfo;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.request.CallActivityRequest;
import com.nidoos.doocti.request.OfflineCallActivityRequest;
import com.nidoos.doocti.response.CallActivityResponse;
import com.nidoos.doocti.response.CustomerInfoResponse;
import com.nidoos.doocti.retrofit.RetrofitApiClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.sqliteDB.SqliteDB;
import com.nidoos.doocti.utils.ApplicationInsight;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.utils.SipManager;
import com.nidoos.doocti.views.CallScreenActivity;
import com.nidoos.doocti.views.Fragments.ProfileFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nidoos.doocti.constants.SharedPrefConstants.CALL_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.CUSTOMER_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.PBX_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.USER_INFO;
import static com.nidoos.doocti.constants.SocketConstants.CALL_STATUS_RESPONSE;
import static com.nidoos.doocti.constants.SocketConstants.HANGUP_RESPONSE;
import static com.nidoos.doocti.constants.SocketConstants.INITIATE_CALL_RESPONSE;
import static com.nidoos.doocti.constants.SocketConstants.PBX_ERROR;
import static com.nidoos.doocti.constants.SocketConstants.QUEUE_LOGIN_RESPONSE;
import static com.nidoos.doocti.constants.SocketConstants.QUEUE_LOGOUT_RESPONSE;
import static com.nidoos.doocti.constants.SocketConstants.QUEUE_PAUSE_RESPONSE;
import static com.nidoos.doocti.constants.SocketConstants.SOCKET_ID;

public class EventListener extends Service {

    private static String NOTIFICATION_CHANNEL_ID = "com.nidoos.doocti";
    SipManager sipManager;
    private Socket mSocket;
    private SharedPreferences sharedpreferences;
    private SqliteDB sqliteDB;
    private UserInfo userInfo;
    private PermissionInfo permissionInfo;
    private CallInfo callInfo;
    private CustomerInfo customerInfo;
    private ApiInfo apiInfo;
    private String unique_id = "", event = "";
    private Context context;
    private ApplicationInsight applicationInsight;

    private Emitter.Listener onNewMessage;
    private Emitter.Listener onConnect;
    private Emitter.Listener onSync;
    private Emitter.Listener onPbxError;
    private Emitter.Listener onDisconnect;
    private Emitter.Listener onDialResponse;
    private Emitter.Listener onSocketEvent;
    private Emitter.Listener onQueuePauseResponse;
    private Emitter.Listener onQueueLoginResponse;
    private Emitter.Listener onQueueLogoutResponse;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startMyOwnForeground();
        } else {
            startForeground(1, new Notification());
        }
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.tdlogo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_NONE)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    public void getCustomerInfo(String number) {
        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<CustomerInfoResponse> call = services.getCustomerInfo(userInfo.getToken(), userInfo.getCrmDomain(), number);
        Log.e("getCustomerInfo", userInfo.getCrmDomain());

        sharedpreferences.edit().putBoolean("customerInfoApiCall", true).apply();

        new Thread(new Runnable() {
            public void run() {
                call.enqueue(new Callback<CustomerInfoResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CustomerInfoResponse> call1, @NonNull Response<CustomerInfoResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.code() == 200) {
                                customerInfo.setName(response.body().getData().get(0).getLeadName());
                                customerInfo.setEmail(response.body().getData().get(0).getCustomerEmail());
                                customerInfo.setCompany(response.body().getData().get(0).getCompanyName());
                                customerInfo.setOwnerId(response.body().getData().get(0).getOwnerID());
                                customerInfo.setPhoneNumber(number);
                                customerInfo.setLeadId(response.body().getData().get(0).getLeadID());
                                customerInfo.setModule(response.body().getData().get(0).getModule());
                                Log.e("customer_info", customerInfo.toString());
                                sharedpreferences.edit()
                                        .putString(CUSTOMER_INFO, new Gson().toJson(customerInfo))
                                        .apply();
                            }

                            if (!event.equals("Drop")) {
                                Log.e("call popup", "calling");
                                Intent callIntent = new Intent(context, CallScreenActivity.class);
                                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CustomerInfoResponse> call1, @NonNull Throwable t) {
                        Log.e("throwable", t.toString());
                    }
                });
            }
        }).start();
    }

    public void pushCallActivityData() {
        try {
            int count = sqliteDB.get_call_activity().size();
            List<OfflineCallActivityRequest> list = sqliteDB.get_call_activity();
            for (int i = 0; i < count; i++) {
                int id = list.get(i).getId();
                CallActivityRequest.CallResult dispo_status = sqliteDB.getCallStatus("LEADCALLSTATUS", list.get(i).getCallResult());
                Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
                Call<CallActivityResponse> call = services.createCallActivity(userInfo.getToken(), userInfo.getCrmDomain(), new CallActivityRequest(list.get(i).getSubject(), list.get(i).getCallType(), "", "", userInfo.getUserId(), userInfo.getUsername(), list.get(i).getCallStartTime(), list.get(i).getPhoneNumber(), list.get(i).getCallDescription(), list.get(i).getDuration(), dispo_status, "", ""));
                call.enqueue(new Callback<CallActivityResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<CallActivityResponse> call, @NonNull Response<CallActivityResponse> response) {
                        if (response.code() == 201) {
                            Log.i("id", String.valueOf(id));
                            sqliteDB.deleteActivity(id);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<CallActivityResponse> call, @NonNull Throwable t) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        com.nidoos.doocti.socket.Doocti app = (com.nidoos.doocti.socket.Doocti) getApplication();
        app.socketConnect();

        mSocket = app.getSocket();

        Log.d("socketcheckevent", "this is " + mSocket);


        context = getApplicationContext();
        applicationInsight = new ApplicationInsight(getApplicationContext(), getApplication());


        sqliteDB = new SqliteDB(this);
        sipManager = new SipManager(getApplicationContext());

        getInfosFromSharedPreferences();

        initializeSocketEvents();

        mSocket.on(Socket.EVENT_MESSAGE, onNewMessage);
        mSocket.on(CALL_STATUS_RESPONSE, onSync);
        mSocket.on(SOCKET_ID, onSocketEvent);
        mSocket.on(PBX_ERROR, onPbxError);
        mSocket.on(INITIATE_CALL_RESPONSE, onDialResponse);
        mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(HANGUP_RESPONSE, onHangupResponse);
        mSocket.on(QUEUE_LOGIN_RESPONSE, onQueueLoginResponse);
        mSocket.on(QUEUE_LOGOUT_RESPONSE, onQueueLogoutResponse);

        mSocket.on(QUEUE_PAUSE_RESPONSE, onQueuePauseResponse);
        getInfosFromSharedPreferences();

        try {
            mSocket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void getInfosFromSharedPreferences() {
        sharedpreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
        permissionInfo = SharedPrefHelper.getPermissionInfo(sharedpreferences);
        callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
        apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);
        customerInfo = SharedPrefHelper.getCustomerInfo(sharedpreferences);


    }

    public void initializeSocketEvents() {
        onNewMessage = getOnNewMessage();
        onConnect = getOnConnect();
        onSync = getOnSync();
        onPbxError = getOnPbxError();
        onDisconnect = getOnDisconnect();
        onDialResponse = getOnDialResponse();
        onSocketEvent = getOnSocketEvent();
        onQueuePauseResponse = getQueuePauseResponse();
        onQueueLoginResponse = getQueueLoginResponse();
        onQueueLogoutResponse = getQueueLogoutResponse();

    }

    private Emitter.Listener getQueueLogoutResponse() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try {
                    for (Object arg : args) {
                        JSONObject data = (JSONObject) arg;
                        Log.w("event Rx", "queue-logout-response," + data.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    }

    private Emitter.Listener getQueuePauseResponse() {

        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                try {
                    for (Object arg : args) {

                        final JSONObject data = (JSONObject) arg;
                        Log.i("Event Rx", "queue-pause-response" + data.toString());


                        try {
                            if (data.getBoolean("paused")) {

                                if (data.getJSONArray("message").getJSONObject(0).getString("response").equals("Success")) {


                                    PbxInfo pbxInfo = SharedPrefHelper.getPbxInfo(sharedpreferences);
                                    pbxInfo.setQueueStatus("pause");

                                    pbxInfo.setPauseCode(data.getString("reason"));

                                    userInfo.setStatus(data.getString("reason"));


                                    sharedpreferences.edit()
                                            .putString(PBX_INFO, new Gson().toJson(pbxInfo))
                                            .putString(SharedPrefConstants.USER_INFO, new Gson().toJson(userInfo))
                                            .apply();

//                                    Intent intent = new Intent(getApplicationContext(), DialerActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);
//                                        startActivity(new Intent(getApplicationContext(), DialerActivity.class));

                                    toast("Queue Pause Success");

                                    ProfileFragment.queuePauseHandler.post(ProfileFragment.queuePauseRunnable);


                                } else {
                                    toast("Queue Pause Failed");
                                }


                            } else {
                                if (data.getJSONArray("message").getJSONObject(0).getString("response").equals("Success")) {
//                                    Toast.makeText(app.getApplicationContext(), "Queue Resume Success", Toast.LENGTH_SHORT).show();

                                    PbxInfo pbxInfo = SharedPrefHelper.getPbxInfo(sharedpreferences);
                                    pbxInfo.setQueueStatus("resume");
                                    pbxInfo.setPauseCode("");
                                    userInfo.setStatus("Available");


                                    sharedpreferences.edit()
                                            .putString(PBX_INFO, new Gson().toJson(pbxInfo))
                                            .putString(SharedPrefConstants.USER_INFO, new Gson().toJson(userInfo))
                                            .apply();

//
//                                    Intent intent = new Intent(getApplicationContext(), DialerActivity.class);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    startActivity(intent);

                                    toast("Queue Resume Success");

                                    ProfileFragment.queuePauseHandler.post(ProfileFragment.queuePauseRunnable);


//
                                } else {
                                    toast("Queue resume failed");
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };


    }

    private Emitter.Listener getQueueLoginResponse() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                for (Object arg : args) {

                    JSONObject data = (JSONObject) arg;
                    Log.w("event Rx", "queue-login-response," + data.toString());


                    Log.d("queue-login-response", "queue login success");
//                    Toast.makeText(app.getApplicationContext(), "Queue Login Success", Toast.LENGTH_SHORT).show();
//                    ll_multi_select_queue_view.setVisibility(View.GONE);
//                    ll_queue_view.setVisibility(View.VISIBLE);
                    JSONArray selected_queue = null;
                    try {
                        selected_queue = data.getJSONArray("queue");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Log.d("queueselected", selected_queue.toString());

                    List<String> selectedQueueList = new ArrayList<>();
                    for (int i = 0; i < selected_queue.length(); i++) {
                        try {
                            JSONObject jsonObject = (JSONObject) selected_queue.get(i);
                            selectedQueueList.add(jsonObject.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    PbxInfo pbxInfo = SharedPrefHelper.getPbxInfo(sharedpreferences);
                    pbxInfo.setQueueSelected(selectedQueueList);

                    sharedpreferences.edit()
                            .putString(SharedPrefConstants.PBX_INFO, new Gson().toJson(pbxInfo))
                            .putBoolean(SharedPrefConstants.FIRST_LOAD, true)
                            .apply();

                    ProfileFragment.queueLoginHandler.postDelayed(ProfileFragment.queueLoginRunnable,500);

                }
            }
        };


    }


    public void toast(String msg) {
        ContextCompat.getMainExecutor(getApplicationContext()).execute(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private Emitter.Listener onHangupResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.w("event Rx", "onHangupRes");

            try {
                for (Object arg : args) {
                    JSONObject data = (JSONObject) arg;
                    Log.w("event Rx", "onHangupRes," + data.toString());
                    sharedpreferences.edit().putBoolean(SharedPrefConstants.HANGUP_RESPONSE, true).apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public Emitter.Listener getOnNewMessage() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try {
                    for (Object arg : args) {

                        String start_time, call_type, phone_number;
                        boolean popup = false;
                        JSONObject data = (JSONObject) arg;

                        try {

                            Log.w("event Rx", "message," + data.toString());
                            event = data.getString("evt");
                            boolean customerInfoApiCall = sharedpreferences.getBoolean("customerInfoApiCall", false);
                            Log.e("customerInfoApiCall", String.valueOf(customerInfoApiCall));
                            popup = permissionInfo.getPopup();
                            Log.e("customerInfoApiCall", String.valueOf(popup));

                            unique_id = data.getString("uniqueid");
                            String current_uniqueid = sharedpreferences.getString("current_uniqueid", "");

                            switch (event) {
                                case "RINGING":

                                    sharedpreferences.edit().putString("current_uniqueid", unique_id).apply();
                                    applicationInsight.trackEvent("RINGING", data.toString());

                                    start_time = data.getString("calltime");
                                    unique_id = data.getString("uniqueid");
                                    call_type = data.getString("call_type");
                                    phone_number = data.getString("phonenumber");

                                    if (call_type.equals("MANUAL")) {
                                        call_type = "OUTBOUND";
                                        sharedpreferences.edit().putBoolean("AutoAnswer", true).apply();
                                    }
                                    try {
                                        Log.e("popup", String.valueOf(popup));

                                        callInfo.setEvent(event);
                                        callInfo.setCallType(call_type);
                                        callInfo.setUniqueId(unique_id);
                                        callInfo.setPhoneNumber(phone_number);
                                        callInfo.setStartTime(start_time);


                                        userInfo.setStatus("InCall");
                                        sharedpreferences.edit()
                                                .putString(CALL_INFO, new Gson().toJson(callInfo))
                                                .putString(USER_INFO, new Gson().toJson(userInfo))
                                                .apply();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (popup) {
                                        Log.e("customerInfo-Api", "called");
                                        getCustomerInfo(phone_number);
                                    }
                                    break;

                                case "ANSWERED":

                                    sharedpreferences.edit().putString("current_uniqueid", unique_id).apply();
                                    applicationInsight.trackEvent("ANSWERED", data.toString());

                                    String agent_channel, caller_channel, answer_time;
                                    agent_channel = data.getString("agent_channel");
                                    caller_channel = data.getString("caller_channel");
                                    answer_time = data.getString("answer_time");

                                    try {
                                        callInfo.setEvent(event);
                                        callInfo.setAgentChannel(agent_channel);
                                        callInfo.setCallerChannel(caller_channel);
                                        callInfo.setAnswerTime(answer_time);

                                        if (!customerInfoApiCall && popup) {

                                            callInfo.setUniqueId(data.getString("uniqueid"));
                                            callInfo.setPhoneNumber(data.getString("phonenumber"));
                                            sharedpreferences.edit()
                                                    .putString(CALL_INFO, new Gson().toJson(callInfo))
                                                    .apply();
                                            Log.e("customerInfo-Api", "called");
                                            getCustomerInfo(data.getString("phonenumber"));
                                        }
                                        sharedpreferences.edit()
                                                .putString(CALL_INFO, new Gson().toJson(callInfo))
                                                .apply();

                                        Intent callIntent = new Intent(context, CallScreenActivity.class);
                                        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(callIntent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                case "DISPO": {

                                    applicationInsight.trackEvent("DISPO", data.toString());

                                    call_type = data.getString("call_type");

                                    if (call_type.equals("MANUAL")) {
                                        call_type = "OUTBOUND";
                                    }

                                    callInfo.setCallType(call_type);
                                    callInfo.setEvent(event);

                                    String uniqueid = data.getString("uniqueid");
                                    assert current_uniqueid != null;
                                    if (!customerInfoApiCall && popup && !current_uniqueid.equals(uniqueid)) {

                                        callInfo.setAnswerTime(data.getString("end_time"));
                                        callInfo.setUniqueId(data.getString("uniqueid"));
                                        callInfo.setPhoneNumber(data.getString("phonenumber"));
                                        callInfo.setEndTime(data.getString("time"));
                                        callInfo.setCallType(data.getString("call_type"));
                                        sharedpreferences.edit()
                                                .putString(CALL_INFO, new Gson().toJson(callInfo))
                                                .apply();

                                        Log.e("customerInfo-Api", "called");
                                        getCustomerInfo(data.getString("phonenumber"));
                                    }

                                    callInfo.setEndTime(data.getString("end_time"));
                                    boolean auto_dispo = permissionInfo.getAutoDispo();

                                    if (auto_dispo) {

                                        userInfo.setStatus("Available");
                                        JSONObject queueResume = new JSONObject();
                                        try {
                                            queueResume.put("action", "QueuePause");
                                            queueResume.put("agent", userInfo.getEmail());
                                            queueResume.put("station", userInfo.getExtension());
                                            queueResume.put("paused", false);
                                            queueResume.put("tenant_id", userInfo.getTenantId());
                                            queueResume.put("call", false);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        Log.i("dispo-pause", queueResume.toString());
                                        if (mSocket.connected()) {
                                            mSocket.emit("dispo-pause", queueResume);
                                            Log.e("event TX", "dispo-pause," + queueResume.toString());
                                        }

                                        callInfo = new CallInfo();
                                        customerInfo = new CustomerInfo();

                                        sharedpreferences.edit()
                                                .putString(CALL_INFO, new Gson().toJson(callInfo))//Empty object required
                                                .putString(CUSTOMER_INFO, new Gson().toJson(customerInfo))// Empty object required
                                                .putString(USER_INFO, new Gson().toJson(userInfo))
                                                .apply();
                                    }


                                    sharedpreferences.edit()
                                            .putString(CALL_INFO, new Gson().toJson(callInfo))
                                            .apply();
                                    break;
                                }
                                case "Drop": {

                                    userInfo.setStatus("Available");

                                    JSONObject queueResume = new JSONObject();
                                    try {
                                        queueResume.put("action", "QueuePause");
                                        queueResume.put("agent", userInfo.getEmail());
                                        queueResume.put("station", userInfo.getExtension());
                                        queueResume.put("paused", false);
                                        queueResume.put("tenant_id", userInfo.getTenantId());
                                        queueResume.put("status", "Drop");
                                        queueResume.put("call", false);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.e("dispo-pause", queueResume.toString());
                                    if (mSocket.connected()) {
                                        sharedpreferences.edit().putBoolean("AutoAnswer", false).apply();
                                        mSocket.emit("dispo-pause", queueResume);
                                        Log.e("event TX", "dispo-pause," + queueResume.toString());
                                    }

                                    callInfo = new CallInfo();
                                    customerInfo = new CustomerInfo();

                                    sharedpreferences.edit()
                                            .putString(CALL_INFO, new Gson().toJson(callInfo))//Empty object required
                                            .putString(CUSTOMER_INFO, new Gson().toJson(customerInfo))// Empty object required
                                            .putString(USER_INFO, new Gson().toJson(userInfo))
                                            .apply();

                                    break;
                                }
                            }

                        } catch (JSONException e) {
                            Log.e("", e.getMessage());
                            return;
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public Emitter.Listener getOnConnect() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                for (Object arg : args) {
                    JSONObject data = (JSONObject) arg;
                    Log.w("event Rx", "connect," + data.toString());
                }
            }
        };
    }

    public Emitter.Listener getOnSync() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
                callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
                boolean alive = true;
                for (Object arg : args) {
                    JSONObject data = (JSONObject) arg;
                    Log.w("event Rx", "check-call-response," + data.toString());
                    try {
                        alive = data.getBoolean("alive");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!alive) {

                        switch (userInfo.getStatus()) {
                            case "Available":
                                Log.e("Status", "Available");
                                break;
                            case "Unavailable":
                            case "InCall":
                                boolean queuePaused = sharedpreferences.getBoolean("paused", false);
                                if (callInfo.getEvent().equals("RINGING")) {
                                    userInfo.setStatus("InCall");
                                    sharedpreferences.edit().putString(USER_INFO, new Gson().toJson(userInfo)).apply();
                                    callInfo.setEvent(callInfo.getEvent());
                                    sharedpreferences.edit().putString(CALL_INFO, new Gson().toJson(callInfo)).apply();
                                    Intent intent = new Intent(context, CallScreenActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                } else if (callInfo.getEvent().equals("ANSWERED")) {
//
                                    callInfo.setEvent("DISPO");
                                    sharedpreferences.edit().putString(CALL_INFO, new Gson().toJson(callInfo)).apply();
                                }
                                break;
                        }
                    } else {
                        Log.e("status", "agent Available");
                        Log.e("Status", "Available");
                        Intent intent = new Intent(context, CallScreenActivity.class);
                        startActivity(intent);
                        applicationInsight.trackEvent("CallPopup", "Re-initiated");
                    }

                }
            }
        };
    }

    public Emitter.Listener getOnPbxError() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                applicationInsight.trackEvent("Socket", "PBX Error");
                for (Object arg : args) {
                    try {
//
                        Log.w("event Rx", "onError,PBX Error\n" + arg.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    public Emitter.Listener getOnDisconnect() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                applicationInsight.trackEvent("Socket", "Disconnected");
                Log.w("event Rx", "onDisconnect, disconnected");
            }
        };
    }

    public Emitter.Listener getOnDialResponse() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try {
                    for (Object arg : args) {
                        JSONObject data = (JSONObject) arg;
                        Log.w("event Rx", "onDialRes," + data.toString());
                        String call_type = data.getString("call_type");
                        if (call_type.equals("AUTO")) {
                            sqliteDB.deleteID();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    public Emitter.Listener getOnSocketEvent() {
        return new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try {
                    for (Object arg : args) {
                        String socketID;
                        JSONObject data = (JSONObject) arg;
                        Log.w("event Rx", "onConnect," + data.toString());
                        try {
                            applicationInsight.trackEvent("Socket", "Connected");
                            Log.i("socket-connected", data.toString());
                            socketID = data.getString("SOCKETID");
                            sharedpreferences.edit().putString("socket_id", socketID).apply();
                            JSONObject clientInfo = new JSONObject();
                            try {
                                clientInfo.put("socket_id", socketID);
                                clientInfo.put("station", userInfo.getExtension());
                                clientInfo.put("type", "doocti");
                                clientInfo.put("tenant_id", userInfo.getTenantId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            mSocket.emit("client-info", clientInfo);
                            Log.e("event TX", "client-info," + clientInfo.toString());
                            if (sqliteDB.get_call_activity().size() > 0) {
                                pushCallActivityData();
                            }
                            //***********************For Allset User Only***********************************//
                            boolean first_time = sharedpreferences.getBoolean("first_time_", true);
                            if (first_time) {
                                JSONObject agent_join = new JSONObject();
                                try {
                                    agent_join.put("action", "agent-join");
                                    agent_join.put("agent", userInfo.getEmail());
                                    agent_join.put("station", userInfo.getExtension());
                                    agent_join.put("tenant_id", userInfo.getTenantId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (mSocket.connected()) {
//                                    mSocket.emit("agent-join",agent_join);
//                                    Log.e("event TX","agent-join,"+ agent_join.toString());
                                    applicationInsight.trackEvent("Agent Join", agent_join.toString());
                                }
                                sharedpreferences.edit().putBoolean("first_time_", false).apply();
                            }
                            //***********************For Allset User Only***********************************//

                        } catch (JSONException e) {
                            Log.e("", e.getMessage());
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}