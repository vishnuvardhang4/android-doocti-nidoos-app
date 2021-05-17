package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FetchLeadsRequest {

    @SerializedName("userID")
    @Expose
    private String userID;
    @SerializedName("mainFiltervalue")
    @Expose
    private String mainFiltervalue;
    @SerializedName("subFilterValue")
    @Expose
    private List<SubFilterValue> subFilterValue = null;
    @SerializedName("callStatus")
    @Expose
    private List<Callstatus> callStatus = null;
    @SerializedName("skip")
    @Expose
    private Integer skip;
    @SerializedName("take")
    @Expose
    private Integer take;

    public FetchLeadsRequest(String userID, int skip, int take, String mainFilterValue, List<SubFilterValue> subFilterValues, List<Callstatus> callStatus) {
        this.userID = userID;
        this.skip = skip;
        this.take=take;
        this.mainFiltervalue = mainFilterValue;
        this.subFilterValue = subFilterValues;
        this.callStatus = callStatus;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMainFiltervalue() {
        return mainFiltervalue;
    }

    public void setMainFiltervalue(String mainFiltervalue) {
        this.mainFiltervalue = mainFiltervalue;
    }

    public List<SubFilterValue> getSubFilterValue() {
        return subFilterValue;
    }

    public void setSubFilterValue(List<SubFilterValue> subFilterValue) {
        this.subFilterValue = subFilterValue;
    }

    public List<Callstatus> getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(List<Callstatus> callStatus) {
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

    public static class SubFilterValue {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("value")
        @Expose
        private String value;

        public SubFilterValue(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

    public static class Callstatus {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("value")
        @Expose
        private String value;

        public Callstatus(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

    }

}
