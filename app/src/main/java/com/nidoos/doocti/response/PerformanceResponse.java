package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PerformanceResponse {
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data")
    @Expose
    private List<PerformanceResponse.Datum> data = null;


    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
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

        @SerializedName("idle")
        @Expose
        private String idle;

        @SerializedName("calldate")
        @Expose
        private String calldate;

        @SerializedName("avg_talk")
        @Expose
        private String avgTalk;

        @SerializedName("start_time")
        @Expose
        private String startTime;

        @SerializedName("end_time")
        @Expose
        private String endTime;

        @SerializedName("campaign")
        @Expose
        private String campaign;

        @SerializedName("queue")
        @Expose
        private String queue;

        @SerializedName("agent")
        @Expose
        private String agent;

        @SerializedName("answer")
        @Expose
        private Integer answer;

        @SerializedName("unanswer")
        @Expose
        private Integer unanswer;

        public String getIdle() {
            return idle;
        }

        public void setIdle(String idle) {
            this.idle = idle;
        }

        public String getCalldate() {
            return calldate;
        }

        public void setCalldate(String calldate) {
            this.calldate = calldate;
        }

        public String getAvgTalk() {
            return avgTalk;
        }

        public void setAvgTalk(String avgTalk) {
            this.avgTalk = avgTalk;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getCampaign() {
            return campaign;
        }

        public void setCampaign(String campaign) {
            this.campaign = campaign;
        }

        public String getQueue() {
            return queue;
        }

        public void setQueue(String queue) {
            this.queue = queue;
        }

        public String getAgent() {
            return agent;
        }

        public void setAgent(String agent) {
            this.agent = agent;
        }

        public Integer getAnswer() {
            return answer;
        }

        public void setAnswer(Integer answer) {
            this.answer = answer;
        }

        public Integer getUnanswer() {
            return unanswer;
        }

        public void setUnanswer(Integer unanswer) {
            this.unanswer = unanswer;
        }
    }

}
