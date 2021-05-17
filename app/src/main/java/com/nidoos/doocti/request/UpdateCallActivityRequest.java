package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateCallActivityRequest {
    @SerializedName("callActivityID")
    @Expose
    private String callActivityID;
    @SerializedName("callDescription")
    @Expose
    private String callDescription;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("callResult")
    @Expose
    private String callResult;
    @SerializedName("callEndTime")
    @Expose
    private String callEndTime;
    @SerializedName("leadID")
    @Expose
    private String leadID;
    @SerializedName("ownerID")
    @Expose
    private String ownerID;
    @SerializedName("ownerName")
    @Expose
    private String ownerName;

    public UpdateCallActivityRequest(String callID, String comments, String duration, String result, String endtime, String leadid, String userID, String username) {
        this.callActivityID = callID;
        this.callDescription = comments;
        this.duration = duration;
        this.callResult = result;
        this.callEndTime = endtime;
        this.leadID = leadid;
        this.ownerID = userID;
        this.ownerName = username;
    }

    public String getCallActivityID() {
        return callActivityID;
    }

    public void setCallActivityID(String callActivityID) {
        this.callActivityID = callActivityID;
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

    public String getCallEndTime() {
        return callEndTime;
    }

    public void setCallEndTime(String callEndTime) {
        this.callEndTime = callEndTime;
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

}
