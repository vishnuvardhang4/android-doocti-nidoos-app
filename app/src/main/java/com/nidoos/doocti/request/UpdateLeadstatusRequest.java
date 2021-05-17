package com.nidoos.doocti.request;

import android.os.Build;
import android.os.StrictMode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nidoos.doocti.BuildConfig;

import org.json.JSONException;

public class UpdateLeadstatusRequest {

    @SerializedName("leadStatus")
    @Expose
    private LeadStatus leadStatus;
    @SerializedName("leadID")
    @Expose
    private String leadID;
    @SerializedName("ownerID")
    @Expose
    private String ownerID;

    public UpdateLeadstatusRequest(LeadStatus leadStatus,String leadID,String ownerID) throws JSONException {
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .penaltyLog()
                    .build());
        }
        this.leadStatus = leadStatus;
        this.leadID = leadID;
        this.ownerID = ownerID;
    }

    public LeadStatus getLeadStatus() {
        return leadStatus;
    }

    public void setLeadStatus(LeadStatus leadStatus) {
        this.leadStatus = leadStatus;
    }

    public String getLeadID() {
        return leadID;
    }

    public void setLeadID(String leadID) {
        this.leadID = leadID;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public static class LeadStatus {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("value")
        @Expose
        private String value;

        public LeadStatus(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}
