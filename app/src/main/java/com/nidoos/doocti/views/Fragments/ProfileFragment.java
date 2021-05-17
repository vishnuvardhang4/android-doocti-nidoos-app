package com.nidoos.doocti.views.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.nidoos.doocti.R;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.info.AutoDialInfo;
import com.nidoos.doocti.info.CallInfo;
import com.nidoos.doocti.info.PbxInfo;
import com.nidoos.doocti.info.PermissionInfo;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.request.LogoutRequest;
import com.nidoos.doocti.response.LogoutResponse;
import com.nidoos.doocti.response.PerformanceResponse;
import com.nidoos.doocti.retrofit.RetrofitAsterClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.socket.EventListener;
import com.nidoos.doocti.sqliteDB.SqliteDB;
import com.nidoos.doocti.utils.ApplicationInsight;
import com.nidoos.doocti.utils.MultiSelectionSpinner;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.utils.SipManager;
import com.nidoos.doocti.views.Adapter.WaitingListAdapter;
import com.nidoos.doocti.views.LoginActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nidoos.doocti.constants.SocketConstants.FORCE_LOGOUT_RESPONSE;
import static com.nidoos.doocti.constants.SocketConstants.QUEUE_WAITING_RESPONSE;
import static com.nidoos.doocti.utils.SharedPrefHelper.getApiInfo;
import static com.nidoos.doocti.utils.SharedPrefHelper.getAutoDialInfo;
import static com.nidoos.doocti.utils.SharedPrefHelper.getCallInfo;
import static com.nidoos.doocti.utils.SharedPrefHelper.getPbxInfo;
import static com.nidoos.doocti.utils.SharedPrefHelper.getPermissionInfo;
import static com.nidoos.doocti.utils.SharedPrefHelper.getUserInfo;


@SuppressWarnings("unchecked")
public class ProfileFragment extends Fragment {



    @BindView(R.id.tv_user_name)
    TextView tv_user_name;

    @BindView(R.id.tv_extension)
    TextView tv_extension;

    @BindView(R.id.tv_queue_list)
    TextView tv_queue_list;

    @BindView(R.id.et_phone_number)
    TextView et_phone_number;

    @BindView(R.id.tv_pause_code_list)
    TextView tv_pause_code_list;

    @BindView(R.id.iv_profile_picture)
    CircleImageView iv_profile_picture;

    @BindView(R.id.iv_status)
    ImageView iv_status;

    @BindView(R.id.pb_loader)
    ProgressBar pb_loader;

    @BindView(R.id.rl_logout)
    RelativeLayout rl_logout;

    @BindView(R.id.rl_whatsaap)
    RelativeLayout rl_whatsaap;

    @BindView(R.id.rl_waiting_list)
    RelativeLayout rl_waiting_list;

    @BindView(R.id.sip_actions)
    RelativeLayout sip_actions;

    @BindView(R.id.ll_queue_view)
    LinearLayout ll_queue_view;

    @BindView(R.id.rv_waiting_list)
    RecyclerView rv_waiting_list;

    @BindView(R.id.ll_multi_select_queue_view)
    LinearLayout ll_multi_select_queue_view;

    @BindView(R.id.ll_pause_view)
    LinearLayout ll_pause_view;

    @BindView(R.id.ll_pause_code_view)
    LinearLayout ll_pause_code_view;

    @BindView(R.id.bt_logout)
    ImageButton bt_logout;

    @BindView(R.id.bt_select_queue)
    Button bt_select_queue;

    @BindView(R.id.bt_save_queue)
    Button bt_save_queue;

    @BindView(R.id.bt_pause_queue)
    Button bt_pause_queue;

    @BindView(R.id.bt_resume_queue)
    Button bt_resume_queue;

    @BindView(R.id.bt_call)
    Button bt_call;

    @BindView(R.id.sp_pause_code)
    Spinner sp_pause_code;

    @BindView(R.id.sp_penalty)
    Spinner sp_penalty;

    @BindView(R.id.sp_queue)
    MultiSelectionSpinner sp_queue;

    @BindView(R.id.iv_sip_status)
    ImageView iv_sip_status;

    @BindView(R.id.sw_auto_answer)
    Switch sw_auto_answer;

    @BindView(R.id.et_whatsaap)
    EditText et_whatsaap;

    @BindView(R.id.bt_whatsaap_message_save)
    Button bt_whatsaap_message_save;

    @BindView(R.id.unanswered_calls)
    TextView unanswered_calls;

    @BindView(R.id.answered_calls)
    TextView answered_calls;

    @BindView(R.id.total_calls)
    TextView total_calls;


    @BindView(R.id.ib_sync)
    ImageButton ib_sync;

        @BindView(R.id.sp_external_message)
    Spinner sp_external_message;

        @BindView(R.id.ll_mail)
    LinearLayout ll_mail;

        @BindView(R.id.bt_mail_save)
    Button bt_mail_save;

