package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallLogResponse {
    @SerializedName("response")
    @Expose
    private String response;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Datum {

        @SerializedName("calldate")
        @Expose
        private String calldate;
        @SerializedName("phone_number")
        @Expose
        private String phoneNumber;
        @SerializedName("duration")
        @Expose
        private String duration;
        @SerializedName("call_type")
        @Expose
        private String callType;
        @SerializedName("call_status")
        @Expose
        private String callStatus;
        @SerializedName("uniqueid")
        @Expose
        private String uniqueid;
        @SerializedName("queue")
        @Expose
        private String queue;
        @SerializedName("dispo_status")
        @Expose
        private String dispoStatus;

        public String getCalldate() {
            return calldate;
        }

        public void setCalldate(String calldate) {
            this.calldate = calldate;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getCallType() {
            return callType;
        }

        public void setCallType(String callType) {
            this.callType = callType;
        }

        public String getCallStatus() {
            return callStatus;
        }

        public void setCallStatus(String callStatus) {
            this.callStatus = callStatus;
        }

        public String getUniqueid() {
            return uniqueid;
        }

        public void setUniqueid(String uniqueid) {
            this.uniqueid = uniqueid;
        }

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public String getDispoStatus() {
            return dispoStatus;
        }

        public void setDispoStatus(String dispoStatus) {
            this.dispoStatus = dispoStatus;
        }

    }
}
