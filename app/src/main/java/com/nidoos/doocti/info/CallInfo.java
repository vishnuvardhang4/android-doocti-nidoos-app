package com.nidoos.doocti.info;

public class CallInfo {

    private String event;
    private String callType;
    private String uniqueId;
    private String phoneNumber;
    private String startTime;
    private String agentChannel;
    private String callerChannel;
    private String answerTime;
    private String endTime;

    public CallInfo(String event, String callType, String uniqueId, String phoneNumber, String startTime, String agentChannel, String callerChannel, String answerTime, String endTime) {
        this.event = event;
        this.callType = callType;
        this.uniqueId = uniqueId;
        this.phoneNumber = phoneNumber;
        this.startTime = startTime;
        this.agentChannel = agentChannel;
        this.callerChannel = callerChannel;
        this.answerTime = answerTime;
        this.endTime = endTime;
    }

    public CallInfo() {
        this.event = "";
        this.callType = "";
        this.uniqueId = "";
        this.phoneNumber = "";
        this.startTime = "";
        this.agentChannel = "";
        this.callerChannel = "";
        this.answerTime = "";
        this.endTime = "";
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getAgentChannel() {
        return agentChannel;
    }

    public void setAgentChannel(String agentChannel) {
        this.agentChannel = agentChannel;
    }

    public String getCallerChannel() {
        return callerChannel;
    }

    public void setCallerChannel(String callerChannel) {
        this.callerChannel = callerChannel;
    }

    public String getAnswerTime() {
        return answerTime;
    }

    public void setAnswerTime(String answerTime) {
        this.answerTime = answerTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "CallInfo{" +
                "event='" + event + '\'' +
                ", callType='" + callType + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", startTime='" + startTime + '\'' +
                ", agentChannel='" + agentChannel + '\'' +
                ", callerChannel='" + callerChannel + '\'' +
                ", answerTime='" + answerTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
