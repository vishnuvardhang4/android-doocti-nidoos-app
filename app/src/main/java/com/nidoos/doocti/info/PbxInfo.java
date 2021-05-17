package com.nidoos.doocti.info;

import java.util.ArrayList;
import java.util.List;

public class PbxInfo {

    private String pauseCode;
    private List<String> pauseCodeArray;
    private List<Queue> queueArray;
    private List<String> didArray;
    private List<String> queueSelected;
    private String queueStatus;
    private String penalty;
    private Boolean queueShow;

    public static class Queue {
        private String name;
        private Integer code;

        public Queue(String name, Integer code) {
            this.name = name;
            this.code = code;
        }

        public Queue() {
            this.name = "";
            this.code = 0;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        @Override
        public String toString() {
            return "Queue{" +
                    "name='" + name + '\'' +
                    ", code=" + code +
                    '}';
        }
    }


    public PbxInfo(String pauseCode, List<String> pauseCodeArray, List<Queue> queueArray, List<String> didArray, List<String> queueSelected, String queueStatus, String penalty, Boolean queueShow) {
        this.pauseCode = pauseCode;
        this.pauseCodeArray = pauseCodeArray;
        this.queueArray = queueArray;
        this.didArray = didArray;
        this.queueSelected = queueSelected;
        this.queueStatus = queueStatus;
        this.penalty = penalty;
        this.queueShow = queueShow;
    }

    public PbxInfo() {

        this.pauseCode = "";
        this.pauseCodeArray = new ArrayList<>();
        this.queueArray = new ArrayList<>();
        this.didArray = new ArrayList<>();
        this.queueSelected = new ArrayList<>();
        this.queueStatus = "";
        this.penalty = "";
        this.queueShow = false;

    }

    public String getPauseCode() {
        return pauseCode;
    }

    public void setPauseCode(String pauseCode) {
        this.pauseCode = pauseCode;
    }

    public List<String> getPauseCodeArray() {
        return pauseCodeArray;
    }

    public void setPauseCodeArray(List<String> pauseCodeArray) {
        this.pauseCodeArray = pauseCodeArray;
    }

    public List<Queue> getQueueArray() {
        return queueArray;
    }

    public void setQueueArray(List<Queue> queueArray) {
        this.queueArray = queueArray;
    }

    public List<String> getDidArray() {
        return didArray;
    }

    public void setDidArray(List<String> didArray) {
        this.didArray = didArray;
    }

    public List<String> getQueueSelected() {
        return queueSelected;
    }

    public void setQueueSelected(List<String> queueSelected) {
        this.queueSelected = queueSelected;
    }

    public String getQueueStatus() {
        return queueStatus;
    }

    public void setQueueStatus(String queueStatus) {
        this.queueStatus = queueStatus;
    }

    public String getPenalty() {
        return penalty;
    }

    public void setPenalty(String penalty) {
        this.penalty = penalty;
    }

    public Boolean getQueueShow() {
        return queueShow;
    }

    public void setQueueShow(Boolean queueShow) {
        this.queueShow = queueShow;
    }

    @Override
    public String toString() {
        return "PbxInfo{" +
                "pauseCode='" + pauseCode + '\'' +
                ", pauseCodeArray=" + pauseCodeArray +
                ", queueArray=" + queueArray +
                ", didArray=" + didArray +
                ", queueSelected=" + queueSelected +
                ", queueStatus='" + queueStatus + '\'' +
                ", penalty='" + penalty + '\'' +
                ", queueShow=" + queueShow +
                '}';
    }
    public List<String> getQueueArrayNames() {

        List<String> queueArrayNames = new ArrayList<>();

        for(Queue q: this.queueArray) {
            queueArrayNames.add(q.getName());
        }

        return queueArrayNames;
    }
}
