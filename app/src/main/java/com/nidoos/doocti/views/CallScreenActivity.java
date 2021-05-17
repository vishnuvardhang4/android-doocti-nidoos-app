package com.nidoos.doocti.views;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.nidoos.doocti.info.CustomerInfo;
import com.nidoos.doocti.info.PbxInfo;
import com.nidoos.doocti.info.PermissionInfo;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.request.CallActivityRequest;
import com.nidoos.doocti.request.ConferenceRequest;
import com.nidoos.doocti.request.CreateMeetingRequest;
import com.nidoos.doocti.request.TimelineRequest;
import com.nidoos.doocti.request.UpdateLeadstatusRequest;
import com.nidoos.doocti.response.CallActivityResponse;
import com.nidoos.doocti.response.ConferenceResponse;
import com.nidoos.doocti.response.CreateMeetingResponse;
import com.nidoos.doocti.response.CrmUrlResponse;
import com.nidoos.doocti.response.LeadViewDB;
import com.nidoos.doocti.response.SubFilterResponse;
import com.nidoos.doocti.response.TimelineResponse;
import com.nidoos.doocti.response.UpdateLeadstatusResponse;
import com.nidoos.doocti.retrofit.RetrofitApiClient;
import com.nidoos.doocti.retrofit.RetrofitAsterClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.socket.EventListener;
import com.nidoos.doocti.sqliteDB.SqliteDB;
import com.nidoos.doocti.utils.ApplicationInsight;
import com.nidoos.doocti.utils.Internet;
import com.nidoos.doocti.utils.MyPhoneStateListener;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.utils.SipManager;
import com.nidoos.doocti.views.Adapter.ConferenceAdapter;
import com.nidoos.doocti.views.Adapter.TimeLineAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nidoos.doocti.constants.SharedPrefConstants.CALL_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.CUSTOMER_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.USER_INFO;
import static com.nidoos.doocti.constants.SocketConstants.*;

public class
CallScreenActivity extends AppCompatActivity {


    @BindView(R.id.tv_timeline)
    TextView tv_timeline;

    @BindView(R.id.tv_info)
    TextView tv_info;

    @BindView(R.id.tv_lead_name)
    TextView tv_lead_name;

    @BindView(R.id.ll_leadstatus)
    LinearLayout ll_leadstatus;

    @BindView(R.id.sp_leadstatus)
    Spinner sp_leadstatus;

    @BindView(R.id.tv_company_name)
    TextView tv_company_name;

    @BindView(R.id.tv_call_event)
    TextView tv_call_event;

    @BindView(R.id.tv_call_type)
    TextView tv_call_type;

    @BindView(R.id.tv_phone_number)
    TextView tv_phone_number;

    @BindView(R.id.bt_leadstatus_save)
    Button bt_leadstatus_save;

    @BindView(R.id.bt_hangup)
    Button bt_hangup;

    @BindView(R.id.bt_more_info)
    Button bt_more_info;

    @BindView(R.id.sp_dispo_status)
    Spinner sp_dispo_status;

    @BindView(R.id.pb_loader)
    ProgressBar pb_loader;

    @BindView(R.id.pb_loader_1)
    ProgressBar pb_loader_1;

    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    @BindView(R.id.rv_time_line)
    RecyclerView rv_time_line;

    @BindView(R.id.rl_call_screen)
    RelativeLayout rl_call_screen;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.bt_hang_call_sip)
    Button hangCallSip;

    @BindView(R.id.bt_hold_call_sip)
    Button holdCallSip;

    @BindView(R.id.bt_mute_call_sip)
    Button muteCallSip;

    @BindView(R.id.bt_whatsaap)
    Button whatsAap;

    @BindView(R.id.wv_asterdialer)
    WebView wv_asterdialer;

    @BindView(R.id.bt_mail)
    Button bt_mail;

    @BindView(R.id.bt_sms)
    Button bt_sms;


    private AlertDialog transferDialog, meetingDialog, descriptionDialog, conferenceDialog;
    private String subject = "", duration = "00:00", transfer_number = "", calldate, call_status = "", leadEmail = "";
    private String dispo_status_ = "";
    private Doocti app;
    private Socket mSocket;
    private SharedPreferences sharedpreferences;
    private SqliteDB sqliteDB;
    private ArrayAdapter<String> statusAdapter;
    private CallActivityRequest.CallResult dispo_status;
    private int position = 0;
    private ImageView iv_call_transfer, iv_add_notes, iv_create_meeting, iv_lead_info, iv_back, iv_create_lead, iv_conference,iv_hold,iv_mute;
    private List<LeadViewDB> leadView = new ArrayList<>();
    private List<String> statusList = new ArrayList<>();
    private List<String> queueArray = new ArrayList<>();
    private Map<String, String> queueMap = new HashMap<String, String>();
    private List<String> agents = new ArrayList<>();
    private ArrayList<String[]> conferencePeople = new ArrayList<String[]>();
    private List<String> participants = new ArrayList<String>();
    private String watsapp_url = "";
    public ArrayList<String> status_filter = new ArrayList<String>();


    private boolean webview = false, contactAdded = true;
    private ConferenceAdapter conferenceAdapter;
    private TimeLineAdapter timeLineAdapter;


    private ApplicationInsight applicationInsight;

    private SipManager sipManager;
    private Button answerCallSip;

    private UserInfo userInfo;
    private PermissionInfo permissionInfo;
    private AutoDialInfo autoDialInfo;
    private CallInfo callInfo;
    private CustomerInfo customerInfo;
    private ApiInfo apiInfo;
    private PbxInfo pbxInfo;


    private Emitter.Listener onConference = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                for (Object arg : args) {
                    String event = "";

                    JSONObject conferenceData = (JSONObject) arg;
                    Log.w("event Rx","conference-response,"+ conferenceData.toString());
                    try {
                        event = conferenceData.getString("action");
//                        Log.e("conference",event);
                        if (event.equals("conference")) {
                            Log.e("conference", "created");
                            String[] myString1 = {conferenceData.getString("tenant_id"), conferenceData.getString("add_number"), conferenceData.getString("station"), conferenceData.getString("uniqueid"), conferenceData.getString("actionid"), conferenceData.getString("live_channel"), conferenceData.getString("customer_channel"), conferenceData.getString("conference_no")};
                            conferencePeople.add(myString1);

                            String[] myString = new String[8];
                            myString = (String[]) conferencePeople.get(position);
                            Log.e("conferenceObject", Arrays.toString(myString));
                            position = position + 1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                for (Object arg : args) {
                    String event;
                    JSONObject data = (JSONObject) arg;
                    Log.w("event Rx","onNewMsg,"+ data.toString());
                    try {
                        event = data.getString("evt");
                        if (event.equals("RINGING")) {
                            tv_call_event.setText(event);
                            break;
                        } else if (event.equals("ANSWERED") && callInfo.getUniqueId().equals(data.getString("uniqueid"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    callInfo.setEvent(event);
                                    tv_call_event.setText(event);

                                    try {
                                        callInfo.setAgentChannel(data.getString("agent_channel"));
                                        callInfo.setCallerChannel(data.getString("caller_channel"));
                                        callInfo.setAnswerTime(data.getString("answer_time"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }
                            });
                        } else if (event.equals("DISPO") && callInfo.getUniqueId().equals(data.getString("uniqueid"))) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (transferDialog != null && transferDialog.isShowing()) {
                                        transferDialog.dismiss();
                                    }

                                    callInfo.setEvent(event);
                                    tv_call_event.setText(event);
                                }
                            });
                        } else if (event.equals("Drop") && callInfo.getUniqueId().equals(data.getString("uniqueid"))) {
                            goToDialerActivity();
                        }
                    } catch (Exception e) {
                        Log.e("", e.getMessage());
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
//    private Emitter.Listener onHangupResponse = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            Log.w("event Rx","onHangupRes");
//
//            try {
//                for (Object arg : args) {
//                    JSONObject data = (JSONObject) arg;
//                    Log.w("event Rx","onHangupRes,"+ data.toString());
//                    sharedpreferences.edit().putBoolean(SharedPrefConstants.HANGUP_RESPONSE, true).apply();
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };
    private Emitter.Listener onTransferResponse = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                for (Object arg : args) {
                    JSONObject data = (JSONObject) arg;
                    Log.w("event Rx","onTansferRes,"+ data.toString());
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Transfer Success", Toast.LENGTH_SHORT).show();
                                transfer_number = "";
                            }
                        });
                    } catch (Exception e) {
                        Log.e("", e.getMessage());
                        return;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_screen);
        ButterKnife.bind(this);


        getInfosFromSharedPreferences();


        app = (Doocti) getApplication();
        mSocket = app.getSocket();

        mSocket.on(Socket.EVENT_MESSAGE, onNewMessage);
        mSocket.on(TRANSFER_RESPONSE, onTransferResponse);
        mSocket.on(CONFERENCE_RESPONSE, onConference);
