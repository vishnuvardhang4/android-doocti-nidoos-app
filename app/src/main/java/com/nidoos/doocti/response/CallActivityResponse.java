package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class
CallActivityResponse {
    @SerializedName("responseCode")
    @Expose
    private Integer responseCode;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("callActivityID")
    @Expose
    private String callActivityID;

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCallActivityID() {
        return callActivityID;
    }

    public void setCallActivityID(String callActivityID) {
        this.callActivityID = callActivityID;
    }

}
