package com.nidoos.doocti.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateCampaignRequest {

    @SerializedName("campaign")
    @Expose
    private String campaign;

    public UpdateCampaignRequest(String campaign) {
        this.campaign = campaign;
    }



    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }
}
