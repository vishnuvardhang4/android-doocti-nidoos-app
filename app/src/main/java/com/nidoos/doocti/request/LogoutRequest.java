package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LogoutRequest {
    @SerializedName("user")
    @Expose
    private String user;

    public LogoutRequest(String name) {
        this.user = name;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