//        mSocket.on(HANGUP_RESPONSE, onHangupResponse);


        initializeVariables();

        sqliteDB = new SqliteDB(this);

        getInfosFromSharedPreferences();


        sipManager = new SipManager(getApplicationContext());
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(new MyPhoneStateListener(getApplicationContext()), PhoneStateListener.LISTEN_CALL_STATE);


        Log.i("userInfo", userInfo.toString());
        Log.i("pbxInfo", pbxInfo.toString());
        Log.i("apiInfo", apiInfo.toString());
        Log.i("callInfo", callInfo.toString());
        Log.i("customerInfo", customerInfo.toString());

        boolean incoming = getIntent().getBooleanExtra("Incoming", true);

        boolean sipRegistered = sharedpreferences.getBoolean(SharedPrefConstants.SIP_REGISTERED, false);
        Log.e("sipReg", Boolean.toString(sipRegistered));
        if (sipRegistered) {
            sipManager.setCall();
        }

        if (incoming && sipRegistered) {
            answerCallSip.setVisibility(View.VISIBLE);
            holdCallSip.setVisibility(View.VISIBLE);
            muteCallSip.setVisibility(View.VISIBLE);
            hangCallSip.setVisibility(View.VISIBLE);
            getIntent().putExtra("Incoming", false);
        }

        iv_mute.setVisibility(View.INVISIBLE);
        iv_hold.setVisibility(View.INVISIBLE);
        answerCallSip.setVisibility(View.INVISIBLE);
        holdCallSip.setVisibility(View.INVISIBLE);
        muteCallSip.setVisibility(View.INVISIBLE);
        hangCallSip.setVisibility(View.INVISIBLE);
        wv_asterdialer.setVisibility(View.INVISIBLE);

        if (userInfo.getCrmDomain().equals("asterdialer")) {
            iv_create_lead.setVisibility(View.GONE);
            iv_lead_info.setVisibility(View.GONE);
        }

        if (!permissionInfo.getViewTransfer()) {
            iv_call_transfer.setVisibility(View.INVISIBLE);
        }

        if (!permissionInfo.getViewAgentConference()) {
            iv_conference.setVisibility(View.GONE);
        }

        if (!permissionInfo.getCreateMeeting()) {
            iv_create_meeting.setVisibility(View.GONE);
        }

        if (!permissionInfo.getCreateNote()) {
            iv_add_notes.setVisibility(View.GONE);
        }

        if (!permissionInfo.getViewUserInfo()) {
            iv_lead_info.setVisibility(View.GONE);
        }

        if (permissionInfo.getNoDispo()) {
            sp_dispo_status.setVisibility(View.INVISIBLE);
        }

        if (!permissionInfo.getiFramePopup()) {
            bt_more_info.setVisibility(View.INVISIBLE);
        }

        try {
            if (!permissionInfo.getShowStatusChange()) {
                tv_info.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!permissionInfo.getPopup()) {
            bt_hangup.setVisibility(View.INVISIBLE);
            sp_dispo_status.setVisibility(View.INVISIBLE);
        }

        rv_time_line.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (userInfo.getCrmDomain().equals("asterdialer") && permissionInfo.getViewTimeline()) {
            getTimeLine();
        } else if (!customerInfo.getLeadId().isEmpty() && permissionInfo.getViewTimeline()) {
            iv_lead_info.setVisibility(View.VISIBLE);
            getTimeLine();
        } else {
            rv_time_line.setVisibility(View.GONE);
            pb_loader_1.setVisibility(View.GONE);
            tv_no_data.setVisibility(View.VISIBLE);
            iv_lead_info.setVisibility(View.INVISIBLE);
        }


        if (customerInfo.getCrmUrl().isEmpty()) {
            getCrmUrl(customerInfo.getModule(), customerInfo.getLeadId());
        }


        try {
            if (!mSocket.connected()) {
                Intent intent = new Intent(CallScreenActivity.this, EventListener.class);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        tv_call_event.setText(callInfo.getEvent());
        tv_call_type.setText(callInfo.getCallType());
        tv_phone_number.setText(callInfo.getPhoneNumber());

        if (callInfo.getEvent().isEmpty()) {
            goToDialerActivity();
        }


        if (callInfo.getEvent().equals("DISPO") || callInfo.getEvent().equals("ANSWERED")) {
            answerCallSip.setVisibility(View.INVISIBLE);
        }

        if (customerInfo.getModule().equals("Leads") || customerInfo.getModule().equals("lead") || customerInfo.getModule().equals("leads")) {
            statusList = sqliteDB.get_values("LEADCALLSTATUS", "Select Dispo Status");
        } else {
            statusList = sqliteDB.get_values("CONTACTCALLSTATUS", "Select Dispo Status");
        }

        statusAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, statusList);
        sp_dispo_status.setAdapter(statusAdapter);

        whatsAap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = sharedpreferences.getString("whatsaapMessage", "");
                watsapp_url = "https://wa.me/91" + callInfo.getPhoneNumber() + "?text=" + message;
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(watsapp_url));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        bt_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = sharedpreferences.getString("sms_message","");
                Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + callInfo.getPhoneNumber()));
                smsIntent.putExtra("sms_body", message);
                startActivity(smsIntent);
            }
        });


        bt_mail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String subject = sharedpreferences.getString("mail_subject","");
                String content = sharedpreferences.getString("mail_content","");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
