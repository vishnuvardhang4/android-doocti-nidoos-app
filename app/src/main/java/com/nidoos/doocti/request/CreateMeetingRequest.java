package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateMeetingRequest {
    @SerializedName("meetingTilte")
    @Expose
    private String meetingTilte;
    @SerializedName("meetingTime")
    @Expose
    private String meetingTime;
    @SerializedName("ownerID")
    @Expose
    private String ownerID;
    @SerializedName("ownerName")
    @Expose
    private String ownerName;
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("leadID")
    @Expose
    private String leadID;
    @SerializedName("leadModule")
    @Expose
    private String leadModule;

    public CreateMeetingRequest(String meeting_title, String meeting_date, String userID, String username, String phone_number, String leadID, String module) {
        this.meetingTilte = meeting_title;
        this.meetingTime = meeting_date;
        this.ownerID= userID;
        this.ownerName = username;
        this.phoneNumber = phone_number;
        this.leadID =leadID;
        this.leadModule = module;
    }

    public String getMeetingTilte() {
        return meetingTilte;
    }

    public void setMeetingTilte(String meetingTilte) {
        this.meetingTilte = meetingTilte;
    }

    public String getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(String meetingTime) {
        this.meetingTime = meetingTime;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLeadID() {
        return leadID;
    }

    public void setLeadID(String leadID) {
        this.leadID = leadID;
    }

    public String getLeadModule() {
        return leadModule;
    }

    public void setLeadModule(String leadModule) {
        this.leadModule = leadModule;
    }
}
