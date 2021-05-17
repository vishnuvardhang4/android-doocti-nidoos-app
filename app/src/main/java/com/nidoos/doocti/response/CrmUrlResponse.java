package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CrmUrlResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("addUrls")
    @Expose
    private AddUrls addUrls;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public AddUrls getAddUrls() {
        return addUrls;
    }

    public void setAddUrls(AddUrls addUrls) {
        this.addUrls = addUrls;
    }


    public class AddUrls {

        @SerializedName("lead")
        @Expose
        private String lead;
        @SerializedName("contact")
        @Expose
        private String contact;

        public String getLead() {
            return lead;
        }

        public void setLead(String lead) {
            this.lead = lead;
        }

        public String getContact() {
            return contact;
        }

        public void setContact(String contact) {
            this.contact = contact;
        }

    }
}