//                intent.putExtra(Intent.EXTRA_EMAIL, addresses);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                emailIntent.putExtra(Intent.EXTRA_TEXT, content);
                if (emailIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(emailIntent);
                }

            }
        });

        tv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_info.setBackgroundColor(getResources().getColor(R.color.tabColor));
                tv_timeline.setBackgroundColor(getResources().getColor(R.color.untabColor));
                tv_no_data.setVisibility(View.GONE);
                whatsAap.setVisibility(View.GONE);
                bt_mail.setVisibility(View.GONE);
                bt_sms.setVisibility(View.GONE);
                wv_asterdialer.setVisibility(View.GONE);
                ll_leadstatus.setVisibility(View.VISIBLE);
                rv_time_line.setVisibility(View.GONE);
                getSubFilter();
            }
        });

        tv_timeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_timeline.setBackgroundColor(getResources().getColor(R.color.tabColor));
                tv_info.setBackgroundColor(getResources().getColor(R.color.untabColor));
                whatsAap.setVisibility(View.VISIBLE);
                bt_mail.setVisibility(View.VISIBLE);
                bt_sms.setVisibility(View.VISIBLE);
                ll_leadstatus.setVisibility(View.GONE);
                rv_time_line.setVisibility(View.VISIBLE);

                rv_time_line.setLayoutManager(new LinearLayoutManager(CallScreenActivity.this));
                if (userInfo.getCrmDomain().equals("asterdialer") && permissionInfo.getViewTimeline()) {
                    getTimeLine();
                } else if (!customerInfo.getLeadId().isEmpty() && permissionInfo.getViewTimeline()) {
                    iv_lead_info.setVisibility(View.VISIBLE);
                    getTimeLine();
                } else {
                    rv_time_line.setVisibility(View.GONE);
                    pb_loader_1.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                    iv_lead_info.setVisibility(View.INVISIBLE);
                }
            }
        });

        bt_leadstatus_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String LeadStatus = "";
                UpdateLeadstatusRequest.LeadStatus leadStatus = sqliteDB.getLeadStatus("SUBFILTER", sp_leadstatus.getSelectedItem().toString());

                int position = sp_leadstatus.getSelectedItemPosition();
                if (position > 0 && !customerInfo.getLeadId().isEmpty()) {
                    LeadStatus = sp_leadstatus.getSelectedItem().toString();
                    Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
                    Call<UpdateLeadstatusResponse> call = null;
                    try {
                        call = services.updateLeadStatus(userInfo.getToken(), userInfo.getCrmDomain(), new UpdateLeadstatusRequest(leadStatus, customerInfo.getLeadId(), customerInfo.getOwnerId()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    assert call != null;
                    call.enqueue(new Callback<UpdateLeadstatusResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<UpdateLeadstatusResponse> call, @NonNull Response<UpdateLeadstatusResponse> response) {
                            if (response.isSuccessful()) {
                                if (response.code() == 200) {
                                    pb_loader.setVisibility(View.GONE);
                                    Toast.makeText(CallScreenActivity.this, "Lead Status Updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    pb_loader.setVisibility(View.GONE);
                                }
                            } else {
                                pb_loader.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<UpdateLeadstatusResponse> call, @NonNull Throwable t) {
                            pb_loader.setVisibility(View.GONE);
                            applicationInsight.trackEvent("SubFilter Exception", t.toString());
                        }
                    });
                } else if (customerInfo.getLeadId().isEmpty()) {
                    Toast.makeText(CallScreenActivity.this, "This user is not a lead", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CallScreenActivity.this, "select the status", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bt_more_info.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"SetTextI18n", "SetJavaScriptEnabled"})
            @Override
            public void onClick(View v) {
                String ring_api_url = null;
                String ringApiParameter = null;
                String campaign = null;

                ring_api_url = userInfo.getRingApiUrl();
                campaign = sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN);
                ringApiParameter = userInfo.getRingApiParameters();


                assert ringApiParameter != null;
                String[] ringApiParameters = ringApiParameter.split(",");
                String tempUrl = ring_api_url + "?" + ringApiParameters[0] + "=" + campaign + "&" + ringApiParameters[1] + "=" + callInfo.getPhoneNumber();
                Log.e("temp_url", tempUrl);
                Log.e("ringAPIArray", Arrays.toString(ringApiParameters));
                if (webview) {
                    wv_asterdialer.setVisibility(View.INVISIBLE);
                    bt_more_info.setText(R.string.moreInfo);
                    tv_timeline.setText(R.string.timeline);
                    webview = false;
                } else {
                    tv_timeline.setText("Lead Details");
                    wv_asterdialer.setVisibility(View.VISIBLE);
                    wv_asterdialer.setWebChromeClient(new WebChromeClient());
                    WebSettings webSettings = wv_asterdialer.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setPluginState(WebSettings.PluginState.ON);
                    webSettings.setPluginState(WebSettings.PluginState.ON_DEMAND);
                    webview = true;
                    bt_more_info.setText("hide");
//                    tempUrl = "https://test-asterdialer.doocti.com/aster-dialer-customform/index.php/agent_c/leadView?campaign=preview&callInfoObj.getPhoneNumber()=7338814711";
                    String html = "<iframe width=\"800\" height=\"500\" src=" + tempUrl + "></iframe>";
                    Log.e("data string", html);
                    wv_asterdialer.loadData(html, "text/html", "UTF-8");
                    wv_asterdialer.setWebViewClient(new WebViewClient() {
                        @Override
                        public boolean shouldOverrideUrlLoading(WebView v, String url) {
                            v.loadUrl(url);
                            return true;
                        }
                    });
                }
            }
        });

        Handler forNetwork = new Handler();
        Runnable NetworkChecker = new Runnable() {
            @Override
            public void run() {
                if (!Internet.isConnected(getApplicationContext()) && !mSocket.connected()) {
                    bt_hangup.setEnabled(false);
                    sp_dispo_status.setEnabled(false);
                    sharedpreferences.edit().putBoolean("internetDisconnectFirst", true).apply();
                } else {
                    boolean check = sharedpreferences.getBoolean("internetDisconnectFirst", false);
                    if (check) {
                        Toast.makeText(CallScreenActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                        sharedpreferences.edit().putBoolean("internetDisconnectFirst", false).apply();
                    }
                    bt_hangup.setEnabled(true);
                    sp_dispo_status.setEnabled(true);
                }
                if (userInfo.getStatus().equals("InCall")) {
                    forNetwork.postDelayed(this, 3000);
                }
            }
        };
        forNetwork.post(NetworkChecker);

        bt_hangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = sp_dispo_status.getSelectedItemPosition();
                dispo_status_ = sp_dispo_status.getSelectedItem().toString();
                boolean inCallSharedPref = sharedpreferences.getBoolean("incall",false);
//                Log.d("incallsharedpref",inCallSharedPref + "");
//                if(inCallSharedPref) {
//                    Toast.makeText(getApplicationContext(), "Phone is In Call", Toast.LENGTH_SHORT).show();
//                    return;
//                }


                if (permissionInfo.getCrmApi() && !permissionInfo.getNoDispo() && permissionInfo.getCreateNote()) {
                    if (position == 0) {
                        Toast.makeText(getApplicationContext(), "Select Dispo Status", Toast.LENGTH_SHORT).show();
                    } else {
                        sharedpreferences.edit().putBoolean("AutoAnswer", false).apply();
                        if (customerInfo.getDescription().equals("")) {
                            notesDialog();

                            Handler handler = new Handler();
                            handler.post(new Runnable() {
                                public void run() {
                                    if (!customerInfo.getDescription().equals("")) {
                                        hangupCall();
                                    } else {
                                        handler.postDelayed(this, 1500);
                                    }
                                }
                            });

                        } else {
                            hangupCall();
                        }

                    }
                } else {
                    hangupCall();
                }
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CallScreenActivity.this, DialerActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Handler handler = new Handler();
        Runnable AgentData = new Runnable() {
            public void run() {
                if (callInfo.getEvent().equals("ANSWERED")) {
                    getLiveAgents();
                }
                handler.postDelayed(this, 8000);
            }
        };
        handler.post(AgentData);

        iv_call_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
                if (callInfo.getEvent().equals("ANSWERED")) {
                    transferDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "Transfer Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_conference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
                if (callInfo.getEvent().equals("ANSWERED")) {
                    if (contactAdded) {
                        String[] myString1 = {userInfo.getTenantId(), callInfo.getPhoneNumber(), "station", callInfo.getUniqueId(), "actionid", "live_channel", "customer_channel", "conference_no"};
                        conferencePeople.add(myString1);
                        contactAdded = false;
                    }
                    conferenceDialogue();
                } else {
                    Toast.makeText(getApplicationContext(), "conference Denied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_create_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customerInfo.getLeadId().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Meeting Creation Denied", Toast.LENGTH_SHORT).show();
                } else {
                    meetingDialog();
                }
            }
        });

        iv_add_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesDialog();
            }
        });

        iv_lead_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("crmUrl", customerInfo.getCrmUrl());
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(customerInfo.getCrmUrl()));
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        iv_create_lead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("url", customerInfo.getAddLeadUrl());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(customerInfo.getAddLeadUrl()));
                try {
                    startActivity(browserIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        answerCallSip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sipManager.acceptCall();
                answerCallSip.setVisibility(View.GONE);
                if (sipRegistered) {
                    holdCallSip.setVisibility(View.VISIBLE);
                    muteCallSip.setVisibility(View.VISIBLE);
                }
            }
        });

        hangCallSip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sipManager.hangCall();
            }
        });

        holdCallSip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sipManager.isRegistered()) {
                    boolean isHold = sipManager.holdCall();
                    if (isHold) {
                        iv_hold.setVisibility(View.VISIBLE);
                    } else {
                        iv_hold.setVisibility(View.INVISIBLE);

                    }
                }
            }
        });

        muteCallSip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sipManager.isRegistered()) {
                    boolean isMuted = sipManager.muteCall();
                    if (isMuted) {
                        iv_mute.setVisibility(View.VISIBLE);
                    } else {
                        iv_mute.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Log.e("mute", "muted");
                    AudioManager audioManager =
                            (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager != null) {
                        audioManager.setMicrophoneMute(true);
                    }
                }
            }
        });

        if (sharedpreferences.getBoolean("AutoAnswer", false)) {
            sipManager.acceptCall();
            answerCallSip.performClick();
        }
    }

    private void initializeVariables() {
        iv_call_transfer = toolbar.findViewById(R.id.iv_call_transfer);
        iv_conference = toolbar.findViewById(R.id.iv_conference);
        iv_add_notes = toolbar.findViewById(R.id.iv_add_notes);
        iv_create_meeting = toolbar.findViewById(R.id.iv_create_meeting);
        iv_lead_info = toolbar.findViewById(R.id.iv_lead_info);
        iv_back = toolbar.findViewById(R.id.iv_back);
        iv_create_lead = toolbar.findViewById(R.id.iv_create_lead);
        iv_create_lead = toolbar.findViewById(R.id.iv_create_lead);
        answerCallSip = findViewById(R.id.bt_answer_call_sip);
        iv_mute = findViewById(R.id.iv_mute);
        iv_hold = findViewById(R.id.iv_hold);
        answerCallSip = findViewById(R.id.bt_answer_call_sip);
        holdCallSip = findViewById(R.id.bt_hold_call_sip);
        muteCallSip = findViewById(R.id.bt_mute_call_sip);
        hangCallSip = findViewById(R.id.bt_hang_call_sip);
        wv_asterdialer = findViewById(R.id.wv_asterdialer);

        applicationInsight = new ApplicationInsight(getApplicationContext(), getApplication());
        applicationInsight.trackPageView("CallScreenActivity", "");
        applicationInsight.trackEvent("CallPopup", "Initiated");
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

    private void getCrmUrl(String module, String leadId) {

        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<CrmUrlResponse> call = services.getCrmUrl(userInfo.getToken(), userInfo.getCrmDomain(), module, leadId);

        call.enqueue(new Callback<CrmUrlResponse>() {
            @Override
            public void onResponse(@NonNull Call<CrmUrlResponse> call, @NonNull Response<CrmUrlResponse> response) {
                if (response.code() == 200) {

                    customerInfo.setCrmUrl(response.body().getUrl());
                    customerInfo.setAddLeadUrl(response.body().getAddUrls().getLead());

                    SharedPrefHelper.putCustomerInfo(sharedpreferences,customerInfo);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CrmUrlResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void meetingDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_create_meeting, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CallScreenActivity.this);
        builder.setView(view);

        meetingDialog = builder.create();
        meetingDialog.setCanceledOnTouchOutside(false);

        ImageView iv_close = view.findViewById(R.id.iv_close);
        EditText et_meeting_title = view.findViewById(R.id.et_meeting_title);
        EditText et_date_picker = view.findViewById(R.id.et_date_picker);
        EditText et_time_picker = view.findViewById(R.id.et_time_picker);
        Button bt_create_meeting = view.findViewById(R.id.bt_create_meeting);

        et_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year_x = c.get(Calendar.YEAR);
                int month_x = c.get(Calendar.MONTH);
                int day_x = c.get(Calendar.DAY_OF_MONTH);
                c.add(Calendar.DAY_OF_YEAR, 1);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CallScreenActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month + 1;
                        String monthString = String.valueOf(month);
                        if (monthString.length() == 1) {
                            monthString = "0" + monthString;
                        }
                        String dayString = String.valueOf(dayOfMonth);
                        if (dayString.length() == 1) {
                            dayString = "0" + dayString;
                        }
                        et_date_picker.setText(year + "-" + monthString + "-" + dayString);
                    }
                }, year_x, month_x, day_x);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        et_time_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(CallScreenActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String hourString = String.valueOf(selectedHour);
                        if (hourString.length() == 1) {
                            hourString = "0" + hourString;
                        }
                        String minString = String.valueOf(selectedMinute);
                        if (minString.length() == 1) {
                            minString = "0" + minString;
                        }
                        et_time_picker.setText(hourString + ":" + minString + ":00");
                    }
                }, hour, minute, true);
                mTimePicker.show();
            }
        });

        bt_create_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = true;
                String meeting_title = et_meeting_title.getText().toString();
                String date = et_date_picker.getText().toString();
                String time = et_time_picker.getText().toString();
                String meeting_date = "";
                if (meeting_title.isEmpty()) {
                    check = false;
                    et_meeting_title.requestFocus();
                    et_meeting_title.setError("Title is required");
                }
                if (et_date_picker.getText().toString().isEmpty() || et_time_picker.getText().toString().isEmpty()) {
                    check = false;
                    Toast.makeText(CallScreenActivity.this, "Date & Time is required", Toast.LENGTH_SHORT).show();
                } else {
                    meeting_date = date + " " + time;
                }

                if (check) {
                    createMeeting(meeting_title, meeting_date);
                    if (meetingDialog.isShowing()) {
                        meetingDialog.dismiss();
                    }
                }
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meetingDialog.dismiss();
            }
        });

        meetingDialog.show();
    }

    private void notesDialog() {
        try {
            View view = getLayoutInflater().inflate(R.layout.layout_call_description, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(CallScreenActivity.this);
            builder.setView(view);

            descriptionDialog = builder.create();
            descriptionDialog.setCanceledOnTouchOutside(false);

            ImageView iv_close = view.findViewById(R.id.iv_close);
            EditText et_call_comments = view.findViewById(R.id.et_call_comments);
            Button bt_save_description = view.findViewById(R.id.bt_save_description);

            if (!customerInfo.getDescription().isEmpty()) {
                et_call_comments.setText(customerInfo.getDescription());
            }
            iv_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    descriptionDialog.dismiss();
                }
            });

            bt_save_description.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customerInfo.setDescription(et_call_comments.getText().toString());
                    SharedPrefHelper.putCustomerInfo(sharedpreferences,customerInfo);

                    descriptionDialog.dismiss();
                }
            });
            descriptionDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void transferDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_call_transfer, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CallScreenActivity.this);
        builder.setView(view);

        transferDialog = builder.create();
        transferDialog.setCanceledOnTouchOutside(false);

        ImageView iv_close = view.findViewById(R.id.iv_close);
        EditText et_transfer_number = view.findViewById(R.id.et_transfer_number);
        Button bt_transfer = view.findViewById(R.id.bt_transfer);
        Spinner sp_did_number = view.findViewById(R.id.sp_did_number);
        Button bt_did_transfer = view.findViewById(R.id.bt_connect);
        LinearLayout ll_other_transfer = view.findViewById(R.id.ll_other_transfer);
        LinearLayout ll_did_transfer = view.findViewById(R.id.ll_did_transfer);
        LinearLayout ll_agent_transfer = view.findViewById(R.id.ll_agent_transfer);
        Spinner sp_agents = view.findViewById(R.id.sp_agents);
        Button bt_agent_connect = view.findViewById(R.id.bt_agent_connect);
        LinearLayout ll_queue_transfer = view.findViewById(R.id.ll_queue_transfer);
        Spinner sp_queues = view.findViewById(R.id.sp_queues);
        Button bt_queue_connect = view.findViewById(R.id.bt_queue_connect);

        if (!permissionInfo.getDidTransfer()) {
            ll_did_transfer.setVisibility(View.GONE);
        }
        if (!permissionInfo.getOtherTransfer()) {
            ll_other_transfer.setVisibility(View.GONE);
        }
        if (!permissionInfo.getAgentTransfer()) {
            ll_agent_transfer.setVisibility(View.GONE);
        }
        if (!permissionInfo.getQueueTransfer()) {
            ll_queue_transfer.setVisibility(View.GONE);
        }

        ArrayAdapter<String> didAdapter = new ArrayAdapter<String>(CallScreenActivity.this, android.R.layout.simple_spinner_dropdown_item, pbxInfo.getDidArray());
        sp_did_number.setAdapter(didAdapter);

        agents = sqliteDB.get_liveAgent_values("LIVEAGENTS", "Select a Agent");
        Log.e("data", String.valueOf(agents));
        Log.e("adapterData", String.valueOf(agents));
        ArrayAdapter<String> agentAdapter = new ArrayAdapter<String>(CallScreenActivity.this, android.R.layout.simple_spinner_dropdown_item, agents);
        sp_agents.setAdapter(agentAdapter);

        try {
            queueArray.clear();
            queueMap.clear();
            queueArray.add("Select Queue");
            for (PbxInfo.Queue queue : pbxInfo.getQueueArray()) {
                queueArray.add(queue.getName());
                queueMap.put(queue.getName(), queue.getCode().toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        ArrayAdapter<String> queueAdapter = new ArrayAdapter<String>(CallScreenActivity.this, android.R.layout.simple_spinner_dropdown_item, queueArray);
        sp_queues.setAdapter(queueAdapter);

        bt_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transfer_number = et_transfer_number.getText().toString();
                if (transfer_number.isEmpty()) {
                    et_transfer_number.requestFocus();
                    et_transfer_number.setError("Number required");
                } else {
                    transferCall();
                    if (transferDialog.isShowing()) {
                        transferDialog.dismiss();
                    }
                }
            }
        });

        bt_did_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = sp_did_number.getSelectedItemPosition();
                if (position == 0) {
                    Toast.makeText(CallScreenActivity.this, "Select Number", Toast.LENGTH_SHORT).show();
                } else {
                    transfer_number = sp_did_number.getSelectedItem().toString();
                    transferCall();
                    if (transferDialog.isShowing()) {
                        transferDialog.dismiss();
                    }
                }
            }
        });

        bt_agent_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked", "clicked");
                int position = sp_agents.getSelectedItemPosition();
                if (position == 0) {
                    Toast.makeText(CallScreenActivity.this, "Select Number", Toast.LENGTH_SHORT).show();
                } else {
                    String Agent = sp_agents.getSelectedItem().toString();
                    String[] data = sqliteDB.get_station("LIVEAGENTS", Agent);
                    Log.e("data", Arrays.toString(data));
                    if (data[1].equals("READY")) {
                        transfer("ATTENDED","agent",data[0]);
                        if (transferDialog.isShowing()) {
                            transferDialog.dismiss();
                        }
                    } else {
                        Toast.makeText(CallScreenActivity.this, "Agent not available now", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        bt_queue_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked", "clicked");
                int position = sp_queues.getSelectedItemPosition();
                if (position == 0) {
                    Toast.makeText(CallScreenActivity.this, "Select Number", Toast.LENGTH_SHORT).show();
                } else {
                    String Queue = sp_queues.getSelectedItem().toString();
                    transfer("BLIND","queue",queueMap.get(Queue));
                    if (transferDialog.isShowing()) {
                        transferDialog.dismiss();
                    }
                }
            }
        });

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transferDialog.dismiss();
            }

        });

        transferDialog.show();
    }

    private void conferenceDialogue() {
        callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
        View view = getLayoutInflater().inflate(R.layout.layout_conference, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CallScreenActivity.this);
        builder.setView(view);

        conferenceDialog = builder.create();
        conferenceDialog.setCanceledOnTouchOutside(false);

        String[][] liveAgentArray = new String[20][5];
        RecyclerView rv_conference = view.findViewById(R.id.rv_conference);
        ImageView iv_close = view.findViewById(R.id.iv_close);
        Spinner sp_agents = view.findViewById(R.id.sp_agents);
        Button bt_conference_connect = view.findViewById(R.id.bt_conference_connect);
        sp_agents.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                agents = sqliteDB.get_liveAgent_values("LIVEAGENTS", "Select a Agent");
                Log.e("data", String.valueOf(agents));
                Log.e("adapterData", String.valueOf(agents));
                ArrayAdapter<String> agentAdapter = new ArrayAdapter<String>(CallScreenActivity.this, android.R.layout.simple_spinner_dropdown_item, agents);
                sp_agents.setAdapter(agentAdapter);
            }
        }, 1500);

        bt_conference_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionInfo.getViewEnableConference()) {
                    int index = sp_agents.getSelectedItemPosition();
                    String Agent = sp_agents.getSelectedItem().toString();
                    String[] data = sqliteDB.get_station("LIVEAGENTS", Agent);
                    if (index != 0) {
                        JSONObject conferenceObject = new JSONObject();
                        try {
                            conferenceObject.put("action", "conference");
                            conferenceObject.put("tenant_id", userInfo.getTenantId());
                            conferenceObject.put("add_number", data[0]);
                            conferenceObject.put("station", userInfo.getExtension());
                            conferenceObject.put("uniqueid", callInfo.getUniqueId());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        boolean alreadyInConference = participants.contains(data[0]);
                        Log.e("Check", String.valueOf(alreadyInConference));
                        if (mSocket.connected() && data[1].equals("READY") && !alreadyInConference) {
                            mSocket.emit("conference", conferenceObject);
                            Log.e("event TX","conference,"+ conferenceObject.toString());
                            participants.add(data[0]);
                            Log.e("conference participants", participants.toString());

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("adapter", String.valueOf(conferencePeople));
                                    conferenceAdapter = new ConferenceAdapter(conferencePeople, R.layout.layout_conference_view, getApplicationContext());
                                    rv_conference.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    rv_conference.setAdapter(conferenceAdapter);
                                }
                            }, 2500);
                        } else if (alreadyInConference) {
                            Toast.makeText(CallScreenActivity.this, "This participant already in conference", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CallScreenActivity.this, "Agent is offline or not ready", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(CallScreenActivity.this, "Please Select One Agent", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CallScreenActivity.this, "Unable to create conference", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (conferencePeople.size() > 0) {
            Log.e("adapter", String.valueOf(conferencePeople));
            conferenceAdapter = new ConferenceAdapter(conferencePeople, R.layout.layout_conference_view, getApplicationContext());
            rv_conference.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            rv_conference.setAdapter(conferenceAdapter);
        }

        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conferenceDialog.dismiss();
            }
        });

        conferenceDialog.show();
    }

    private void createMeeting(String meeting_title, String meeting_date) {
        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<CreateMeetingResponse> call = services.createMeeting(userInfo.getToken(), userInfo.getCrmDomain(), new CreateMeetingRequest(meeting_title, meeting_date, userInfo.getUserId(), userInfo.getUsername(), callInfo.getPhoneNumber(), customerInfo.getLeadId(), customerInfo.getModule()));
        call.enqueue(new Callback<CreateMeetingResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreateMeetingResponse> call, @NonNull Response<CreateMeetingResponse> response) {
                if (response.code() == 201) {
                    Toast.makeText(CallScreenActivity.this, "Meeting Created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CallScreenActivity.this, "Meeting Creation Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreateMeetingResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private void getSubFilter() {
        pb_loader.setVisibility(View.VISIBLE);
        status_filter.clear();

        String status = sqliteDB.get_id_by_name("MAINFILTER", "Lead Status");
        Log.e("status", status);

        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);

        Call<SubFilterResponse> call = services.getSubFilter(userInfo.getToken(), userInfo.getCrmDomain(), status);
        call.enqueue(new Callback<SubFilterResponse>() {
            @Override
            public void onResponse(@NonNull Call<SubFilterResponse> call, @NonNull Response<SubFilterResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        pb_loader.setVisibility(View.GONE);
                        List<SubFilterResponse.Datum> group = new ArrayList<>();
                        if (response.isSuccessful()) {
                            assert response.body() != null;
                            group = response.body().getData();
                            status_filter.add("--select--");
                            for (int i = 0; i < group.size(); i++) {
                                status_filter.add(group.get(i).getName());
                                statusAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, status_filter);
                                sp_leadstatus.setAdapter(statusAdapter);
                            }
                            if (group.size() > 0) {
                                sqliteDB.delete_rows("SUBFILTER");
                            }
                            for (int i = 0; i < group.size(); i++) {
                                sqliteDB.insert_values("SUBFILTER", group.get(i).getName(), group.get(i).getValue());
                            }
                        }
                    } else {
                        pb_loader.setVisibility(View.GONE);
                        status_filter.add("no data");
                        statusAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, status_filter);
                        sp_leadstatus.setAdapter(statusAdapter);
                    }
                } else {
                    pb_loader.setVisibility(View.GONE);
                    status_filter.add("no data");
                    statusAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, status_filter);
                    sp_leadstatus.setAdapter(statusAdapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubFilterResponse> call, @NonNull Throwable t) {
                pb_loader.setVisibility(View.GONE);
                sp_leadstatus.setEnabled(false);
                applicationInsight.trackEvent("SubFilter Exception", t.toString());
            }
        });
    }

    public void getLiveAgents() {
        Log.e("dialerdomain", apiInfo.getDialerDomain().toString());
        Services services = RetrofitAsterClient.getClient(apiInfo.getDialerDomain()).create(Services.class);
        Call<ConferenceResponse> call = services.getOtherAgents(userInfo.getToken(), new ConferenceRequest(userInfo.getEmail(), ""));
        call.enqueue(new Callback<ConferenceResponse>() {
            @Override
            public void onResponse(@NonNull Call<ConferenceResponse> call, @NonNull Response<ConferenceResponse> response) {
                try {
                    List<ConferenceResponse.Datum> group = new ArrayList<>();
                    if (response.code() == 200) {
                        assert response.body() != null;
                        group = response.body().getData();

                        if (group.size() > 0) {
                            sqliteDB.delete_rows("LIVEAGENTS");
                        }

                        System.out.println("size" + group.size());
                        for (int i = 0; i < group.size(); i++) {
                            Log.e("conference Response", i + " " + group.get(i).getName().toString() + " " + group.get(i).getUser().toString() + " " + group.get(i).getStation() + " " + group.get(i).getQueue().toString() + " " + group.get(i).getStatus().toString());

                            sqliteDB.insert_data("LIVEAGENTS", group.get(i).getName().toString(), group.get(i).getUser().toString(), group.get(i).getStation().toString(), group.get(i).getQueue().toString(), group.get(i).getStatus().toString());
                        }
                    } else if (response.code() == 403) {
                        Toast.makeText(CallScreenActivity.this, "UnAuthorized", Toast.LENGTH_SHORT).show();
                    } else if (response.code() == 204) {
                        Toast.makeText(CallScreenActivity.this, "No Agents available", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("failure", String.valueOf(response.code()));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ConferenceResponse> call, Throwable t) {
                Log.e("throw", t.toString());
            }
        });
    }

    private void getTimeLine() {
        rv_time_line.setVisibility(View.INVISIBLE);
        pb_loader_1.setVisibility(View.VISIBLE);
        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<TimelineResponse> call = null;
        if (userInfo.getCrmDomain().equals("asterdialer")) {
            call = services.getAsterTimeline(userInfo.getToken(), userInfo.getCrmDomain(), new TimelineRequest(callInfo.getPhoneNumber(), customerInfo.getLeadId()));
        } else {
            call = services.getTimeline(userInfo.getToken(), userInfo.getCrmDomain(), customerInfo.getLeadId());
        }
        call.enqueue(new Callback<TimelineResponse>() {
            @Override
            public void onResponse(@NonNull Call<TimelineResponse> call, @NonNull Response<TimelineResponse> response) {
                List<TimelineResponse.Datum> list = new ArrayList<>();
                if (response.code() == 200) {
                    rv_time_line.setVisibility(View.VISIBLE);
                    pb_loader_1.setVisibility(View.GONE);
                    list = response.body().getData();
                    timeLineAdapter = new TimeLineAdapter(list, R.layout.layout_timeline_view, CallScreenActivity.this);
                    rv_time_line.setAdapter(timeLineAdapter);
                } else {
                    rv_time_line.setVisibility(View.GONE);
                    pb_loader_1.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TimelineResponse> call, @NonNull Throwable t) {
                rv_time_line.setVisibility(View.GONE);
                pb_loader_1.setVisibility(View.GONE);
                tv_no_data.setVisibility(View.VISIBLE);
            }
        });
    }


    private void transfer(String xfer_type,String xfer_to, String xfer_dest) {
        JSONObject event = new JSONObject();
        try {
            event.put("action", "Transfer");
            event.put("uniqueid", callInfo.getUniqueId());
            event.put("caller_channel", callInfo.getCallerChannel());
            event.put("agent_channel", callInfo.getAgentChannel());
            event.put("xfer_type", xfer_type);
            event.put("xfer_to", xfer_to);
            event.put("xfer_dest", xfer_dest);
            event.put("agent", userInfo.getEmail());
            event.put("phone_number", callInfo.getPhoneNumber());
            event.put("station", userInfo.getExtension());
            event.put("tenant_id", userInfo.getTenantId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket.connected()) {
            mSocket.emit("transfer", event);
            Log.e("event TX","transfer,"+ event.toString());
        } else {
            Toast.makeText(getApplicationContext(), "Disconnected, Try after sometime", Toast.LENGTH_SHORT).show();
        }

    }


    private void transferCall() {
        JSONObject transferCall = new JSONObject();
        try {
            transferCall.put("agent", userInfo.getEmail());
            transferCall.put("tenant_id", userInfo.getTenantId());
            transferCall.put("action", "Transfer");
            transferCall.put("station", userInfo.getExtension());
            transferCall.put("extension", userInfo.getExtension());
            transferCall.put("phone_number", callInfo.getPhoneNumber());
            transferCall.put("uniqueid", callInfo.getUniqueId());
            transferCall.put("caller_channel", callInfo.getCallerChannel());
            transferCall.put("agent_channel", callInfo.getAgentChannel());
            transferCall.put("xfer_type", "ATTENDED");
            transferCall.put("xfer_to", "others");
            transferCall.put("xfer_dest", transfer_number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mSocket.connected()) {
            mSocket.emit("transfer", transferCall);
            Log.e("event TX","transfer,"+ transferCall.toString());
        } else {
            Toast.makeText(getApplicationContext(), "Disconnected, Try after sometime", Toast.LENGTH_SHORT).show();
        }
    }

    private void hangupCall() {
        mSocket = app.getSocket();
        pb_loader.setVisibility(View.VISIBLE);
        callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
        Log.e("callInfo",callInfo.toString());
        if (!callInfo.getAnswerTime().isEmpty()) {
            try {

                sharedpreferences.edit().putBoolean("customerInfoApiCall", false).apply();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date date1 = output.parse(callInfo.getAnswerTime());
                Date date2 = null;

                if (callInfo.getEndTime().isEmpty()) {
                    callInfo.setEndTime(Calendar.getInstance().getTime().toString());
                    date2 = output.parse(output.format(Calendar.getInstance().getTime()));
                } else {
                    date2 = output.parse(callInfo.getEndTime());
                }

                Log.i("current_time", String.valueOf(date2));
                long difference = date2.getTime() - date1.getTime();
                long minutes = (difference / 1000) / 60;
                long seconds = (difference / 1000) % 60;
                String min = "", sec = "";
                if (minutes < 10) {
                    min = "0" + minutes;
                } else {
                    min = String.valueOf(minutes);
                }
                if (seconds < 10) {
                    sec = "0" + seconds;
                } else {
                    sec = String.valueOf(seconds);
                }
                duration = min + ":" + sec;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            duration = "00:00";
            callInfo.setEndTime(Calendar.getInstance().getTime().toString());
        }
        @SuppressLint("SimpleDateFormat") SimpleDateFormat calldate_output = new SimpleDateFormat("yyyy-MM-dd");

        calldate = calldate_output.format(Calendar.getInstance().getTime());

        if (customerInfo.getModule().equals("Leads") || customerInfo.getModule().equals("lead") || customerInfo.getModule().equals("leads")) {
            dispo_status = sqliteDB.getCallStatus("LEADCALLSTATUS", sp_dispo_status.getSelectedItem().toString());
        } else {
            dispo_status = sqliteDB.getCallStatus("CONTACTCALLSTATUS", sp_dispo_status.getSelectedItem().toString());
        }
        if (callInfo.getCallType().equals("OUTBOUND")) {
            subject = "Call from " + userInfo.getExtension() + " to " + callInfo.getPhoneNumber();
        } else if (callInfo.getCallType().equals("INBOUND")) {
            subject = "Call from " + callInfo.getPhoneNumber() + " to " + userInfo.getExtension();
        }

        if (permissionInfo.getCrmApi() && !permissionInfo.getNoDispo()) {
            String CAMPAIGN = "";
            if (userInfo.getCrmDomain().equals("asterdialer") && !sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN).equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
                CAMPAIGN = sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN);
                Log.e("campaign", CAMPAIGN);
            }

            Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
            Call<CallActivityResponse> call = null;

            if (callInfo.getStartTime().equals("")) {
                Date currentTime = Calendar.getInstance().getTime();
                callInfo.setStartTime(sharedpreferences.getString("callStartTime", currentTime.toString()));
            }

            String call_type = callInfo.getCallType();
            if (userInfo.getCrmDomain().equals("asterdialer")) {
                call = services.createCallActivity(userInfo.getToken(), userInfo.getCrmDomain(), new CallActivityRequest(subject, call_type, customerInfo.getModule(), customerInfo.getLeadId(), userInfo.getUserId(), userInfo.getUsername(), callInfo.getStartTime(), callInfo.getPhoneNumber(), customerInfo.getDescription(), duration, dispo_status, callInfo.getEndTime(), callInfo.getUniqueId(), CAMPAIGN));
            } else {
                call = services.createCallActivity(userInfo.getToken(), userInfo.getCrmDomain(), new CallActivityRequest(subject, callInfo.getCallType(), customerInfo.getModule(), customerInfo.getLeadId(), userInfo.getUserId(), userInfo.getUsername(), callInfo.getStartTime(), callInfo.getPhoneNumber(), customerInfo.getDescription(), duration, dispo_status, callInfo.getEndTime(), callInfo.getUniqueId()));
            }


            call.enqueue(new Callback<CallActivityResponse>() {
                @Override
                public void onResponse(@NonNull Call<CallActivityResponse> call, @NonNull Response<CallActivityResponse> response) {
                    int code = response.code();

                    if (code == 201) {
                        JSONObject dispo = new JSONObject();
                        try {
                            dispo.put("name", dispo_status_);
                            dispo.put("value", dispo_status_);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Log.e("hanged up data", dispo.toString());
                        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
                        callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
                        JSONObject hangupCall = new JSONObject();
                        try {
                            hangupCall.put("action", "Hangup");
                            hangupCall.put("agent", userInfo.getEmail());
                            hangupCall.put("agent_name", userInfo.getUsername());
                            hangupCall.put("tenant_id", userInfo.getTenantId());
                            hangupCall.put("extension", userInfo.getExtension());
                            hangupCall.put("station", userInfo.getExtension());
                            hangupCall.put("phone_number", callInfo.getPhoneNumber());
                            hangupCall.put("status", callInfo.getEvent());
                            hangupCall.put("uniqueid", callInfo.getUniqueId());
                            String campaign = sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN);

                            if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
                                hangupCall.put("campaign", campaign);
                                hangupCall.put("leadId", customerInfo.getLeadId());
                            }
                            hangupCall.put("caller_channel", callInfo.getCallerChannel());
                            hangupCall.put("agent_channel", callInfo.getAgentChannel());
                            hangupCall.put("dispo_status", dispo);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e("hangup_", hangupCall.toString());

                        boolean sipRegistered = sharedpreferences.getBoolean(SharedPrefConstants.SIP_REGISTERED, false);
                        if (sipRegistered) {
                            sipManager.hangCall();
                        }
                        if (mSocket.connected()) {
                            mSocket.emit("hangup", hangupCall);
                            Log.e("event TX","hangup,"+ hangupCall.toString());
                        }
                        try {

                            userInfo.setStatus("Available");
                            callInfo = new CallInfo();
                            customerInfo = new CustomerInfo();

                                if (autoDialInfo.getStatus().equals("start")) {
                                com.nidoos.doocti.utils.Call c2c = new com.nidoos.doocti.utils.Call(getApplicationContext(), getApplication(), mSocket);
                                c2c.startAutoDial();
                            } else {
                                JSONObject queueResume = new JSONObject();
                                try {
                                    queueResume.put("action", "QueuePause");
                                    queueResume.put("agent", userInfo.getEmail());
                                    queueResume.put("station", userInfo.getExtension());
                                    queueResume.put("paused", false);
                                    queueResume.put("tenant_id", userInfo.getTenantId());
                                    queueResume.put("status", "DISPO");
                                    queueResume.put("call", false);
                                    String campaign = sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN);
                                    assert campaign != null;
                                    if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
                                        queueResume.put("campaign", campaign);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                if (mSocket.connected()) {
                                    Log.e("event TX","dispo-pause,"+ queueResume.toString());
                                    mSocket.emit("dispo-pause", queueResume);
                                    applicationInsight.trackEvent("Dispo Pause", queueResume.toString());

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        applicationInsight.trackEvent("Hangup", hangupCall.toString());
                        applicationInsight.trackEvent("CallPopup", "Closed");


                        Handler hangup = new Handler();
                        Runnable hangup_response = new Runnable() {
                            @Override
                            public void run() {
                                boolean hangupResponse = sharedpreferences.getBoolean(SharedPrefConstants.HANGUP_RESPONSE, false);
                                Log.e("hangup-response", String.valueOf(hangupResponse));
//                                hangupResponse = true;
                                if (hangupResponse) {
                                    sharedpreferences.edit().putString(CALL_INFO, new Gson().toJson(callInfo)).apply();
                                    userInfo.setStatus("Available");

                                    sharedpreferences.edit()
                                            .putString(CALL_INFO, new Gson().toJson(callInfo))
                                            .putString(CUSTOMER_INFO, new Gson().toJson(customerInfo))
                                            .putString(USER_INFO, new Gson().toJson(userInfo))
                                            .apply();


                                    sharedpreferences.edit().putBoolean(SharedPrefConstants.HANGUP_RESPONSE, false).apply();

                                    goToDialerActivity();


                                } else {
                                    hangup.postDelayed(this, 900);
                                }
                            }
                        };
                        hangup.postDelayed(hangup_response, 300);
                    } else if (code == 401) {
                        Toast.makeText(getApplicationContext(), "Dispo Not Updated, Unauthorized Access", Toast.LENGTH_SHORT).show();
                    } else {
                        pb_loader.setVisibility(View.GONE);
                        rl_call_screen.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), "Dispo Not Updated, Try again...", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CallActivityResponse> call, @NonNull Throwable t) {
                    pb_loader.setVisibility(View.GONE);
                    rl_call_screen.setVisibility(View.VISIBLE);
                    applicationInsight.trackEvent("CallActivity Exception", t.toString());
                    Toast.makeText(getApplicationContext(), customerInfo.getDescription() + " " + "Check your Internet Connection...", Toast.LENGTH_SHORT).show();
                    Log.e("description", customerInfo.getDescription());
                }
            });

        } else if (!permissionInfo.getCrmApi() && permissionInfo.getNoDispo()) {
            JSONObject dispo = new JSONObject();
            try {
                dispo.put("name", "permissionInfo.getNoDispo()");
                dispo.put("value", "permissionInfo.getNoDispo()");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject hangupCall = new JSONObject();
            try {
                hangupCall.put("action", "Hangup");
                hangupCall.put("agent", userInfo.getEmail());
                hangupCall.put("agent_name", userInfo.getUsername());
                hangupCall.put("tenant_id", userInfo.getTenantId());
                hangupCall.put("extension", userInfo.getExtension());
                hangupCall.put("station", userInfo.getExtension());
                hangupCall.put("phone_number", callInfo.getPhoneNumber());
                hangupCall.put("status", callInfo.getEvent());
                hangupCall.put("uniqueid", callInfo.getUniqueId());
                hangupCall.put("caller_channel", callInfo.getCallerChannel());
                hangupCall.put("agent_channel", callInfo.getAgentChannel());
                hangupCall.put("dispo_status", dispo);
                String campaign = sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN);
                if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
                    hangupCall.put("campaign", campaign);
                    hangupCall.put("leadId", customerInfo.getLeadId());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("hangup_", hangupCall.toString());

            boolean sipRegistered = sharedpreferences.getBoolean(SharedPrefConstants.SIP_REGISTERED, false);
            if (sipRegistered) {
                sipManager.hangCall();
            }
            if (mSocket.connected()) {
                mSocket.emit("hangup", hangupCall);
                Log.e("event TX","hangup,"+ hangupCall.toString());
            }
            try {
                callInfo = new CallInfo();
                customerInfo = new CustomerInfo();
                userInfo.setStatus("Available");


                if (autoDialInfo.getStatus().equals("start")) {
                    com.nidoos.doocti.utils.Call c2c = new com.nidoos.doocti.utils.Call(getApplicationContext(), getApplication(), mSocket);
                    c2c.startAutoDial();
                } else {
                    JSONObject queueResume = new JSONObject();
                    try {
                        queueResume.put("action", "QueuePause");
                        queueResume.put("agent", userInfo.getEmail());
                        queueResume.put("station", userInfo.getExtension());
                        queueResume.put("paused", false);
                        queueResume.put("tenant_id", userInfo.getTenantId());
                        queueResume.put("status", "DISPO");
                        queueResume.put("call", false);
                        String campaign = sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN);
                        assert campaign != null;
                        if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
                            queueResume.put("campaign", campaign);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mSocket.connected()) {
                        mSocket.emit("dispo-pause", queueResume);
                        Log.e("event TX","dispo-pause,"+ queueResume.toString());
                        applicationInsight.trackEvent("Dispo Pause", queueResume.toString());

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            applicationInsight.trackEvent("Hangup", hangupCall.toString());
            applicationInsight.trackEvent("CallPopup", "Closed");

//
            Handler hangup = new Handler();
            Runnable hangup_response = new Runnable() {
                @Override
                public void run() {
                    boolean hangupResponse = sharedpreferences.getBoolean(SharedPrefConstants.HANGUP_RESPONSE, false);
                    if (hangupResponse) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(CALL_INFO, new Gson().toJson(callInfo));
                        editor.putString(CUSTOMER_INFO, new Gson().toJson(customerInfo));
                        editor.putString(USER_INFO, new Gson().toJson(userInfo));
                        editor.apply();

                        sharedpreferences.edit().putBoolean(SharedPrefConstants.HANGUP_RESPONSE, false).apply();
                        goToDialerActivity();
                    } else {
                        hangup.postDelayed(this, 900);
                    }
                }
            };
            hangup.postDelayed(hangup_response, 300);
//            }
        } else {
            JSONObject dispo = new JSONObject();
            try {
                dispo.put("name", "No Dispo");
                dispo.put("value", "No Dispo");
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject hangupCall = new JSONObject();
            try {
                hangupCall.put("action", "Hangup");
                hangupCall.put("agent", userInfo.getEmail());
                hangupCall.put("agent_name", userInfo.getUsername());
                hangupCall.put("tenant_id", userInfo.getTenantId());
                hangupCall.put("extension", userInfo.getExtension());
                hangupCall.put("station", userInfo.getExtension());
                hangupCall.put("phone_number", callInfo.getPhoneNumber());
                hangupCall.put("status", callInfo.getEvent());
                hangupCall.put("uniqueid", callInfo.getUniqueId());
                hangupCall.put("caller_channel", callInfo.getCallerChannel());
                hangupCall.put("agent_channel", callInfo.getAgentChannel());
                hangupCall.put("dispo_status", dispo);
                String campaign = sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN);
                assert campaign != null;
                if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
                    hangupCall.put("campaign", campaign);
                    hangupCall.put("customerInfoObj.getLeadId()", customerInfo.getLeadId());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("hangup_", hangupCall.toString());

            boolean sipRegistered = sharedpreferences.getBoolean(SharedPrefConstants.SIP_REGISTERED, false);
            if (sipRegistered) {
                sipManager.hangCall();
            }
            if (mSocket.connected()) {
                mSocket.emit("hangup", hangupCall);
                Log.e("event TX","hangup,"+ hangupCall.toString());
            }
            try {
                callInfo = new CallInfo();
                customerInfo = new CustomerInfo();
                userInfo.setStatus("Available");


                if (autoDialInfo.getStatus().equals("start")) {
                    com.nidoos.doocti.utils.Call c2c = new com.nidoos.doocti.utils.Call(getApplicationContext(), getApplication(), mSocket);
                    c2c.startAutoDial();
                } else {
                    JSONObject queueResume = new JSONObject();
                    try {
                        queueResume.put("action", "QueuePause");
                        queueResume.put("agent", userInfo.getEmail());
                        queueResume.put("station", userInfo.getExtension());
                        queueResume.put("paused", false);
                        queueResume.put("tenant_id", userInfo.getTenantId());
                        queueResume.put("status", "DISPO");
                        queueResume.put("call", false);
                        String campaign = sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN);
                        assert campaign != null;
                        if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
                            queueResume.put("campaign", campaign);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (mSocket.connected()) {
                        mSocket.emit("dispo-pause", queueResume);
                        Log.e("event TX","dispo-pause,"+ queueResume.toString());
                        applicationInsight.trackEvent("Dispo Pause", queueResume.toString());

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            applicationInsight.trackEvent("Hangup", hangupCall.toString());
            applicationInsight.trackEvent("CallPopup", "Closed");


            Handler hangup = new Handler();
            Runnable hangup_response = new Runnable() {
                @Override
                public void run() {
                    boolean hangupResponse = sharedpreferences.getBoolean(SharedPrefConstants.HANGUP_RESPONSE, false);
                    if (hangupResponse) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString(CALL_INFO, new Gson().toJson(callInfo));
                        editor.putString(CUSTOMER_INFO, new Gson().toJson(customerInfo));
                        editor.putString(USER_INFO, new Gson().toJson(userInfo));
                        editor.apply();

                        sharedpreferences.edit().putBoolean(SharedPrefConstants.HANGUP_RESPONSE, false).apply();
                        goToDialerActivity();
                    } else {
                        hangup.postDelayed(this, 900);
                    }
                }
            };
            hangup.postDelayed(hangup_response, 300);
        }


    }

    private void goToDialerActivity() {
        Intent intent = new Intent(CallScreenActivity.this, DialerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void triggerEvent(String event, String party_number, String tenant_id, String station, String conference_no) throws JSONException {
        JSONObject eventData = new JSONObject();
        try {
            eventData.put("action", "con-event");
            eventData.put("value", event);
            eventData.put("party_number", party_number);
            eventData.put("tenant_id", tenant_id);
            eventData.put("conference_no", conference_no);
            eventData.put("station", station);
            mSocket.emit("con-event", eventData);
            Log.e("event TX","con-event,"+ eventData.toString());
            Log.e("event", "emitted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.i("Back Pressed", "OK");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        }
    }
}
