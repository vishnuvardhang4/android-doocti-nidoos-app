package com.nidoos.doocti.views.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.R;
import com.nidoos.doocti.response.GetMeetingResponse;
import com.nidoos.doocti.response.PhoneNumberResponse;
import com.nidoos.doocti.retrofit.RetrofitApiClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.utils.ApplicationInsight;
import com.nidoos.doocti.views.Adapter.MeetingAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetingViewFragment extends Fragment {
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    @BindView(R.id.rv_contacts)
    RecyclerView rv_contacts;

    @BindView(R.id.pb_loader)
    ProgressBar pb_loader;

    private Socket mSocket;
    private Doocti app;
    private SharedPreferences sharedpreferences;

    private MeetingAdapter meetingAdapter;

    private ApplicationInsight applicationInsight;
    
    private UserInfo userInfo;
    private ApiInfo apiInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_meeting_view, container, false);
        ButterKnife.bind(this, rootView);


        app = (Doocti) getActivity().getApplicationContext();
        mSocket = app.getSocket();

        applicationInsight = new ApplicationInsight(app.getApplicationContext(), getActivity().getApplication());
        applicationInsight.trackPageView("Meeting Fragment", "");

        sharedpreferences = app.getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userInfo = new Gson().fromJson(sharedpreferences.getString(SharedPrefConstants.USER_INFO,"userInfo Not found"), UserInfo.class);
        apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);

        rv_contacts.setLayoutManager(new LinearLayoutManager(getActivity()));

        getMeetings();

        return rootView;
    }

    private void getMeetings() {
        rv_contacts.setVisibility(View.GONE);
        pb_loader.setVisibility(View.VISIBLE);

        int skip=0,take=50;

        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<GetMeetingResponse> call = services.getMeetings(userInfo.getToken(), userInfo.getCrmDomain(), userInfo.getUserId(), skip, take);
        call.enqueue(new Callback<GetMeetingResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetMeetingResponse> call, @NonNull Response<GetMeetingResponse> response) {
                pb_loader.setVisibility(View.GONE);
                if (response.code() == 200) {
                    List<GetMeetingResponse.Datum> list = new ArrayList<>();
                    list = response.body().getData();
                    if (list.size() != 0) {
                        rv_contacts.setVisibility(View.VISIBLE);
                        tv_no_data.setVisibility(View.GONE);
                        meetingAdapter = new MeetingAdapter(list, R.layout.layout_meeting_screen, getActivity(), new MeetingAdapter.OnMeetingItemClickListener() {
                            @Override
                            public void onItemClick(GetMeetingResponse.Datum item) {
                                if (item.getPhoneNumber() == null || item.getPhoneNumber().equals("")) {
                                    Toast.makeText(getActivity(), "Fetching Number", Toast.LENGTH_SHORT).show();
                                    getPhoneNumber(item.getLeadModule(), item.getLeadNumber());
                                } else {
                                    com.nidoos.doocti.utils.Call c2c = new com.nidoos.doocti.utils.Call(getActivity().getApplicationContext(), getActivity().getApplication(), mSocket);
                                    c2c.clickToCall(item.getLeadNumber(), "MANUAL","");
                                }
                            }
                        });
                        rv_contacts.setAdapter(meetingAdapter);
                    } else {
                        rv_contacts.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                } else {
                    rv_contacts.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(@NonNull Call<GetMeetingResponse> call, @NonNull Throwable t) {
                pb_loader.setVisibility(View.GONE);
                applicationInsight.trackEvent("GetMeetings Exception", t.toString());
                Log.e("error", t.toString());
            }
        });
    }

    private void getPhoneNumber(String leadModule, String leadNumber) {
        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<PhoneNumberResponse> call = services.getPhoneNumber(userInfo.getToken(), userInfo.getCrmDomain(), leadModule, leadNumber);
        call.enqueue(new Callback<PhoneNumberResponse>() {
            @Override
            public void onResponse(@NonNull Call<PhoneNumberResponse> call, @NonNull Response<PhoneNumberResponse> response) {
                if (response.code() == 200) {
                    com.nidoos.doocti.utils.Call c2c = new com.nidoos.doocti.utils.Call(getActivity().getApplicationContext(), getActivity().getApplication(), mSocket);
                    c2c.clickToCall(response.body().getLeadNumber(), "MANUAL","");
                }
            }

            @Override
            public void onFailure(@NonNull Call<PhoneNumberResponse> call, @NonNull Throwable t) {

            }
        });
    }
}
