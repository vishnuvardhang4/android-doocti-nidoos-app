package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoginResponse {

    @SerializedName("response")
    @Expose
    private String response;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("queue")
    @Expose
    private List<Queue> queue = null;
    @SerializedName("pause_code")
    @Expose
    private List<String> pauseCode = null;
    @SerializedName("did_no")
    @Expose
    private List<String> didNo = null;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<Queue> getQueue() {
        return queue;
    }

    public void setQueue(List<Queue> queue) {
        this.queue = queue;
    }

    public List<String> getPauseCode() {
        return pauseCode;
    }

    public void setPauseCode(List<String> pauseCode) {
        this.pauseCode = pauseCode;
    }

    public List<String> getDidNo() {
        return didNo;
    }

    public void setDidNo(List<String> didNo) {
        this.didNo = didNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Queue {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("code")
        @Expose
        private Integer code;

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

    }

    public class Data {

        @SerializedName("avatar_url")
        @Expose
        private String avatarUrl;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("station")
        @Expose
        private String station;
        @SerializedName("user")
        @Expose
        private String user;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("id")
        @Expose
        private Integer id;

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStation() {
            return station;
        }

        public void setStation(String station) {
            this.station = station;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

    }

}
