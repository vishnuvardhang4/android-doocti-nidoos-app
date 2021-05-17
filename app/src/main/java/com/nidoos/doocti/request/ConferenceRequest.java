package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ConferenceRequest {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("name")
    @Expose
    private String name;

    public ConferenceRequest(String email, String username) {
        this.name = username;
        this.user = email;
    }


    public String getEmail() {
        return user;
    }

    public void setEmail(String agent) {
        this.user = user;
    }

    public String getAgent_name() {
        return name;
    }

    public void setAgent_name(String callStatus) {
        this.name = name;
    }
}
