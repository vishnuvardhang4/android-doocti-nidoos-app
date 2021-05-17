package com.nidoos.doocti.views.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.nidoos.doocti.info.AutoDialInfo;
import com.nidoos.doocti.info.CallInfo;
import com.nidoos.doocti.info.PermissionInfo;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.R;
import com.nidoos.doocti.request.FetchLeadsRequest;
import com.nidoos.doocti.response.FetchLeadsResponse;
import com.nidoos.doocti.response.SubFilterResponse;
import com.nidoos.doocti.retrofit.RetrofitApiClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.sqliteDB.SqliteDB;
import com.nidoos.doocti.utils.ApplicationInsight;
import com.nidoos.doocti.utils.MultiSelectionSpinner;
import com.nidoos.doocti.utils.RecyclerItemClickListener;
import com.nidoos.doocti.views.Adapter.ViewLeadAutoDial;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nidoos.doocti.constants.SharedPrefConstants.AUTO_DIAL_INFO;

public class AutoDialerFragment extends Fragment {


    @BindView(R.id.iv_search)
    ImageView iv_search;

    @BindView(R.id.iv_clear)
    ImageView iv_clear;

    @BindView(R.id.iv_start_auto_dial)
    ImageView iv_start_auto_dial;

    @BindView(R.id.iv_stop_auto_dial)
    ImageView iv_stop_auto_dial;

    @BindView(R.id.sp_main_filter)
    Spinner sp_main_filter;

    @BindView(R.id.sp_sub_filter)
    MultiSelectionSpinner sp_sub_filter;

    @BindView(R.id.sp_call_status)
    MultiSelectionSpinner sp_call_status;

    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    @BindView(R.id.rv_leads)
    RecyclerView rv_leads;

    @BindView(R.id.pb_loader)
    ProgressBar pb_loader;

    @BindView(R.id.bt_skip_leads)
    Button bt_skip_leads;

    private Doocti app;
    private Socket mSocket;

    private SharedPreferences sharedpreferences;
    private String campaign;
    private boolean isMultiSelect = false;
    private SqliteDB sqliteDB;


    private List<String> mainFilterList;
    private List<String> subFilterList;
    private List<String> callStatusList;
    private List<FetchLeadsResponse.Datum> leadViewList;
    private List<FetchLeadsResponse.Datum> multiSelectList;


    private ViewLeadAutoDial viewLeadAutoDial;
    private ApplicationInsight applicationInsight;