        @BindView(R.id.et_mail_subject)
    EditText et_mail_subject;

        @BindView(R.id.et_mail_content)
    EditText et_mail_content;

    private SharedPreferences sharedpreferences;
    private Socket mSocket;
    private Doocti app;
    private ArrayList<String[]> waitingList;

    private ApplicationInsight applicationInsight;
    private SqliteDB sqliteDB;
    private SipManager sipManager;

    private UserInfo userInfo;
    private PermissionInfo permissionInfo;
    private AutoDialInfo autoDialInfo;
    private CallInfo callInfo;
    private PbxInfo pbxInfo;
    public static Handler queuePauseHandler;
    public static Runnable queuePauseRunnable;
    public static Handler queueLoginHandler;
    public static Runnable queueLoginRunnable;


    private Emitter.Listener queueLoginResponse;
    private Emitter.Listener queueLogoutResponse;
    private Emitter.Listener queueWaitingResponse;
    private Emitter.Listener forceLogoutCheck;
    private Emitter.Listener queuePauseResponse;


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, rootView);

        app = (Doocti) requireActivity().getApplicationContext();
        mSocket = app.getSocket();


        Log.d("profileFragment", "In profileFragment");

        waitingList = new ArrayList<>();

        getInfosFromSharedPreferences();


        if (!sharedpreferences.getBoolean(SharedPrefConstants.DOMAIN_SELECT, false)) {
            asterLogout();
        }

        initializeSocketEvents();


        //  if (mSocket != null) {

//            Log.d("socketcheck","socket is not null");

