package com.nidoos.doocti.views.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.nkzawa.socketio.client.Socket;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.R;
import com.nidoos.doocti.response.GetContactsReponse;
import com.nidoos.doocti.retrofit.RetrofitApiClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.socket.Doocti;
import com.nidoos.doocti.utils.ApplicationInsight;
import com.nidoos.doocti.views.Adapter.ContactsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactsViewFragment extends Fragment {

    @BindView(R.id.tv_no_data)
    TextView tv_no_data;

    @BindView(R.id.rv_contacts)
    RecyclerView rv_contacts;

    @BindView(R.id.pb_loader)
    ProgressBar pb_loader;

    @BindView(R.id.et_search_contacts)
    EditText et_search_contacts;


    List<GetContactsReponse.Datum> list;
    private Socket mSocket;
    private Doocti app;
    private SharedPreferences sharedpreferences;
    private boolean moreData, isLoading ;
    private String name, number;
    private ContactsAdapter contactsAdapter;
    private ApplicationInsight applicationInsight;

    private UserInfo userInfo;
    private ApiInfo apiInfo;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.layout_contacts_view, container, false);
        ButterKnife.bind(this, rootView);

        app = (Doocti) getActivity().getApplicationContext();
        mSocket = app.getSocket();

        name = number = "";
        moreData = isLoading = false;
        list = new ArrayList<>();


        applicationInsight = new ApplicationInsight(app.getApplicationContext(), getActivity().getApplication());
        applicationInsight.trackPageView("ContactsFragment", "");

        sharedpreferences = app.getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
        apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);

        rv_contacts.setLayoutManager(new LinearLayoutManager(getActivity()));

        et_search_contacts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 3) {
                    String regexStr = "^[0-9]*$";
                    if (s.toString().trim().matches(regexStr)) {
                        number = s.toString();
                    } else {
                        name = s.toString();
                    }
                    getContacts();
                } else if (s.toString().length() == 0) {
                    getContacts();
                }
            }
        });

        getContacts();

        return rootView;
    }

    private void getContacts() {
        rv_contacts.setVisibility(View.GONE);
        pb_loader.setVisibility(View.VISIBLE);

        int skip = 0, take = 50;

        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<GetContactsReponse> call = services.getContacts(userInfo.getToken(), userInfo.getCrmDomain(), userInfo.getUserId(), name, number, skip, take);
        call.enqueue(new Callback<GetContactsReponse>() {
            @Override
            public void onResponse(@NonNull Call<GetContactsReponse> call, @NonNull Response<GetContactsReponse> response) {
                pb_loader.setVisibility(View.GONE);
                if (response.code() == 200) {
                    list = response.body().getData();
                    if (list.size() != 0) {
                        rv_contacts.setVisibility(View.VISIBLE);
                        tv_no_data.setVisibility(View.GONE);

                        contactsAdapter = new ContactsAdapter(list, R.layout.layout_contact_screen, getActivity(), new ContactsAdapter.OnContactItemClickListener() {
                            @Override
                            public void onItemClick(GetContactsReponse.Datum item) {
                                com.nidoos.doocti.utils.Call c2c = new com.nidoos.doocti.utils.Call(getActivity().getApplicationContext(), getActivity().getApplication(), mSocket);
                                c2c.clickToCall(item.getContactNumber(), "MANUAL", item.getContactID());
                            }
                        });

                        rv_contacts.setAdapter(contactsAdapter);

                        moreData = response.body().getMoreData();
                        initScrollListener();
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
            public void onFailure(@NonNull Call<GetContactsReponse> call, @NonNull Throwable t) {
                pb_loader.setVisibility(View.GONE);
                tv_no_data.setVisibility(View.VISIBLE);
                applicationInsight.trackEvent("Contacts Exception", t.toString());
                Log.e("error", t.toString());
            }
        });
    }

    private void initScrollListener() {
        rv_contacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (moreData && !isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == list.size() - 1) {
                        //bottom of list!
                        Log.i("loadmore", "data");
                        loadMoreContacts();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMoreContacts() {
        list.add(null);
        rv_contacts.post(new Runnable() {
            public void run() {
                contactsAdapter.notifyItemInserted(list.size() - 1);
            }
        });

        int skip = 0, take = 50;

        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<GetContactsReponse> call = services.getContacts(userInfo.getToken(), userInfo.getCrmDomain(), userInfo.getUserId(), name, number, skip, take);
        call.enqueue(new Callback<GetContactsReponse>() {
            @Override
            public void onResponse(@NonNull Call<GetContactsReponse> call, @NonNull Response<GetContactsReponse> response) {
                pb_loader.setVisibility(View.GONE);
                if (response.code() == 200) {
                    list.remove(list.size() - 1);
                    int scrollPosition = list.size();
                    contactsAdapter.notifyItemRemoved(scrollPosition);
                    assert response.body() != null;
                    list.addAll(response.body().getData());
                    moreData = response.body().getMoreData();
                }
                contactsAdapter.notifyDataSetChanged();
                isLoading = false;

            }

            @Override
            public void onFailure(@NonNull Call<GetContactsReponse> call, @NonNull Throwable t) {
                pb_loader.setVisibility(View.GONE);
                tv_no_data.setVisibility(View.VISIBLE);
                Log.e("error", t.toString());
            }
        });
    }
}
