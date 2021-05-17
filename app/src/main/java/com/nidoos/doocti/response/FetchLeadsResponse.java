package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FetchLeadsResponse {
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


    public static class Datum {

        @SerializedName("leadID")
        @Expose
        private String leadID;
        @SerializedName("leadName")
        @Expose
        private String leadName;
        @SerializedName("leadNumber")
        @Expose
        private String leadNumber;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("ownerName")
        @Expose
        private String ownerName;
        @SerializedName("ownerID")
        @Expose
        private String ownerID;
        @SerializedName("id")
        @Expose
        private String id;

        public Datum(String id,String leadID, String name, String number, String status) {
            this.id = id;
            this.leadID = leadID;
            this.leadName = name;
            this.leadNumber = number;
            this.status = status;
        }

        public String getLeadID() {
            return leadID;
        }

        public void setLeadID(String leadID) {
            this.leadID = leadID;
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOwnerName() {
            return ownerName;
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


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }


    }
}
