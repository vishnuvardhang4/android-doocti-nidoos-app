package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimelineRequest {
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("customerID")
    @Expose
    private String customerID;
    public TimelineRequest(String phoneNumber,String customerID){
        this.customerID = customerID;
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
}