    private UserInfo userInfo;
    private PermissionInfo permissionInfo;
    private ApiInfo apiInfo;
    private CallInfo callInfo;
    private AutoDialInfo autoDialInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_auto_dialer, container, false);
        ButterKnife.bind(this, rootView);

        assignVariables();

        Log.e("campaign", campaign);

        if (sharedpreferences.getBoolean("At first", false)) {
            sqliteDB.delete_rows("LEADS");
            sharedpreferences.edit().putBoolean("At first", false).apply();
        }

        Log.i("userInfo", userInfo.toString());
        Log.i("apiInfo", apiInfo.toString());
        Log.i("autoDialInfo", autoDialInfo.toString());

        if (campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
            mainFilterList = sqliteDB.get_values("MAINFILTER", "---Select---");
        } else {
            mainFilterList.add(campaign);
        }

        callStatusList = sqliteDB.get_call_values("LEADCALLSTATUS");


        ArrayAdapter<String> filterAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, mainFilterList);
        sp_main_filter.setAdapter(filterAdapter);

        if (campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
            sp_call_status.setItems(callStatusList);
            sp_call_status.setMaxSelection(2);
        }


        if (!autoDialInfo.getStatus().equals("stop") && campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {

            sp_call_status.setSelection(autoDialInfo.getCallStatusValue());
            int spinnerPosition = filterAdapter.getPosition(autoDialInfo.getMainFilterValue());
            sp_main_filter.setSelection(spinnerPosition);
            subFilterList = sqliteDB.get_call_values("SUBFILTER");
            sp_sub_filter.setItems(subFilterList);
            sp_sub_filter.setSelection(autoDialInfo.getSubFilterValue());
        } else if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
            rv_leads.setVisibility(View.INVISIBLE);
            tv_no_data.setVisibility(View.INVISIBLE);
        } else {
            rv_leads.setVisibility(View.GONE);
            tv_no_data.setVisibility(View.VISIBLE);
        }


        viewLeadsFromDB();
        viewChanges();

        if (!permissionInfo.getAutoDialMainFilter()) {
            sp_main_filter.setVisibility(View.INVISIBLE);
        }
        if (!permissionInfo.getAutoDialSubFilter()) {
            sp_sub_filter.setVisibility(View.INVISIBLE);
        }
        if (!permissionInfo.getAutoDialStatusFilter()) {
            sp_call_status.setVisibility(View.INVISIBLE);
        }

        rv_leads.setLayoutManager(new LinearLayoutManager(getActivity()));


        sp_main_filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (position > 0) {
                    mainFilterItemSelectedHandler();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = true;
                if (sp_main_filter.getSelectedItemPosition() < 1) {
                    check = false;
                } else if (sp_sub_filter.getSelectedItemsAsString().isEmpty()) {
                    check = false;
                }
                if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN) && userInfo.getCrmDomain().equals("asterdialer")) {
                    check = true;
                }

                if (check)
                    ivSearchClickHandler();
                else
                    Toast.makeText(getActivity(), "Please select the filter", Toast.LENGTH_SHORT).show();
            }
        });

        iv_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!autoDialInfo.getStatus().equals("start")) {
                    ivClearOnClickHandler();
                    viewLeadsFromDB();
                } else {
                    Toast.makeText(getActivity(), "Please stop the dialer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        iv_start_auto_dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leadViewList.size() == 0) {
                    Toast.makeText(getActivity(), "No leads Fetched", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        boolean check = true;
                        if (sp_main_filter.getSelectedItemPosition() < 0) check = false;
                        if (subFilterList.size() > 0 && sp_sub_filter.getSelectedItemsAsString().isEmpty()) check = false;
                        if (leadViewList.size() == 0) check = false;
                        if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) check = true;

                        if (check) {
                            if (userInfo.getStatus().equals("Available")) {
                                startAutoDial();
                                Toast.makeText(getActivity(), "Auto Dial Started", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Unable to start", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Please select the filter", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        iv_stop_auto_dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_start_auto_dial.setVisibility(View.VISIBLE);
                iv_stop_auto_dial.setVisibility(View.GONE);

                try {
                    stopAutoDial();
                    autoDialInfo.setStatus("stop");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                sharedpreferences.edit().putString(AUTO_DIAL_INFO, new Gson().toJson(autoDialInfo)).apply();
                Toast.makeText(getActivity(), "Auto Dial Stopped", Toast.LENGTH_SHORT).show();
                applicationInsight.trackEvent("AutoDial", "Stopped");
            }
        });

        bt_skip_leads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt_skip_leads.setVisibility(View.INVISIBLE);
                StringBuilder id = new StringBuilder();
                if (multiSelectList.size() > 0 && !autoDialInfo.getStatus().equals("stop")) {
                    for (int i = 0; i < multiSelectList.size(); i++) {
                        if (i == multiSelectList.size() - 1) {
                            id.append(multiSelectList.get(i).getId());
                        } else {
                            id.append(multiSelectList.get(i).getId());
                            id.append(",");
                        }
                    }
                    sqliteDB.deleteLeads(String.valueOf(id));
                    com.nidoos.doocti.utils.Call c2c = new com.nidoos.doocti.utils.Call(getActivity().getApplicationContext(), getActivity().getApplication(), mSocket);
                    c2c.checkDataSize();
                    viewLeadsFromDB();
                } else if (multiSelectList.size() > 0) {
                    for (int i = 0; i < multiSelectList.size(); i++) {
                        leadViewList.remove(multiSelectList.get(i));
                    }
                    checkDataSize(campaign);
                }

                isMultiSelect = false;
                multiSelectList = new ArrayList<>();
                refreshAdapter();
                Log.i("Data Size", String.valueOf(sqliteDB.get_leads().size()));
                applicationInsight.trackEvent("AutoDial", multiSelectList.size() + "Leads Skipped");
            }
        });

        rv_leads.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv_leads, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.i("Single Click", "OK");
                if (isMultiSelect) {
                    multiSelect(position);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Log.i("Long Click", "OK");
                if (!isMultiSelect) {
                    multiSelectList = new ArrayList<>();
                    isMultiSelect = true;
                }
                multiSelect(position);
            }
        }));

        return rootView;
    }

    private void assignVariables() {
        app = (Doocti) getActivity().getApplicationContext();
        mSocket = app.getSocket();

        applicationInsight = new ApplicationInsight(requireContext().getApplicationContext(), getActivity().getApplication());
        applicationInsight.trackPageView("AutoDialFragment", "");

        sqliteDB = new SqliteDB(getActivity());
        sharedpreferences = requireActivity().getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
        permissionInfo = SharedPrefHelper.getPermissionInfo(sharedpreferences);
        apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);
        callInfo = SharedPrefHelper.getCallInfo(sharedpreferences);
        autoDialInfo = SharedPrefHelper.getAutoDialInfo(sharedpreferences);

        campaign = sharedpreferences.getString(SharedPrefConstants.CAMPAIGN, SharedPrefConstants.DEFAULT_CAMPAIGN);


        mainFilterList = new ArrayList<>();
        subFilterList = new ArrayList<>();
        callStatusList = new ArrayList<>();
        leadViewList = new ArrayList<>();
        multiSelectList = new ArrayList<>();
    }

    private void stopAutoDial() {


        String event = callInfo.getEvent();
        if (event.equals("")) {
            JSONObject queueResume = new JSONObject();
            try {
                queueResume.put("action", "QueuePause");
                queueResume.put("agent", userInfo.getEmail());
                queueResume.put("station", userInfo.getExtension());
                queueResume.put("paused", false);
                queueResume.put("tenant_id", userInfo.getTenantId());
                queueResume.put("call", false);
                if (!campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
                    queueResume.put("campaign", campaign);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("dispo-pause", queueResume.toString());

            if (mSocket.connected()) {
                mSocket.emit("dispo-pause", queueResume);
                Log.e("event TX","dispo-pause,"+ queueResume.toString());
            }

        }
    }

    private void startAutoDial() {
        if (leadViewList.size() > 0) {
            sqliteDB.delete_rows("LEADS");
        }
        for (int i = 0; i < leadViewList.size(); i++) {
            sqliteDB.insert_lead_info(leadViewList.get(i).getLeadID(), leadViewList.get(i).getLeadName(), leadViewList.get(i).getLeadNumber(), leadViewList.get(i).getStatus());
        }

        autoDialInfo.setStatus("start");
        SharedPrefHelper.putAutoDialInfo(sharedpreferences,autoDialInfo);


        try {
            if (!autoDialInfo.getStatus().equals("pause")) {
                    autoDialInfo.setMainFilterValue(sp_main_filter.getSelectedItem().toString());
                    autoDialInfo.setSubFilterValue(sp_sub_filter.getSelectedStrings());
                    autoDialInfo.setCallStatusValue(sp_call_status.getSelectedStrings());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        autoDialInfo.setStatus("start");
        SharedPrefHelper.putAutoDialInfo(sharedpreferences,autoDialInfo);
        com.nidoos.doocti.utils.Call c2c = new com.nidoos.doocti.utils.Call(getActivity().getApplicationContext(), getActivity().getApplication(), mSocket);
        c2c.startAutoDial();


        viewChanges();
        applicationInsight.trackEvent("AutoDial", "");
    }

    private void ivClearOnClickHandler() {
        sp_main_filter.setEnabled(true);
        sp_sub_filter.setEnabled(true);
        sp_call_status.setEnabled(true);
        iv_search.setEnabled(true);
        iv_search.setVisibility(View.VISIBLE);
        iv_start_auto_dial.setVisibility(View.VISIBLE);
        iv_stop_auto_dial.setVisibility(View.GONE);
        iv_clear.setVisibility(View.GONE);

        for(String m : mainFilterList) {
            Log.d("mainfilterlist",m);
        }

        List<String> filter = new ArrayList<String>();
        filter.add("Select");


        sp_sub_filter.setSelection(filter);
        sp_call_status.setSelection(filter);

        autoDialInfo = new AutoDialInfo(
                "stop",
                0,
                50,
                false,
                "",
                new ArrayList<>(),
                new ArrayList<>()
        );

        sharedpreferences.edit().putString(AUTO_DIAL_INFO, new Gson().toJson(autoDialInfo)).apply();

        autoDialInfo.setStatus("stop");

        rv_leads.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.VISIBLE);
        applicationInsight.trackEvent("AutoDial", "Clear");
        sqliteDB.delete_rows("LEADS");
    }

    private void ivSearchClickHandler() {
        leadViewList = new ArrayList<>();

        getLeads();

        autoDialInfo = new AutoDialInfo(
                "stop",
                0,
                50,
                false,
                "",
                new ArrayList<>(),
                new ArrayList<>()
        );

        sharedpreferences.edit().putString(AUTO_DIAL_INFO, new Gson().toJson(autoDialInfo)).apply();

        iv_search.setVisibility(View.GONE);
        iv_clear.setVisibility(View.VISIBLE);
        sp_main_filter.setEnabled(false);
        sp_call_status.setEnabled(false);
        sp_sub_filter.setEnabled(false);


    }

    private void mainFilterItemSelectedHandler() {
        String name = sp_main_filter.getSelectedItem().toString();
        autoDialInfo.setMainFilterValue(sqliteDB.get_id_by_name("MAINFILTER", name));

        if (autoDialInfo.getStatus().equals("stop")) {
            if (leadViewList.size() != 0) {
                leadViewList = new ArrayList<>();
                refreshAdapter();
            }
            getSubFilter(autoDialInfo.getMainFilterValue());
            if (name.equals("Campaign")) {
                sp_call_status.setEnabled(false);
                sp_call_status.setItems(new ArrayList<>());
            } else {
                sp_call_status.setEnabled(true);
                sp_call_status.setItems(callStatusList);
            }
        }
    }

    private void checkDataSize(String campaign) {
        if (leadViewList.size() <= 30 && autoDialInfo.getMoreData() && !campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
            getLeads();
        }
    }

    private void multiSelect(int position) {
        if (multiSelectList.contains(leadViewList.get(position))) {
            multiSelectList.remove(leadViewList.get(position));
        } else {
            multiSelectList.add((leadViewList.get(position)));
        }

        if (multiSelectList.size() > 0) {
            bt_skip_leads.setVisibility(View.VISIBLE);
        } else {
            isMultiSelect = false;
            multiSelectList = new ArrayList<>();
            bt_skip_leads.setVisibility(View.INVISIBLE);
        }
        refreshAdapter();
    }

    private void viewLeadsFromDB() {
        leadViewList = sqliteDB.get_leads();
        if (leadViewList.size() == 0) {
            rv_leads.setVisibility(View.GONE);
            tv_no_data.setVisibility(View.VISIBLE);
        } else {
            pb_loader.setVisibility(View.GONE);
            rv_leads.setVisibility(View.VISIBLE);
            tv_no_data.setVisibility(View.GONE);
            viewLeadAutoDial = new ViewLeadAutoDial(leadViewList, multiSelectList, R.layout.layout_view_leads_auto_dial, getActivity());
            rv_leads.setAdapter(viewLeadAutoDial);
        }
    }

    private void refreshAdapter() {
        try {
            viewLeadAutoDial.resultList = leadViewList;
            viewLeadAutoDial.selectedList = multiSelectList;
            viewLeadAutoDial.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLeads() {
        if (campaign.equals(SharedPrefConstants.DEFAULT_CAMPAIGN)) {
            rv_leads.setVisibility(View.GONE);
            pb_loader.setVisibility(View.VISIBLE);


            List<FetchLeadsRequest.SubFilterValue> subFilterValues = sqliteDB.getSubFilterValue("SUBFILTER", sp_sub_filter.getSelectedItemsAsString());
            List<FetchLeadsRequest.Callstatus> callStatus = sqliteDB.getCallStatusValue("LEADCALLSTATUS", sp_call_status.getSelectedItemsAsString());



            getLeadsFromUrl(subFilterValues, callStatus);
        } else {
            rv_leads.setVisibility(View.GONE);
            pb_loader.setVisibility(View.VISIBLE);


            List<FetchLeadsRequest.SubFilterValue> subFilterValues = new ArrayList<>();
            List<FetchLeadsRequest.Callstatus> callStatus = new ArrayList<>();

            subFilterValues.add(new FetchLeadsRequest.SubFilterValue("No data", "Nodata"));
            callStatus.add(new FetchLeadsRequest.Callstatus("No data", "No data"));

            getLeadsFromUrl(subFilterValues, callStatus);
        }
    }

    private void getLeadsFromUrl(List<FetchLeadsRequest.SubFilterValue> subFilterValues, List<FetchLeadsRequest.Callstatus> callStatus) {

        String name = sp_main_filter.getSelectedItem().toString();
        autoDialInfo.setMainFilterValue(sqliteDB.get_id_by_name("MAINFILTER", name));

        if (userInfo.getCrmDomain().equals("asterdialer")) {
            autoDialInfo.setMainFilterValue(campaign);
        }


        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);

        Call<FetchLeadsResponse> call = services.searchLeads(userInfo.getToken(), userInfo.getCrmDomain(), new FetchLeadsRequest(userInfo.getUserId(), autoDialInfo.getSkip(), autoDialInfo.getTake(), autoDialInfo.getMainFilterValue(), subFilterValues, callStatus));
        call.enqueue(new Callback<FetchLeadsResponse>() {
            @Override
            public void onResponse(@NonNull Call<FetchLeadsResponse> call, @NonNull Response<FetchLeadsResponse> response) {
                if (response.code() == 200) {
                    leadViewList.addAll(response.body().getData());
                    if (leadViewList.size() != 0) {

                        pb_loader.setVisibility(View.GONE);
                        rv_leads.setVisibility(View.VISIBLE);
                        tv_no_data.setVisibility(View.GONE);
                        if (leadViewList.size() > 0) {
                            sqliteDB.delete_rows("LEADS");
                        }
                        for (int i = 0; i < leadViewList.size(); i++) {
                            sqliteDB.insert_lead_info(leadViewList.get(i).getLeadID(), leadViewList.get(i).getLeadName(), leadViewList.get(i).getLeadNumber(), leadViewList.get(i).getStatus());
                        }

                        viewLeadAutoDial = new ViewLeadAutoDial(leadViewList, multiSelectList, R.layout.layout_view_leads_auto_dial, getActivity());
                        rv_leads.setAdapter(viewLeadAutoDial);
                    } else {
                        pb_loader.setVisibility(View.GONE);
                        rv_leads.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                } else {
                    pb_loader.setVisibility(View.GONE);
                    rv_leads.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<FetchLeadsResponse> call, @NonNull Throwable t) {
                Log.e("error", t.toString());
                pb_loader.setVisibility(View.GONE);
                rv_leads.setVisibility(View.GONE);
                tv_no_data.setVisibility(View.VISIBLE);
                applicationInsight.trackEvent("GetLeadsAutoDial Exception", t.toString());
            }
        });
    }

    private void getSubFilter(String value) {
        pb_loader.setVisibility(View.VISIBLE);
        sp_sub_filter.setEnabled(false);

        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<SubFilterResponse> call = services.getSubFilter(userInfo.getToken(), userInfo.getCrmDomain(), value);
        call.enqueue(new Callback<SubFilterResponse>() {
            @Override
            public void onResponse(@NonNull Call<SubFilterResponse> call, @NonNull Response<SubFilterResponse> response) {
                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        pb_loader.setVisibility(View.GONE);
                        sp_sub_filter.setEnabled(true);
                        List<SubFilterResponse.Datum> group;
                        if (response.isSuccessful()) {
                            subFilterList = new ArrayList<>();
                            group = response.body().getData();
                            for (int i = 0; i < group.size(); i++) {
                                subFilterList.add(group.get(i).getName());
                            }

                            sp_sub_filter.setItems(subFilterList);

                            if (sp_main_filter.getSelectedItem().toString().equals("Campaign")) {
                                sp_sub_filter.setMaxSelection(1);
                            } else {
                                sp_sub_filter.setMaxSelection(3);
                            }

                            if (group.size() > 0) {
                                sqliteDB.delete_rows("SUBFILTER");
                            }
                            for (int i = 0; i < group.size(); i++) {
                                sqliteDB.insert_values("SUBFILTER", group.get(i).getName(), group.get(i).getValue());
                            }

                        }
                    } else {
                        sp_sub_filter.setEnabled(true);
                        subFilterList = new ArrayList<>();
                        subFilterList.add("No data");
                        sp_sub_filter.setItems(subFilterList);
                        pb_loader.setVisibility(View.GONE);
                    }
                } else {
                    sp_sub_filter.setEnabled(true);
                    pb_loader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<SubFilterResponse> call, @NonNull Throwable t) {
                pb_loader.setVisibility(View.GONE);
                applicationInsight.trackEvent("SubFilter Exception", t.toString());
            }
        });
    }

    private void viewChanges() {
        sp_main_filter.setEnabled(false);
        sp_sub_filter.setEnabled(false);
        sp_call_status.setEnabled(false);
        iv_search.setEnabled(false);
        iv_search.setVisibility(View.GONE);
        if (autoDialInfo.getStatus().equals("start")) {
            iv_start_auto_dial.setVisibility(View.GONE);
            iv_stop_auto_dial.setVisibility(View.VISIBLE);
        } else if (autoDialInfo.getStatus().equals("pause")) {
            iv_start_auto_dial.setVisibility(View.VISIBLE);
            iv_stop_auto_dial.setVisibility(View.GONE);
        }
        iv_clear.setVisibility(View.VISIBLE);
    }

}