package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallCountRequest {
    @SerializedName("agent")
    @Expose
    private String agent;
    @SerializedName("extension")
    @Expose
    private String extension;

    public CallCountRequest(String email, String extension) {
        this.agent = email;
        this.extension = extension;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
