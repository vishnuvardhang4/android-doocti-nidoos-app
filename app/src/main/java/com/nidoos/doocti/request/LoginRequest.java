package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginRequest {

    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("station")
    @Expose
    private String station;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("avatar_url")
    @Expose
    private String avatarUrl;
    @SerializedName("agent_id")
    @Expose
    private String agentID;


    public LoginRequest(String username, String email, String extension, String url, String agent_id) {
        this.user = email;
        this.name = username;
        this.station = extension;
        this.avatarUrl = url;
        this.agentID = agent_id;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }


}
