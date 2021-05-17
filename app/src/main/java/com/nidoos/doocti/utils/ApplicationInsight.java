package com.nidoos.doocti.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.microsoft.applicationinsights.library.ApplicationInsights;
import com.microsoft.applicationinsights.library.TelemetryClient;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.info.UserInfo;

import java.util.HashMap;
import java.util.Random;

public class ApplicationInsight {
    private Context applicationContext;
    private Application application;
    private TelemetryClient client;
    private SharedPreferences sharedpreferences;
    private UserInfo userInfo;

    public ApplicationInsight(Context applicationContext, Application application) {
        this.applicationContext = applicationContext;
        this.application = application;
        sharedpreferences = applicationContext.getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        int random = new Random().nextInt((1000) + 1);
        ApplicationInsights.setup(applicationContext, application);
        ApplicationInsights.disableAutoCollection();
        ApplicationInsights.disableAutoPageViewTracking();
        ApplicationInsights.disableAutoSessionManagement();
        ApplicationInsights.renewSession(String.valueOf(random));
        ApplicationInsights.start();
        client = TelemetryClient.getInstance();
        ApplicationInsights.setDeveloperMode(false);
        try {
            userInfo = new Gson().fromJson(sharedpreferences.getString(SharedPrefConstants.USER_INFO,"userInfo Not found"), UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void trackEvent(String eventName, String payload) {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("tenant_code", userInfo.getTenantId());
        properties.put("payload", payload);
        client.trackEvent(eventName, properties);
    }


    public void trackPageView(String eventName, String payload) {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("tenant_code", userInfo.getTenantId());

    }

}
