package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ConferenceResponse {

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private List<ConferenceResponse.Datum> data = null;

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public static class Datum{

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("user")
        @Expose
        private String user;
        @SerializedName("station")
        @Expose
        private String station;
        @SerializedName("queue")
        @Expose
        private String queue;
        @SerializedName("status")
        @Expose
        private String status;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getStation(){ return station; }

        public void setStation(String station){ this.station = station; }

        public String getQueue(){ return queue; }

        public void setQueue(String queue){ this.queue = queue; }

        public String getStatus(){ return status; }

        public void setStatus(String status){ this.status = status; }

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
}
