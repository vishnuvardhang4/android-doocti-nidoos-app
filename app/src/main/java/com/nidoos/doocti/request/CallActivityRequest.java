package com.nidoos.doocti.request;

import android.os.Build;
import android.os.StrictMode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nidoos.doocti.BuildConfig;

import java.util.Calendar;
import java.util.Date;

public class CallActivityRequest {

    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("callType")
    @Expose
    private String callType;
    @SerializedName("leadModule")
    @Expose
    private String leadModule;
    @SerializedName("leadID")
    @Expose
    private String leadID;
    @SerializedName("ownerID")
    @Expose
    private String ownerID;
    @SerializedName("ownerName")
    @Expose
    private String ownerName;
    @SerializedName("callStartTime")
    @Expose
    private String callStartTime;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("callDescription")
    @Expose
    private String callDescription;


    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("callResult")
    @Expose
    private CallResult callResult;
    @SerializedName("callEndTime")
    @Expose
    private String callEndTime;
    @SerializedName("uniqueID")
    @Expose
    private String uniqueID;
    @SerializedName("campaign")
    @Expose
    private String campaign;

    public CallActivityRequest(String subject, String call_type, String module, String leadID, String userID, String username, String start_time, String phone_number, String description, String duration, CallResult dispo_status, String end_time, String uniqueID) {
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .penaltyLog()
                    .build());
        }
        if(!subject.equals("")) {
            this.subject = subject;
        }else{
            this.subject = "Call from " + phone_number + " to ";
//            "Call from " + phone_number + " to "+extension;
        }
        this.callType = call_type;
        this.leadModule = module;
        this.leadID = leadID;
        this.ownerID = userID;
        this.ownerName = username;
        this.callStartTime = start_time;
        this.phoneNumber = phone_number;
        this.callDescription = description;
        this.duration = duration;
        this.callResult = dispo_status;
        if(end_time == ""){
            Date currentTime = Calendar.getInstance().getTime();
            this.callEndTime = currentTime.toString();
        }else {
            this.callEndTime = end_time;
        }   
        this.uniqueID = uniqueID;
    }

    public CallActivityRequest(String subject, String call_type, String module, String leadID, String userID, String username, String start_time, String phone_number, String description, String duration, CallResult dispo_status, String end_time, String uniqueID,String campaign){
        if (BuildConfig.DEBUG && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .penaltyLog()
                    .build());
        }
        if(!subject.equals("")) {
            this.subject = subject;
        }else{
            this.subject = "Call from " + phone_number + " to ";
//            "Call from " + phone_number + " to "+extension;
        }
        this.callType = call_type;
        this.leadModule = module;
        this.leadID = leadID;
        this.ownerID = userID;
        this.ownerName = username;
        this.callStartTime = start_time;
        this.phoneNumber = phone_number;
        this.callDescription = description;
        this.duration = duration;
        this.callResult = dispo_status;
        if(end_time.equals("")){
            Date currentTime = Calendar.getInstance().getTime();
            this.callEndTime = currentTime.toString();
        }else {
            this.callEndTime = end_time;
        }
        this.uniqueID = uniqueID;
        this.campaign = campaign;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getLeadModule() {
        return leadModule;
    }

    public void setLeadModule(String leadModule) {
        this.leadModule = leadModule;
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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCallStartTime() {
        return callStartTime;
    }

    public void setCallStartTime(String callStartTime) {
        this.callStartTime = callStartTime;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCallDescription() {
        return callDescription;
    }

    public void setCallDescription(String callDescription) {
        this.callDescription = callDescription;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public CallResult getCallResult() {
        return callResult;
    }

    public void setCallResult(CallResult callResult) {
        this.callResult = callResult;
    }

    public String getCallEndTime() {
        return callEndTime;
    }

    public void setCallEndTime(String callEndTime) {
        this.callEndTime = callEndTime;
    }

    public static class CallResult {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("value")
        @Expose
        private String value;

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



    @Override
    public String toString() {
        return "CallActivityRequest{" +
                "subject='" + subject + '\'' +
                ", callType='" + callType + '\'' +
                ", leadModule='" + leadModule + '\'' +
                ", leadID='" + leadID + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", callStartTime='" + callStartTime + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", callDescription='" + callDescription + '\'' +
                ", duration='" + duration + '\'' +
                ", callResult=" + callResult +
                ", callEndTime='" + callEndTime + '\'' +
                ", uniqueID='" + uniqueID + '\'' +
                ", campaign='" + campaign + '\'' +
                '}';
    }
}
