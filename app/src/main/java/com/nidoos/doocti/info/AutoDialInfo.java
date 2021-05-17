package com.nidoos.doocti.info;

import java.util.List;

public class AutoDialInfo {

    private String status;
    private Integer skip;
    private Integer take;
    private Boolean moreData;
    private String mainFilterValue;
    private List<String> subFilterValue;
    private List<String> callStatusValue;

    public AutoDialInfo(String status, Integer skip, Integer take, Boolean moreData, String mainFilterValue, List<String> subFilterValue, List<String> callStatusValue) {
        this.status = status;
        this.skip = skip;
        this.take = take;
        this.moreData = moreData;
        this.mainFilterValue = mainFilterValue;
        this.subFilterValue = subFilterValue;
        this.callStatusValue = callStatusValue;
    }

    public AutoDialInfo() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getMainFilterValue() {
        return mainFilterValue;
    }

    public void setMainFilterValue(String mainFilterValue) {
        this.mainFilterValue = mainFilterValue;
    }

    public List<String> getSubFilterValue() {
        return subFilterValue;
    }

    public void setSubFilterValue(List<String> subFilterValue) {
        this.subFilterValue = subFilterValue;
    }

    public List<String> getCallStatusValue() {
        return callStatusValue;
    }

    public void setCallStatusValue(List<String> callStatusValue) {
        this.callStatusValue = callStatusValue;
    }

    @Override
    public String toString() {
        return "AutoDialInfo{" +
                "status='" + status + '\'' +
                ", skip=" + skip +
                ", take=" + take +
                ", moreData=" + moreData +
                ", mainFilterValue='" + mainFilterValue + '\'' +
                ", subFilterValue=" + subFilterValue +
                ", callStatusValue=" + callStatusValue +
                '}';
    }
}
