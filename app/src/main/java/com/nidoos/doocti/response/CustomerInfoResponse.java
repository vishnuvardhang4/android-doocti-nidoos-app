package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CustomerInfoResponse {
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

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }


    public class Datum {

        @SerializedName("leadID")
        @Expose
        private String leadID;
        @SerializedName("leadName")
        @Expose
        private String leadName;
        @SerializedName("customerEmail")
        @Expose
        private String customerEmail;
        @SerializedName("leadNumber")
        @Expose
        private String leadNumber;
        @SerializedName("leadStatus")
        @Expose
        private String leadStatus;
        @SerializedName("callStatus")
        @Expose
        private String callStatus;
        @SerializedName("module")
        @Expose
        private String module;
        @SerializedName("companyName")
        @Expose
        private String companyName;
        @SerializedName("ownerName")
        @Expose
        private String ownerName;
        @SerializedName("ownerID")
        @Expose
        private String ownerID;

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

        public String getCustomerEmail() {
            return customerEmail;
        }

        public void setCustomerEmail(String customerEmail) {
            this.customerEmail = customerEmail;
        }

        public String getLeadNumber() {
            return leadNumber;
        }

        public void setLeadNumber(String leadNumber) {
            this.leadNumber = leadNumber;
        }

        public String getLeadStatus() {
            return leadStatus;
        }

        public void setLeadStatus(String leadStatus) {
            this.leadStatus = leadStatus;
        }

        public String getCallStatus() {
            return callStatus;
        }

        public void setCallStatus(String callStatus) {
            this.callStatus = callStatus;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
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

    }
}
