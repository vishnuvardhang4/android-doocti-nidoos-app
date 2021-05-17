package com.nidoos.doocti.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.UserInfo;
import com.nidoos.doocti.R;
import com.nidoos.doocti.request.CallActivityRequest;
import com.nidoos.doocti.response.CallActivityResponse;
import com.nidoos.doocti.retrofit.RetrofitApiClient;
import com.nidoos.doocti.retrofit.Services;
import com.nidoos.doocti.sqliteDB.SqliteDB;
import com.nidoos.doocti.utils.ApplicationInsight;
import com.nidoos.doocti.utils.Internet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.nidoos.doocti.constants.SharedPrefConstants.API_INFO;

public class CreateCallActivity extends AppCompatActivity {

    @BindView(R.id.bt_save)
    Button bt_save;

    @BindView(R.id.et_phone_number)
    EditText et_phone_number;

    @BindView(R.id.et_date_picker)
    EditText et_date_picker;

    @BindView(R.id.et_time_picker)
    EditText et_time_picker;

    @BindView(R.id.et_minutes)
    EditText et_minutes;

    @BindView(R.id.et_seconds)
    EditText et_seconds;

    @BindView(R.id.et_call_comments)
    EditText et_call_comments;

    @BindView(R.id.sp_call_type)
    Spinner sp_call_type;

    @BindView(R.id.sp_dispo_status)
    Spinner sp_dispo_status;

    @BindView(R.id.pb_loader)
    ProgressBar pb_loader;

    @BindView(R.id.ll_full_view)
    LinearLayout ll_full_view;

    private SharedPreferences sharedpreferences;

    private SqliteDB sqliteDB;

    private List<String> callTypeList = new ArrayList<>();
    private List<String> statusList = new ArrayList<>();

    private String description = "", call_type = "", module = "", subject = "", duration = "00:00", start_time = "",
            end_time = "", answer_time = "", calldate, leadName = "", leadEmail = "", leadID = "", call_event = "", phone_number;

    private CallActivityRequest.CallResult dispo_status;

    private ArrayAdapter statusAdapter, callTypeAdapter;


    private ApplicationInsight applicationInsight;
    
    private UserInfo userInfo;
    private ApiInfo apiInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_call);
        ButterKnife.bind(this);

        sqliteDB = new SqliteDB(this);
        sharedpreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userInfo = new Gson().fromJson(sharedpreferences.getString(SharedPrefConstants.USER_INFO,"userInfo Not found"), UserInfo.class);
        apiInfo = new Gson().fromJson(sharedpreferences.getString(API_INFO, "apiInfo Not found"),ApiInfo.class);


        applicationInsight = new ApplicationInsight(getApplicationContext(), getApplication());
        applicationInsight.trackPageView("CreateCallActivity", "");

        Log.i("userInfo", userInfo.toString());

        callTypeList = new ArrayList<>();
        callTypeList.add("Select Call Type");
        callTypeList.add("INBOUND");
        callTypeList.add("OUTBOUND");

        statusList = sqliteDB.get_values("LEADCALLSTATUS", "Select Dispo Status");

        statusAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, statusList);
        sp_dispo_status.setAdapter(statusAdapter);

        callTypeAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, callTypeList);
        sp_call_type.setAdapter(callTypeAdapter);


        et_date_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year_x = c.get(Calendar.YEAR);
                int month_x = c.get(Calendar.MONTH);
                int day_x = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateCallActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
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
                mTimePicker = new TimePickerDialog(CreateCallActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                }, hour, minute, true);//Yes 24 hour time