//        mSocket.on(QUEUE_LOGIN_RESPONSE, queueLoginResponse);
//        mSocket.on(QUEUE_LOGOUT_RESPONSE, queueLogoutResponse);
//        mSocket.on(QUEUE_PAUSE_RESPONSE, queuePauseResponse);
        mSocket.on(FORCE_LOGOUT_RESPONSE, forceLogoutCheck);
        mSocket.on(QUEUE_WAITING_RESPONSE, queueWaitingResponse);
        //}


        getInfosFromSharedPreferences();

        applicationInsight = new ApplicationInsight(requireContext().getApplicationContext(), requireActivity().getApplication());
        applicationInsight.trackPageView("ProfileFragment", "");

        sqliteDB = new SqliteDB(app);

        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
        pbxInfo = SharedPrefHelper.getPbxInfo(sharedpreferences);
        Log.i("userInfo", userInfo.toString());
        Log.i("pbxInfo", pbxInfo.toString());

        updateUI();

        Handler handler = new Handler();
        handler.postAtFrontOfQueue(new Runnable() {
            @Override
            public void run() {
                waitingCall();
                handler.postDelayed(this, 3000);

            }
        });


        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo.setStatus("Logout");
                sharedpreferences.edit()
                        .putString(SharedPrefConstants.USER_INFO, new Gson().toJson(userInfo))
                        .apply();

                asterLogout();

            }
        });

        bt_whatsaap_message_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveWhatsappMessage();
            }
        });

        bt_mail_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMail();
            }
        });

        bt_select_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pbxInfo.getQueueStatus().equals("resume")) {
                    Toast.makeText(getActivity(), "Pause the queue", Toast.LENGTH_SHORT).show();
                } else {
                    ll_multi_select_queue_view.setVisibility(View.VISIBLE);
                    ll_queue_view.setVisibility(View.GONE);
                }
            }
        });

        bt_save_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp_queue.getSelectedItemsAsString().isEmpty())
                    Toast.makeText(getActivity(), "Select the queue", Toast.LENGTH_SHORT).show();
                else {

                    for (UserInfo.Queue queue : userInfo.getDefaultUserQueue()) {
                        if (!sp_queue.getSelectedStrings().contains(queue.getName())) {
                            Toast.makeText(getActivity(), queue.getName() + " must be selected", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    updateUserQueue();
                    saveQueue();
                }

            }
        });

        bt_resume_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!permissionInfo.getQueue()) resumeQueue();
                else if (sp_queue.getSelectedItemsAsString().isEmpty())
                    Toast.makeText(getActivity(), "Select the queue", Toast.LENGTH_SHORT).show();
                else if (!sharedpreferences.getBoolean("queueSaved", false))
                    Toast.makeText(getActivity(), "Save the queue", Toast.LENGTH_SHORT).show();
                else resumeQueue();
            }
        });

        bt_pause_queue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int position = sp_pause_code.getSelectedItemPosition();
                pbxInfo.setPauseCode(sp_pause_code.getSelectedItem().toString());
                SharedPrefHelper.putPbxInfo(sharedpreferences, pbxInfo);
                if (!userInfo.getStatus().equals("InCall")) {
                    if (position == 0)
                        Toast.makeText(getActivity(), "Select the pause code", Toast.LENGTH_SHORT).show();
                    else {
                        pauseQueue();
                    }
                } else {
                    Toast.makeText(getActivity(), "Unable to pause while in call", Toast.LENGTH_SHORT).show();
                }

            }
        });


        ib_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncQueue();
            }
        });

        sw_auto_answer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedpreferences.edit().putBoolean("AutoAnswer", isChecked).apply();
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean sipAccount = sharedpreferences.getBoolean(SharedPrefConstants.SIP_REGISTERED, false);
                boolean sipRegistered = sipManager.isRegistered();
                if (sipRegistered) {
                    iv_sip_status.setImageResource(R.drawable.green_circle);
                } else {
                    iv_sip_status.setImageResource(R.drawable.red_circle);
                }
                if (!sipAccount) {
                    sip_actions.setVisibility(View.GONE);
                }

            }
        }, 3000);


        return rootView;
    }

    private void updateUserQueue() {
        userInfo.setSelectedUserQueue(new ArrayList<>());

        userInfo.getSelectedUserQueue().addAll(userInfo.getDefaultUserQueue());

        for (String s : sp_queue.getSelectedStrings()) {
            if (!userInfo.getDefaultUserQueueNames().contains(s)) {
                userInfo.getSelectedUserQueue().add(new UserInfo.Queue(s, 2));
            }
        }

        Log.d("userinfodefault", userInfo.getDefaultUserQueue().toString());
        Log.d("userinfoselected", userInfo.getSelectedUserQueue().toString());

        SharedPrefHelper.putUserInfo(sharedpreferences, userInfo);
    }

    private void saveMail() {
        String subject = et_mail_subject.getText().toString();
        String content = et_mail_content.getText().toString();

        if (subject.isEmpty()) et_mail_subject.setError("Subject is required");
        else if (content.isEmpty()) et_mail_content.setError("Required");
        else {
            sharedpreferences.edit()
                    .putString("mail_subject", subject)
                    .putString("mail_content", content)
                    .apply();


            Toast toast = Toast.makeText(getActivity(),
                    "Message Saved !", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            toast.show();
        }
    }

    private void saveWhatsappMessage() {


        String message = et_whatsaap.getText().toString();
        if (message.isEmpty()) {
            et_whatsaap.setError("Message is required");
        } else {


            if (sp_external_message.getSelectedItem().toString().equals("SMS")) {
                sharedpreferences.edit().putString("sms_message", message).apply();
            } else {
                sharedpreferences.edit().putString("whatsaapMessage", message).apply();
            }

            Toast toast = Toast.makeText(getActivity(),
                    "Message Saved !", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            toast.show();
        }
    }

    private void syncQueue() {
        JSONObject queueSync = new JSONObject();
        try {
            queueSync.put("tenant_id", userInfo.getTenantId());
            queueSync.put("station", userInfo.getExtension());
            queueSync.put("uniqueid", callInfo.getUniqueId());
            queueSync.put("action", "check-call-status");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("check-call-status", queueSync.toString());

        if (mSocket.connected()) {
            mSocket.emit("check-call-status", queueSync);
            Log.e("event TX", "check-call-status," + queueSync.toString());
            ApplicationInsight applicationInsight = new ApplicationInsight(requireContext().getApplicationContext(), getActivity().getApplication());
            applicationInsight.trackEvent("Click To Call", queueSync.toString());
        }
    }

    private void pauseQueue() {
        mSocket = app.getSocket();
        pbxInfo = SharedPrefHelper.getPbxInfo(sharedpreferences);
        JSONObject queuePause = new JSONObject();
        try {
            queuePause.put("action", "QueuePause");
            queuePause.put("agent", userInfo.getEmail());
            queuePause.put("station", userInfo.getExtension());
            queuePause.put("paused", true);
            queuePause.put("tenant_id", userInfo.getTenantId());
            queuePause.put("reason", pbxInfo.getPauseCode());
            sharedpreferences.edit().putBoolean("paused", true).apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (mSocket.connected()) {
            mSocket.emit("queue-pause", queuePause);
            Log.e("event TX", "queue-pause," + queuePause.toString());
            applicationInsight.trackEvent("Queue Pause", queuePause.toString());
        }
        Log.i("queuePause", queuePause.toString());
    }

    private void resumeQueue() {
        mSocket = app.getSocket();
        JSONObject queueResume = new JSONObject();
        try {
            queueResume.put("action", "QueuePause");
            queueResume.put("agent", userInfo.getEmail());
            queueResume.put("station", userInfo.getExtension());
            queueResume.put("paused", false);
            queueResume.put("tenant_id", userInfo.getTenantId());
            sharedpreferences.edit().putBoolean("paused", false).apply();
            boolean first_time = sharedpreferences.getBoolean("queuePaused_first_time", false);
            if (first_time) {
                sharedpreferences.edit().putBoolean("queuePaused_first_time", false).apply();
                queueResume.put("call", true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket.connected()) {
            mSocket.emit("queue-pause", queueResume);
            Log.e("event TX", "queue-pause," + queueResume.toString());
            applicationInsight.trackEvent("Queue Resume", queueResume.toString());
        }
    }

    private void saveQueue() {
        userInfo = getUserInfo(sharedpreferences);
        queueLogout();
        queueLogin();
        sharedpreferences.edit().putBoolean("queueSaved", true).apply();

    }

    private void queueLogout() {
        mSocket = app.getSocket();

        JSONObject queueLogout = new JSONObject();
        try {
            queueLogout.put("action", "QueueLogout");
            queueLogout.put("agent", userInfo.getEmail());
            queueLogout.put("station", userInfo.getExtension());
            queueLogout.put("current_queue", new JSONArray(new Gson().toJson(userInfo.getSelectedUserQueue())));
            queueLogout.put("queue", new JSONArray(userInfo.getSelectedUserQueueNames()));
            queueLogout.put("tenant_id", userInfo.getTenantId());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        if (mSocket.connected()) {
            mSocket.emit("queue-logout", queueLogout);
            Log.e("event TX", "queue-logout," + queueLogout.toString());
            applicationInsight.trackEvent("Queue Logout", queueLogout.toString());
        }
    }

    private void queueLogin() {
        mSocket = app.getSocket();
        JSONObject queueLogin = new JSONObject();
        try {
            queueLogin.put("action", "QueueLogin");
            queueLogin.put("agent", userInfo.getEmail());
            queueLogin.put("station", userInfo.getExtension());
            queueLogin.put("paused", true);
            queueLogin.put("queue", new JSONArray(new Gson().toJson(userInfo.getSelectedUserQueue())));
            queueLogin.put("penalty", "1");
            queueLogin.put("tenant_id", userInfo.getTenantId());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mSocket.connected()) {
            mSocket.emit("queue-login", queueLogin);
            Log.e("event TX", "queue-login," + queueLogin.toString());
            applicationInsight.trackEvent("Queue Login", queueLogin.toString());
        }
    }

    private void waitingCall() {
        boolean forceLoggedoutCheck = sharedpreferences.getBoolean(SharedPrefConstants.FORCE_LOGOUT, false);


        if (forceLoggedoutCheck && !userInfo.getStatus().equals("InCall") && !autoDialInfo.getStatus().equals("start")) {
            asterLogout();
        }
        if ((userInfo.getStatus().equals("InCall") || userInfo.getStatus().equals("Available")) && permissionInfo.getWaitingCalls()) {

            if (waitingList.size() > 0) {
                WaitingListAdapter waitingListAdapter = new WaitingListAdapter(waitingList, R.layout.layout_call_waiting, app.getApplicationContext());
                rv_waiting_list.setLayoutManager(new LinearLayoutManager(app.getApplicationContext()));
                rv_waiting_list.setAdapter(waitingListAdapter);
            }

            JSONObject waitingCallData = new JSONObject();
            try {
                String socket_id = sharedpreferences.getString("socket_id", "");
                waitingCallData.put("socket_id", socket_id);
                waitingCallData.put("station", userInfo.getExtension());
                waitingCallData.put("queue", new JSONArray(sp_queue.getSelectedStrings()));
                waitingCallData.put("type", "doocti");
                waitingCallData.put("agent", userInfo.getEmail());
                waitingCallData.put("tenant_id", userInfo.getTenantId());
            } catch (Exception e) {
                e.printStackTrace();
            }
            mSocket.emit("queue-waiting", waitingCallData);
            Log.e("event TX", "queue-waiting," + waitingCallData.toString());
        }
    }

    private void updateUI() {
        sipManager = new SipManager(app.getApplicationContext());
        sw_auto_answer.setChecked(sipManager.getAutoAnswerEnabled());


        List<String> penaltyList = new ArrayList<>();
        penaltyList.add("Priority");
        penaltyList.addAll(Arrays.asList("1", "2", "3", "4", "5"));
        ArrayAdapter penaltyAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, penaltyList);
        sp_penalty.setAdapter(penaltyAdapter);


        if (!pbxInfo.getPauseCodeArray().get(0).equals("Select Pause Code")) {
            pbxInfo.getPauseCodeArray().add(0, "Select Pause Code");
        }

        for (String s : pbxInfo.getQueueSelected()) Log.d("getselectedlist", s + " queue");

        sp_queue.setItems(pbxInfo.getQueueArrayNames());
        sp_queue.setSelection(pbxInfo.getQueueSelected());

        if (sp_queue.getSelectedItemsAsString().isEmpty()) {
            tv_queue_list.setText(getString(R.string.select_queue));
        } else {
            tv_queue_list.setText(sp_queue.getSelectedItemsAsString());
        }

        if (!pbxInfo.getPenalty().isEmpty()) {
            int spinnerPosition = penaltyAdapter.getPosition(pbxInfo.getPenalty());
            sp_penalty.setSelection(spinnerPosition);
        }

        if (sharedpreferences.getBoolean("paused", true)) {
            userInfo.setStatus("Unavailable");
            sharedpreferences.edit().putString(SharedPrefConstants.USER_INFO, new Gson().toJson(userInfo)).apply();
        }


        tv_user_name.setText(userInfo.getUsername());
        tv_extension.setText(userInfo.getExtension());

        Picasso.with(getActivity()).load(userInfo.getProfilePicture()).into(iv_profile_picture);
        Log.e("agentStatus", userInfo.getStatus());

        switch (userInfo.getStatus()) {
            case "Available":
                iv_status.setImageResource(R.drawable.green_circle);
                Log.e("Status", "Available");
                break;
            case "Unavailable":
                iv_status.setImageResource(R.drawable.red_circle);
                Log.e("Status", "Unavailable");
                break;
            case "InCall":
                iv_status.setImageResource(R.drawable.blue_circle);
                Log.e("Status", "InCall");
                break;
            default:
                iv_status.setImageResource(R.drawable.yellow_circle);
                Log.e("Status", "Default");
                break;
        }


        if (!(sharedpreferences.getString("whatsaapMessage", "")).isEmpty()) {
            et_whatsaap.setText(sharedpreferences.getString("whatsaapMessage", ""));
        }
        if (pbxInfo.getQueueStatus().equals("pause")) {
            ll_multi_select_queue_view.setVisibility(View.GONE);
            ll_queue_view.setVisibility(View.VISIBLE);
            ll_pause_code_view.setVisibility(View.GONE);
            ll_pause_view.setVisibility(View.VISIBLE);
            if (pbxInfo.getPauseCode().isEmpty()) {
                tv_pause_code_list.setText(permissionInfo.getQueue() ? "Queue Paused" : "Start Dialer");
            } else {
                tv_pause_code_list.setText(permissionInfo.getQueue() ? "Queue Pause (" + pbxInfo.getPauseCode() + ")" : "Start Dialer");

            }
        } else {
            ll_multi_select_queue_view.setVisibility(View.GONE);
            ll_queue_view.setVisibility(View.VISIBLE);
            ll_pause_code_view.setVisibility(View.VISIBLE);
            ll_pause_view.setVisibility(View.GONE);
        }

        ArrayAdapter pauseCodeAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, pbxInfo.getPauseCodeArray());
        sp_pause_code.setAdapter(pauseCodeAdapter);

        if (!permissionInfo.getWaitingCalls()) {
            rl_waiting_list.setVisibility(View.GONE);
        }

        if (!permissionInfo.getQueue()) {
            ll_queue_view.setVisibility(View.GONE);
        }

        rl_waiting_list.setVisibility(View.GONE);
        getPerformance();


        if (!sharedpreferences.getBoolean(SharedPrefConstants.FIRST_LOAD, false)) {
            sp_queue.setSelection(userInfo.getDefaultUserQueueNames());
            updateUserQueue();
            saveQueue();
        }


        setupExternalMessage();
        queuePauseHandler = new Handler();
        queuePauseRunnable = new Runnable() {
            public void run() {
                pbxInfo = getPbxInfo(sharedpreferences);

                Log.d("pbxinfohand", pbxInfo.toString());

                if (pbxInfo.getQueueStatus().equals("pause")) {
                    ll_multi_select_queue_view.setVisibility(View.GONE);
                    ll_queue_view.setVisibility(View.VISIBLE);
                    ll_pause_code_view.setVisibility(View.GONE);
                    ll_pause_view.setVisibility(View.VISIBLE);
                    iv_status.setImageResource(R.drawable.yellow_circle);

                    if (pbxInfo.getPauseCode().isEmpty()) {
                        tv_pause_code_list.setText(permissionInfo.getQueue() ? "Queue Paused" : "Start Dialer");
                    } else {
                        tv_pause_code_list.setText(permissionInfo.getQueue() ? "Queue Pause (" + pbxInfo.getPauseCode() + ")" : "Start Dialer");

                    }
                } else {
                    ll_multi_select_queue_view.setVisibility(View.GONE);
                    ll_queue_view.setVisibility(View.VISIBLE);
                    ll_pause_code_view.setVisibility(View.VISIBLE);
                    ll_pause_view.setVisibility(View.GONE);
                    iv_status.setImageResource(R.drawable.green_circle);

                }


            }
        };
        queuePauseHandler.post(queuePauseRunnable);


        queueLoginHandler = new Handler();
        queueLoginRunnable = new Runnable() {
            @Override
            public void run() {

                pbxInfo = SharedPrefHelper.getPbxInfo(sharedpreferences);
                Toast.makeText(app.getApplicationContext(), "Queue Login Success", Toast.LENGTH_SHORT).show();
                ll_multi_select_queue_view.setVisibility(View.GONE);
                ll_queue_view.setVisibility(View.VISIBLE);
                sp_queue.setSelection(pbxInfo.getQueueSelected());
                if (sp_queue.getSelectedItemsAsString().isEmpty()) {
                    tv_queue_list.setText(getString(R.string.select_queue));
                } else {
                    tv_queue_list.setText(sp_queue.getSelectedItemsAsString());
                }
                pbxInfo.setPenalty(sp_penalty.getSelectedItem().toString());
                SharedPrefHelper.putPbxInfo(sharedpreferences,pbxInfo);
            }
        };

        sip_actions.setVisibility(View.GONE);

    }


    private void setupExternalMessage() {
        SpinnerAdapter externalMessageAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, Arrays.asList("WhatsApp", "Email", "SMS"));
        sp_external_message.setAdapter(externalMessageAdapter);

        sp_external_message.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String itemSelected = adapterView.getItemAtPosition(i).toString();
                if (itemSelected.equals("WhatsApp") || itemSelected.equals("SMS")) {

                    ll_mail.setVisibility(View.GONE);

                    et_whatsaap.setVisibility(View.VISIBLE);
                    bt_whatsaap_message_save.setVisibility(View.VISIBLE);

                    et_whatsaap.setHint(itemSelected + " message");
                }

                if (itemSelected.equals("Email")) {

                    et_whatsaap.setVisibility(View.GONE);
                    bt_whatsaap_message_save.setVisibility(View.GONE);

                    ll_mail.setVisibility(View.VISIBLE);

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void initializeSocketEvents() {
        queueWaitingResponse = getQueueWaitingResponse();
        forceLogoutCheck = getForceLogoutCheckResponse();
    }

//    private Emitter.Listener getQueuePauseResponse() {
//
//        return new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//
//                try {
//                    for (Object arg : args) {
//
//                        final JSONObject data = (JSONObject) arg;
//                        Log.i("queue-pause-response", data.toString());
//
//
//                        try {
//                            if (data.getBoolean("paused")) {
//
//                                if (data.getJSONArray("message").getJSONObject(0).getString("response").equals("Success")) {
//
//
//                                    pbxInfo.setQueueStatus("pause");
//
//                                    pbxInfo.setPauseCode(data.getString("reason"));
//
//                                    userInfo.setStatus(data.getString("reason"));
//
//
//                                    sharedpreferences.edit()
//                                            .putString(PBX_INFO, new Gson().toJson(pbxInfo))
//                                            .putString(SharedPrefConstants.USER_INFO, new Gson().toJson(userInfo))
//                                            .apply();
//
////                                    startActivity(new Intent(app.getApplicationContext(),DialerActivity.class));
//
//
//                                    queuePauseHandler.postDelayed(queuePauseRunnable, 500);
//
//
////                                                ll_pause_code_view.setVisibility(View.GONE);
////                                                ll_pause_view.setVisibility(View.VISIBLE);
////                                                tv_pause_code_list.setText(permissionInfo.getQueue() ? "Queue Pause (" + pbxInfo.getPauseCode() + ")" : "Start Dialer");
////                                                iv_status.setImageResource(R.drawable.yellow_circle);
//                                } else {
//                                    Toast.makeText(getActivity(), "Queue Pause Failed", Toast.LENGTH_SHORT).show();
//                                }
//
//
//                            } else {
//                                if (data.getJSONArray("message").getJSONObject(0).getString("response").equals("Success")) {
////                                    Toast.makeText(app.getApplicationContext(), "Queue Resume Success", Toast.LENGTH_SHORT).show();
//
//
//                                    pbxInfo.setQueueStatus("resume");
//                                    pbxInfo.setPauseCode("");
//                                    userInfo.setStatus("Available");
//
//
//                                    sharedpreferences.edit()
//                                            .putString(PBX_INFO, new Gson().toJson(pbxInfo))
//                                            .putString(SharedPrefConstants.USER_INFO, new Gson().toJson(userInfo))
//                                            .apply();
//
//
////                                    startActivity(new Intent(app.getApplicationContext(),DialerActivity.class));
//
//                                    queuePauseHandler.postDelayed(queuePauseRunnable, 500);
//
//
////                                                ll_pause_code_view.setVisibility(View.VISIBLE);
////                                                ll_pause_view.setVisibility(View.GONE);
////                                                iv_status.setImageResource(R.drawable.green_circle);
////                                                tv_pause_code_list.setText(permissionInfo.getQueue() ? "Queue Paused" : "Start Dialer");
//
//                                } else {
//                                    Toast.makeText(app.getApplicationContext(), "Queue Resume Failed", Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        };
//
//
//    }

//    private Emitter.Listener getQueuePauseResponse() {
//        return new Emitter.Listener() {
//            @Override
//            public void call(final Object... args) {
//
//                for (Object arg : args) {
//
//                    JSONObject data = (JSONObject) arg;
//                    Log.w("event Rx","queue-pause-response,"+ data.toString());
//                    pbxInfo = SharedPrefHelper.getPbxInfo(sharedpreferences);
//                    userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
//
//                    try {
//                        if (data.getJSONArray("message").getJSONObject(0).getString("response").equals("Success") && data.getBoolean("paused")) {
//                            pbxInfo.setPauseCode(data.getString("reason"));
//                            userInfo.setStatus(data.getString("reason"));
//                            pbxInfo.setQueueStatus("pause");
//                        } else {
//                            pbxInfo.setQueueStatus("resume");
//                            pbxInfo.setPauseCode("");
//                            userInfo.setStatus("Available");
//                        }
//                        sharedpreferences.edit()
//                                .putString(PBX_INFO, new Gson().toJson(pbxInfo))
//                                .putString(SharedPrefConstants.USER_INFO, new Gson().toJson(userInfo))
//                                .apply();
//
//                        Activity activity = getActivity();
//                        if(activity != null) {
//                            getActivity().runOnUiThread(new Runnable() {
//                                @SuppressLint("SetTextI18n")
//                                @Override
//                                public void run() {
//
//                                    try {
//                                        if (data.getBoolean("paused")) {
//                                            Toast.makeText(app.getApplicationContext(), "Queue Pause Success", Toast.LENGTH_SHORT).show();
//
//                                            if (data.getJSONArray("message").getJSONObject(0).getString("response").equals("Success")) {
//                                                ll_pause_code_view.setVisibility(View.GONE);
//                                                ll_pause_view.setVisibility(View.VISIBLE);
//
//                                                userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
//                                                tv_pause_code_list.setText(permissionInfo.getQueue() ? "Queue Pause (" + pbxInfo.getPauseCode() + ")" : "Start Dialer");
//                                                iv_status.setImageResource(R.drawable.yellow_circle);
//                                            } else {
//                                                Toast.makeText(getActivity(), "Queue Pause Failed", Toast.LENGTH_SHORT).show();
//                                            }
//
//
//                                        } else {
//                                            if (data.getJSONArray("message").getJSONObject(0).getString("response").equals("Success")) {
//                                                Toast.makeText(app.getApplicationContext(), "Queue Resume Success", Toast.LENGTH_SHORT).show();
//                                                ll_pause_code_view.setVisibility(View.VISIBLE);
//                                                ll_pause_view.setVisibility(View.GONE);
//                                                iv_status.setImageResource(R.drawable.green_circle);
//                                                tv_pause_code_list.setText(permissionInfo.getQueue() ? "Queue Paused" : "Start Dialer");
//                                            } else {
//                                                Toast.makeText(app.getApplicationContext(), "Queue Resume Failed", Toast.LENGTH_SHORT).show();
//                                            }
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//
//            }
//        };


//    }

    private Emitter.Listener getForceLogoutCheckResponse() {
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                try {
                    for (Object arg : args) {
                        JSONObject forceLogoutStatus = (JSONObject) arg;
                        Log.w("event Rx", "force-logout-response," + forceLogoutStatus.toString());
                        if (forceLogoutStatus.getString("user").equals(userInfo.getEmail())) {
                            sharedpreferences.edit().putBoolean(SharedPrefConstants.FORCE_LOGOUT, true).apply();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        };
    }

    private Emitter.Listener getQueueWaitingResponse() {
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                try {
                    for (Object arg : args) {

                        JSONObject waitingObject = (JSONObject) arg;
                        Log.w("event Rx", "queue-waiting-response," + waitingObject.toString());

                        JSONArray queue_array = (JSONArray) waitingObject.getJSONArray("calls");
                        int size = queue_array.length();

                        ArrayList<JSONObject> arrays = new ArrayList<>();

                        for (int i = 0; i < size; i++) {
                            JSONObject another_json_object = queue_array.getJSONObject(i);
                            arrays.add(another_json_object);
                        }

                        JSONObject[] jsons = new JSONObject[arrays.size()];
                        arrays.toArray(jsons);
                        if (arrays.size() > 0) {
                            waitingList.clear();
                        }
                        for (int i = 0; i < arrays.size(); i++) {
                            JSONObject tempJson = (JSONObject) arrays.get(i);
                            String phone_number = tempJson.getString("phone_number");
                            String queue = tempJson.getString("queue");
                            String entry = tempJson.getString("entry_time");
                            String duration = tempJson.getString("duration");
                            String[] newString = {phone_number, queue, entry, duration};
                            waitingList.add(newString);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };


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


                    pbxInfo.setQueueSelected(selectedQueueList);

                    pbxInfo.setPenalty(sp_penalty.getSelectedItem().toString());
                    sharedpreferences.edit()
                            .putString(SharedPrefConstants.PBX_INFO, new Gson().toJson(pbxInfo))
                            .putBoolean(SharedPrefConstants.FIRST_LOAD, true)
                            .apply();

                    queueLoginHandler.postDelayed(queueLoginRunnable, 500);

                }
            }
        };


    }

    public void getInfosFromSharedPreferences() {
        sharedpreferences = requireActivity().getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userInfo = getUserInfo(sharedpreferences);
        permissionInfo = getPermissionInfo(sharedpreferences);
        callInfo = getCallInfo(sharedpreferences);
        autoDialInfo = getAutoDialInfo(sharedpreferences);
        pbxInfo = getPbxInfo(sharedpreferences);

        userInfo = getUserInfo(sharedpreferences);
    }


    private void asterLogout() {
        if (!userInfo.getStatus().equals("InCall")) {

            ApiInfo apiInfo = getApiInfo(sharedpreferences);

            pb_loader.setVisibility(View.VISIBLE);
            rl_logout.setVisibility(View.GONE);
            rl_waiting_list.setVisibility(View.GONE);
            sip_actions.setVisibility(View.GONE);
            rl_whatsaap.setVisibility(View.GONE);

            Services services = RetrofitAsterClient.getClient(apiInfo.getDialerDomain()).create(Services.class);
            Call<LogoutResponse> call = services.logout(userInfo.getToken(), new LogoutRequest(userInfo.getEmail()));
            call.enqueue(new Callback<LogoutResponse>() {
                @Override
                public void onResponse(@NonNull Call<LogoutResponse> call, @NonNull Response<LogoutResponse> response) {
                    try {

                        int code = response.body().getStatus();

                        if (code == 200) {
                            sqliteDB.clearData();

                            JSONObject queueLogout = new JSONObject();
                            try {
                                queueLogout.put("action", "QueueLogout");
                                queueLogout.put("agent", userInfo.getEmail());
                                queueLogout.put("station", userInfo.getExtension());
                                queueLogout.put("current_queue", new JSONArray());
                                queueLogout.put("queue", new JSONArray(sp_queue.getSelectedStrings()));
                                queueLogout.put("tenant_id", userInfo.getTenantId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (mSocket.connected()) {
                                mSocket.emit("queue-logout", queueLogout);
                                Log.e("event TX", "queue-logout," + queueLogout.toString());
                            }
                            JSONObject Logout = new JSONObject();
                            try {
                                Logout.put("action", "user-logout");
                                Logout.put("agent", userInfo.getEmail());
                                Logout.put("station", userInfo.getExtension());
                                Logout.put("tenant_id", userInfo.getTenantId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (mSocket.connected()) {
                                mSocket.emit("user-logout", Logout);
                                Log.e("event TX", "user-logout," + Logout.toString());
                            }
                            if (mSocket.connected()) {
                                mSocket.disconnect();
                            }

                            bt_logout.setEnabled(false);
                            if (sipManager.register()) {
                                sipManager.unRegister();
                            }

                            Intent service = new Intent(getActivity(), EventListener.class);
                            requireActivity().stopService(service);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(SharedPrefConstants.LOGIN, "false");
                            editor.putString(SharedPrefConstants.USER_INFO, "");
                            editor.putString(SharedPrefConstants.PBX_INFO, "");
                            editor.putString(SharedPrefConstants.API_INFO, "");
                            editor.putString(SharedPrefConstants.AUTO_DIAL_INFO, "");
                            editor.clear();
                            editor.apply();
                            Log.e("sharedPreference", String.valueOf(sharedpreferences));

                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            intent.putExtra(LoginActivity.KEY_CLEAR_CREDENTIALS, true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            requireActivity().finish();
                        } else {
                            pb_loader.setVisibility(View.GONE);
                            rl_logout.setVisibility(View.VISIBLE);
                            bt_logout.setEnabled(false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    pb_loader.setVisibility(View.GONE);
                    rl_logout.setVisibility(View.VISIBLE);
                    bt_logout.setEnabled(false);
                }
            });
        } else {
            Toast.makeText(app.getApplicationContext(), "Can't logout while in call", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPerformance() {

        String startTime = sharedpreferences.getString("callStartTime", Calendar.getInstance().getTime().toString());
        String endTime = sharedpreferences.getString("callEndTime", Calendar.getInstance().getTime().toString());
        ApiInfo apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);


        Services services = RetrofitAsterClient.getClient(apiInfo.getDialerDomain()).create(Services.class);
        Call<PerformanceResponse> call = services.getPerformance(userInfo.getToken(), startTime, endTime, userInfo.getEmail());

        call.enqueue(new Callback<PerformanceResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<PerformanceResponse> call, Response<PerformanceResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getData().size() > 0) {
                        int total = response.body().getData().get(0).getAnswer() +
                                response.body().getData().get(0).getUnanswer();
                        unanswered_calls.setText(response.body().getData().get(0).getUnanswer() + "");
                        answered_calls.setText(response.body().getData().get(0).getAnswer() + "");
                        total_calls.setText(total + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<PerformanceResponse> call, Throwable t) {
                Log.e("error", t.getMessage());
            }
        });
    }
}

