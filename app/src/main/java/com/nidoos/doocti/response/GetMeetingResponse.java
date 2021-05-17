package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetMeetingResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("totalCount")
    @Expose
    private Integer totalCount;
    @SerializedName("skip")
    @Expose
    private Integer skip;
    @SerializedName("take")
    @Expose
    private Integer take;
    @SerializedName("moreData")
    @Expose
    private Boolean moreData;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

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

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
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

    public Boolean getMoreData() {
        return moreData;
    }

    public void setMoreData(Boolean moreData) {
        this.moreData = moreData;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }


    public class Datum {

        @SerializedName("meetingID")
        @Expose
        private String meetingID;
        @SerializedName("scheduledTime")
        @Expose
        private String scheduledTime;
        @SerializedName("subject")
        @Expose
        private String subject;
        @SerializedName("leadModule")
        @Expose
        private String leadModule;
        @SerializedName("leadName")
        @Expose
        private String leadName;
        @SerializedName("phoneNumber")
        private String phoneNumber;
        @SerializedName("leadNumber")
        @Expose
        private String leadNumber;
        @SerializedName("ownerName")
        @Expose
        private String ownerName;
        @SerializedName("ownerID")
        @Expose
        private String ownerID;

        public String getMeetingID() {
            return meetingID;
        }

        public void setMeetingID(String meetingID) {
            this.meetingID = meetingID;
        }

        public String getScheduledTime() {
            return scheduledTime;
        }

        public void setScheduledTime(String scheduledTime) {
            this.scheduledTime = scheduledTime;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getLeadModule() {
            return leadModule;
        }

        public void setLeadModule(String leadModule) {
            this.leadModule = leadModule;
        }

        public String getLeadName() {
            return leadName;
        }

        public void setLeadName(String leadName) {
            this.leadName = leadName;
        }

        public String getLeadNumber() {
            return leadNumber;
        }

        public void setLeadNumber(String leadNumber) {
            this.leadNumber = leadNumber;
        }

        public String getOwnerName() {
            return ownerName;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public void setOwnerName(String ownerName) {
            this.ownerName = ownerName;
        }

        public String getOwnerID() {
            return ownerID;
        }

        public void setOwnerID(String ownerID) {
            this.ownerID = ownerID;
        }

    }
}
