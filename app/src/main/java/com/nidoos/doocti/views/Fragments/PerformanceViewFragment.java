package com.nidoos.doocti.views.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.R;
import com.nidoos.doocti.response.PerformanceResponse;
import com.nidoos.doocti.retrofit.RetrofitAsterClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.utils.ApplicationInsight;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerformanceViewFragment extends Fragment {

    @BindView(R.id.pb_loader)
    ProgressBar pb_loader;

    @BindView(R.id.unanswered_calls)
    TextView unanswered_calls;

    @BindView(R.id.answered_calls)
    TextView answered_calls;

    @BindView(R.id.total_calls)
    TextView total_calls;

    private Doocti app;
    private Socket mSocket;

    private SharedPreferences sharedpreferences;
    private ApplicationInsight applicationInsight;

    private UserInfo userInfo;
    private ApiInfo apiInfo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_performace_view, container, false);
        ButterKnife.bind(this, rootView);

        app = (Doocti) getActivity().getApplicationContext();
        mSocket = app.getSocket();

        applicationInsight = new ApplicationInsight(app.getApplicationContext(), getActivity().getApplication());
        applicationInsight.trackPageView("Performance Fragment", "");

        sharedpreferences = app.getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userInfo = new Gson().fromJson(sharedpreferences.getString(SharedPrefConstants.USER_INFO,"userInfo Not found"), UserInfo.class);
        apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);

        getPerformance();

        return rootView;
    }

    private void getPerformance() {

        String startTime = sharedpreferences.getString("callStartTime",Calendar.getInstance().getTime().toString());
        String endTime = sharedpreferences.getString("callEndTime",Calendar.getInstance().getTime().toString());

        Services services = RetrofitAsterClient.getClient(apiInfo.getDialerDomain()).create(Services.class);
        Call<PerformanceResponse> call = services.getPerformance(userInfo.getToken(), startTime,endTime, userInfo.getEmail());

        call.enqueue(new Callback<PerformanceResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<PerformanceResponse> call, Response<PerformanceResponse> response) {
                if(response.isSuccessful()) {
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
                Log.e("error",t.getMessage());
            }
        });
    }



}
