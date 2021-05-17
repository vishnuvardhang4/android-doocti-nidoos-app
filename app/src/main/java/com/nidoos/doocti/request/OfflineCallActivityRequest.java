package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OfflineCallActivityRequest {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("callType")
    @Expose
    private String callType;
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
    private String callResult;

    public OfflineCallActivityRequest(int id, String subject, String callType, String callStartTime, String phoneNumber, String callDescription, String duration, String callResult) {
        this.id = id;
        this.subject = subject;
        this.callType = callType;
        this.callStartTime = callStartTime;
        this.phoneNumber = phoneNumber;
        this.callDescription = callDescription;
        this.duration = duration;
        this.callResult = callResult;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCallResult() {
        return callResult;
    }

    public void setCallResult(String callResult) {
        this.callResult = callResult;
    }


}
