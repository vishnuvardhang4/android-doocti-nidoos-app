package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CallLogRequest {

    @SerializedName("agent")
    @Expose
    private String agent;
    @SerializedName("call_status")
    @Expose
    private String callStatus;
    @SerializedName("skip")
    @Expose
    private Integer skip;
    @SerializedName("take")
    @Expose
    private Integer take;

    public CallLogRequest(String email, String call_log_filter, int skip, int take) {
        this.agent = email;
        this.callStatus = call_log_filter;
        this.skip = skip;
        this.take = take;
    }

    public String getAgent() {
        return agent;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String callStatus) {
        this.callStatus = callStatus;
    }

    public Integer getSkip() {
        return skip;
    }

    public void setSkip(Integer skip) {
        this.skip = skip;
    }

    public Integer getTake() {
        return take;
    }

    public void setTake(Integer take) {
        this.take = take;
    }
}
