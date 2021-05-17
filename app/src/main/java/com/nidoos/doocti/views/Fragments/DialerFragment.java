package com.nidoos.doocti.views.Fragments;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.socketio.client.Socket;
import com.nidoos.doocti.R;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.request.CallLogRequest;
import com.nidoos.doocti.response.CallLogResponse;
import com.nidoos.doocti.response.PlayRecordingResponse;
import com.nidoos.doocti.retrofit.RetrofitAsterClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.utils.ApplicationInsight;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.views.Adapter.CallLogAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DialerFragment extends Fragment {


    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    @BindView(R.id.rv_call_log)
    RecyclerView rv_call_log;

    @BindView(R.id.pb_loader)
    ProgressBar pb_loader;

    @BindView(R.id.sp_call_log_filter)
    Spinner sp_call_log_filter;


    private Doocti app;
    private Socket mSocket;
    private SharedPreferences sharedpreferences;

    private String call_log_filter = "All";

    private CallLogAdapter callLogAdapter;
    private AlertDialog alertDialog;

    private WebView wv_url;

    private ApplicationInsight applicationInsight;

    private UserInfo userInfo;
    private ApiInfo apiInfo;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dialer, container, false);
        ButterKnife.bind(this, rootView);

        app = (Doocti) getActivity().getApplicationContext();
        mSocket = app.getSocket();

        applicationInsight = new ApplicationInsight(app.getApplicationContext(), getActivity().getApplication());
        applicationInsight.trackPageView("CallLogFragment", "");

        sharedpreferences = app.getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
        apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);


        Log.i("userInfo", userInfo.toString());

        List<String> filerList = new ArrayList<>();
        filerList.addAll(Arrays.asList("All", "Inbound", "Outbound", "Missed"));

        ArrayAdapter filerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, filerList);
        sp_call_log_filter.setAdapter(filerAdapter);

        sp_call_log_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                call_log_filter = sp_call_log_filter.getSelectedItem().toString();
                getCallLogs();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        rv_call_log.setLayoutManager(new LinearLayoutManager(getActivity()));

        getCallLogs();

        return rootView;
    }

    private void getCallLogs() {
        rv_call_log.setVisibility(View.GONE);
        pb_loader.setVisibility(View.VISIBLE);
        int skip = 0, take = 50;

        Services services = RetrofitAsterClient.getClient(apiInfo.getDialerDomain()).create(Services.class);

        Call<CallLogResponse> call = services.callLog(userInfo.getToken(), new CallLogRequest(userInfo.getEmail(), call_log_filter, skip, take));
        call.enqueue(new Callback<CallLogResponse>() {
            @Override
            public void onResponse(@NonNull Call<CallLogResponse> call, @NonNull Response<CallLogResponse> response) {

                pb_loader.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    List<CallLogResponse.Datum> callLogList;
                    callLogList = response.body().getData();
                    if (callLogList.size() != 0) {
                        rv_call_log.setVisibility(View.VISIBLE);
                        tv_no_data.setVisibility(View.GONE);


                        try {
                            callLogAdapter = new CallLogAdapter(callLogList, R.layout.layout_view_call_log, getActivity(), new CallLogAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(CallLogResponse.Datum item) {

                                        getRecordingURL(item.getCalldate(), item.getUniqueid());
                                        applicationInsight.trackEvent("Play Recording", "Cli6cked");

                                }
                            });
                            rv_call_log.setAdapter(callLogAdapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        rv_call_log.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<CallLogResponse> call, @NonNull Throwable t) {
                pb_loader.setVisibility(View.GONE);
                applicationInsight.trackEvent("CallLog Exception", t.toString());
                Log.e("error", t.toString());
            }
        });
    }

    private void getRecordingURL(String date, String unique_id) {
        String call_date = date.substring(0, 10);
        Services services = RetrofitAsterClient.getClient(apiInfo.getDialerDomain()).create(Services.class);
        Call<PlayRecordingResponse> call = services.getRecordingURL(userInfo.getToken(), call_date, unique_id);
        call.enqueue(new Callback<PlayRecordingResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlayRecordingResponse> call, @NonNull Response<PlayRecordingResponse> response) {
                if (response.code() == 200) {
                    openRecordingDialog(response.body().getUrl());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PlayRecordingResponse> call, @NonNull Throwable t) {
                applicationInsight.trackEvent("Recording URL Exception", t.toString());
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void openRecordingDialog(String url) {
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_play_recording, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        wv_url = dialogView.findViewById(R.id.wv_url);
        wv_url.getSettings().setJavaScriptEnabled(true);
        wv_url.setWebViewClient(new WebViewClient());
        wv_url.loadUrl(url);

        ImageView iv_close = dialogView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wv_url.destroy();
                alertDialog.dismiss();
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                wv_url.destroy();
                Log.i("Dialog Cancelled", "OK");
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                wv_url.destroy();
                Log.i("Dialog Dismissed", "OK");
            }
        });


        try {
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            if (alertDialog != null && alertDialog.isShowing()) {
                wv_url.destroy();
                alertDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
