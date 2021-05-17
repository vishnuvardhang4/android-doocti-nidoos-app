package com.nidoos.doocti.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.R;
import com.nidoos.doocti.request.UpdateCampaignRequest;
import com.nidoos.doocti.response.CampaignResponse;
import com.nidoos.doocti.response.CampaignUpdateResponse;
import com.nidoos.doocti.retrofit.RetrofitApiClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.sqliteDB.SqliteDB;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DomainSelectActivity extends AppCompatActivity {

    private Button bt_continue;
    private RadioGroup rg_domainSelection;
    private RadioButton rb_asterdialer;
    private RadioButton rb_others;
    private List<String> queueList = new ArrayList<>();
    private Spinner sp_asterdialer;
    private SharedPreferences sharedpreferences;
    private SqliteDB sqliteDB;
    private List<String> campaignList = new ArrayList<>();
    private ArrayAdapter statusAdapter;
    private ProgressBar pb_domain;

    private UserInfo userInfo;
    private ApiInfo apiInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
   
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domain_select);
        findViewById(R.id.sp_asterdialer).setVisibility(View.INVISIBLE);

        sqliteDB = new SqliteDB(this);
        bt_continue = findViewById(R.id.bt_continue);
        rg_domainSelection = findViewById(R.id.rg_domainSelection);
        rb_asterdialer = findViewById(R.id.rb_asterdialer);
        rb_others =findViewById(R.id.rb_others);
        sp_asterdialer =  findViewById(R.id.sp_asterdialer);
        pb_domain =  findViewById(R.id.pb_domain);

        int radioButtonID = rg_domainSelection.getCheckedRadioButtonId();

        RadioButton radioButton =rg_domainSelection.findViewById(radioButtonID);

        if(radioButton.getText().toString().equals("Others")){
            sp_asterdialer.setVisibility(View.INVISIBLE);
        }


        sharedpreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userInfo = new Gson().fromJson(sharedpreferences.getString(SharedPrefConstants.USER_INFO,"userInfo Not found"), UserInfo.class);
        apiInfo = new Gson().fromJson(sharedpreferences.getString(SharedPrefConstants.API_INFO,"apiInfo not found"),ApiInfo.class);


        sharedpreferences.edit().putBoolean(SharedPrefConstants.DOMAIN_SELECT,false).apply();
        sharedpreferences.edit().putBoolean("customerInfoApiCall",false).apply();
        sharedpreferences.edit().putBoolean("queuePaused_first_time",true).apply();
        sharedpreferences.edit().putString("current_uniqueid","").apply();
        sharedpreferences.edit().putString("whatsaapMessage","").apply();
        sharedpreferences.edit().putBoolean(SharedPrefConstants.HANGUP_RESPONSE,true).apply();


        if(userInfo.getCrmDomain().equals("asterdialer")) {
            asterCampaigns(userInfo.getToken());
            Toast.makeText(DomainSelectActivity.this,
                    "Select any Campaign", Toast.LENGTH_SHORT).show();
        }

        bt_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pb_domain.setVisibility(View.VISIBLE);
                int selectedId = rg_domainSelection.getCheckedRadioButtonId();

                RadioButton newBtn = findViewById(selectedId);
                Toast.makeText(DomainSelectActivity.this,
                        "You selected "+newBtn.getText(), Toast.LENGTH_SHORT).show();

                if(rb_asterdialer.isChecked()){
                    int position = sp_asterdialer.getFirstVisiblePosition();
                    if (position == 0) {
                        Toast.makeText(DomainSelectActivity.this, "Select a Campaign", Toast.LENGTH_SHORT).show();
                    } else {
                        String campaign = sp_asterdialer.getSelectedItem().toString();
                        Log.d("userEmail", userInfo.getEmail());
                        Log.d("campaign",campaign);

                        sharedpreferences.edit().putString(SharedPrefConstants.CAMPAIGN,campaign).apply();
                        sharedpreferences.edit().putBoolean("At first",true).apply();
                        asterCampaignUpdate(userInfo.getToken(), userInfo.getEmail(),campaign);
                    }
                }
                else if(rb_others.isChecked()){
                    sharedpreferences.edit().putString(SharedPrefConstants.CAMPAIGN,SharedPrefConstants.DEFAULT_CAMPAIGN).apply();
                    moveNextPage();
                }
            }
        });
    }


    public void asradioButtonClicked(View view) {

        if(view.getId() == R.id.rb_asterdialer) {
            findViewById(R.id.sp_asterdialer).setVisibility(View.VISIBLE);
        }else if(view.getId() == R.id.rb_others) {
            findViewById(R.id.sp_asterdialer).setVisibility(View.INVISIBLE);
        }

    }

    private  void asterCampaigns(String token){

        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<CampaignResponse> call = services.campaigns(token);

        call.enqueue(new Callback<CampaignResponse>() {
            @Override
            public void onResponse(Call<CampaignResponse> call, Response<CampaignResponse> response) {
                int code = response.code();
                if(code == 200){
                    List<CampaignResponse.Datum> group;
                    if(response.isSuccessful()){

                        sp_asterdialer.setVisibility(View.VISIBLE);
                        group = response.body().getData();
                        campaignList.add("Select a Campaign");

                        if (group.size() > 0) {
                            sqliteDB.delete_rows("CAMPAIGNS");
                        }
                        for (int i = 0; i < group.size(); i++) {
                            Log.d("name",group.get(i).getName().toString());
                            Log.d("value",group.get(i).getValue().toString());
                            sqliteDB.insert_values("CAMPAIGNS", group.get(i).getName(), group.get(i).getValue());
                        }
                        campaignList = sqliteDB.get_values("CAMPAIGNS", "Select a Campaign");

                        statusAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, campaignList);
                        sp_asterdialer.setAdapter(statusAdapter);

//                    domain_select.this.moveNextPage();
                    }
                }
                else{
                    Log.d("token",token);
                    Log.d("failure","data Getted");
                }
            }

            @Override
            public void onFailure(Call<CampaignResponse> call, Throwable t) {
                Log.e("error", t.toString());

            }
        });

    }

    private void asterCampaignUpdate(String token,String userEmail,String campaign){


        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
        Call<CampaignUpdateResponse> call = services.CampaignUpdate(token,userEmail,new UpdateCampaignRequest(campaign));
        call.enqueue(new Callback<CampaignUpdateResponse>() {
            @Override
            public void onResponse(Call<CampaignUpdateResponse> call, Response<CampaignUpdateResponse> response) {
                int code = response.code();
                if(code == 200){
                    Toast.makeText(DomainSelectActivity.this, "Selected Campaign is "+campaign, Toast.LENGTH_SHORT).show();

                    moveNextPage();
                } else if(code == 400){
                    Toast.makeText(DomainSelectActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                } else if(code == 403){
                    Toast.makeText(DomainSelectActivity.this, "Unauthorized Access", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DomainSelectActivity.this, code+" "+response.body().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CampaignUpdateResponse> call, Throwable t) {
                Toast.makeText(DomainSelectActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.e("error", t.toString());
            }
        });
    }

    private void moveNextPage(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                sharedpreferences.edit().putBoolean(SharedPrefConstants.DOMAIN_SELECT,true).apply();
                Intent intent = new Intent(DomainSelectActivity.this, DialerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 1500);

    }
}