//                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = true;
                phone_number = et_phone_number.getText().toString();
                int position = sp_call_type.getSelectedItemPosition();
                if (position == 0) {
                    check = false;
                    Toast.makeText(CreateCallActivity.this, "Select Call Type", Toast.LENGTH_SHORT).show();
                } else {
                    call_type = sp_call_type.getSelectedItem().toString();
                }

                start_time = et_date_picker.getText().toString() + " " + et_time_picker.getText().toString();
                duration = et_minutes.getText().toString() + ":" + et_seconds.getText().toString();

                if (et_call_comments.getText().toString().isEmpty()) {
                    description = "";
                } else {
                    description = et_call_comments.getText().toString();
                }

                int position1 = sp_dispo_status.getFirstVisiblePosition();
                if (position1 == 0) {
                    check = false;
                    Toast.makeText(CreateCallActivity.this, "Select Dispo Status", Toast.LENGTH_SHORT).show();
                } else {
                    dispo_status = sqliteDB.getCallStatus("LEADCALLSTATUS", sp_dispo_status.getSelectedItem().toString());
                }
                if (phone_number.isEmpty()) {
                    check = false;
                    et_phone_number.requestFocus();
                    et_phone_number.setError("Mobile number is required");
                }

                if (et_date_picker.getText().toString().isEmpty() || et_time_picker.getText().toString().isEmpty()) {
                    check = false;
                    Toast.makeText(CreateCallActivity.this, "Date & Time is required", Toast.LENGTH_SHORT).show();
                }

                if (et_minutes.getText().toString().isEmpty()) {
                    check = false;
                    et_minutes.requestFocus();
                    et_minutes.setError("Date is required");
                }
                if (et_seconds.getText().toString().isEmpty()) {
                    check = false;
                    et_seconds.requestFocus();
                    et_seconds.setError("Time is required");
                } else {
                    if (Integer.parseInt(et_seconds.getText().toString()) >= 60) {
                        check = false;
                        et_seconds.requestFocus();
                        et_seconds.setError("Enter less than 60");
                    }
                }

                if (call_type.equals("OUTBOUND")) {
                    subject = "Call from " + userInfo.getExtension() + " to " + phone_number;
                } else if (call_type.equals("INBOUND")) {
                    subject = "Call from " + phone_number + " to " + userInfo.getExtension();
                } else if(call_type.equals("AUTO")){
                    subject = "Call from " + phone_number + " to "+userInfo.getExtension();
                }

                if (check) {
                    pb_loader.setVisibility(View.VISIBLE);
                    ll_full_view.setVisibility(View.GONE);
                    bt_save.setVisibility(View.GONE);
                    if (Internet.isConnected(getApplicationContext())) {
                        Services services = RetrofitApiClient.getClient(apiInfo.getApiDomain()).create(Services.class);
                        Call<CallActivityResponse> call = services.createCallActivity(userInfo.getToken(),userInfo.getCrmDomain(), new CallActivityRequest(subject, call_type, module, leadID, userInfo.getUserId(), userInfo.getUsername(), start_time, phone_number, description, duration, dispo_status, end_time, ""));
                        call.enqueue(new Callback<CallActivityResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<CallActivityResponse> call, @NonNull Response<CallActivityResponse> response) {
                                if (response.code() == 201) {
                                    pb_loader.setVisibility(View.GONE);
                                    ll_full_view.setVisibility(View.VISIBLE);
                                    bt_save.setVisibility(View.VISIBLE);
                                    Toast.makeText(CreateCallActivity.this, "Successfully Created", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CreateCallActivity.this, DialerActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(CreateCallActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    pb_loader.setVisibility(View.GONE);
                                    ll_full_view.setVisibility(View.VISIBLE);
                                    bt_save.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<CallActivityResponse> call, @NonNull Throwable t) {
                                Toast.makeText(CreateCallActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                pb_loader.setVisibility(View.GONE);
                                ll_full_view.setVisibility(View.VISIBLE);
                                bt_save.setVisibility(View.VISIBLE);
                                applicationInsight.trackEvent("CallActivity Exception", t.toString());
                            }
                        });
                    } else {
                        sqliteDB.insert_call_activity(subject, call_type, start_time, phone_number, description, duration, sp_dispo_status.getSelectedItem().toString());
                        Toast.makeText(CreateCallActivity.this, "Successfully Created", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateCallActivity.this, DialerActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

    }

}
