package com.nidoos.doocti.info;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    private String email;
    private String username;
    private String tenantId;
    private String extension;
    private String userId;
    private String profilePicture;
    private String token;
    private String status;
    private String crmDomain;
    private String ringApiUrl;
    private String ringApiParameters;
    private Boolean missed;



    private List<Queue> defaultUserQueue;



    private List<Queue> selectedUserQueue;




    public static class Queue {



        private String name;
        private Integer user_rank;



        public Queue(String name, Integer user_rank) {
            this.name = name;
            this.user_rank = user_rank;
        }

        public Queue() {
            this.name = "";
            this.user_rank = 2;
        }



        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getUser_rank() {
            return user_rank;
        }

        public void setUser_rank(Integer user_rank) {
            this.user_rank = user_rank;
        }

        @Override
        public String toString() {
            return "Queue{" +
                    "queue='" + name + '\'' +
                    ", userRank=" + user_rank +
                    '}';
        }
    }


    public UserInfo() {
        this.email = "";
        this.username = "";
        this.tenantId = "";
        this.extension = "";
        this.userId = "";
        this.profilePicture = "";
        this.token = "";
        this.status = "";
        this.crmDomain = "";
        this.ringApiUrl = "";
        this.ringApiParameters = "";
        this.missed = false;
        this.defaultUserQueue = new ArrayList<>();
        this.selectedUserQueue = new ArrayList<>();
    }

    public UserInfo(String email, String username, String tenantId, String extension, String userId, String profilePicture, String token, String status, String crmDomain, String ringApiUrl, String ringApiParameters, Boolean missed, List<Queue> defaultUserQueue, List<Queue> selectedUserQueue) {
        this.email = email;
        this.username = username;
        this.tenantId = tenantId;
        this.extension = extension;
        this.userId = userId;
        this.profilePicture = profilePicture;
        this.token = token;
        this.status = status;
        this.crmDomain = crmDomain;
        this.ringApiUrl = ringApiUrl;
        this.ringApiParameters = ringApiParameters;
        this.missed = missed;
        this.defaultUserQueue = defaultUserQueue;
        this.selectedUserQueue = selectedUserQueue;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCrmDomain() {
        return crmDomain;
    }

    public void setCrmDomain(String crmDomain) {
        this.crmDomain = crmDomain;
    }

    public String getRingApiUrl() {
        return ringApiUrl;
    }

    public void setRingApiUrl(String ringApiUrl) {
        this.ringApiUrl = ringApiUrl;
    }

    public String getRingApiParameters() {
        return ringApiParameters;
    }

    public void setRingApiParameters(String ringApiParameters) {
        this.ringApiParameters = ringApiParameters;
    }

    public Boolean getMissed() {
        return missed;
    }

    public void setMissed(Boolean missed) {
        this.missed = missed;
    }

    public List<Queue> getDefaultUserQueue() {
        return defaultUserQueue;
    }

    public void setDefaultUserQueue(List<Queue> defaultUserQueue) {
        this.defaultUserQueue = defaultUserQueue;
    }

    public List<Queue> getSelectedUserQueue() {
        return selectedUserQueue;
    }

    public void setSelectedUserQueue(List<Queue> selectedUserQueue) {
        this.selectedUserQueue = selectedUserQueue;
    }

    public List<String> getDefaultUserQueueNames() {

        List<String> userQueueNames = new ArrayList<>();

        for(UserInfo.Queue q: this.getDefaultUserQueue()) {
            userQueueNames.add(q.getName());
        }

        return userQueueNames;
    }

    public List<String> getSelectedUserQueueNames() {

        List<String> userQueueNames = new ArrayList<>();

        for(UserInfo.Queue q: this.getSelectedUserQueue()) {
            userQueueNames.add(q.getName());
        }

        return userQueueNames;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", tenantId='" + tenantId + '\'' +
                ", extension='" + extension + '\'' +
                ", userId='" + userId + '\'' +
                ", profilePicture='" + profilePicture + '\'' +
                ", token='" + token + '\'' +
                ", status='" + status + '\'' +
                ", crmDomain='" + crmDomain + '\'' +
                ", ringApiUrl='" + ringApiUrl + '\'' +
                ", ringApiParameters='" + ringApiParameters + '\'' +
                ", missed=" + missed +
                ", defaultUserQueue=" + defaultUserQueue +
                ", selectedUserQueue=" + selectedUserQueue +
                '}';
    }
}
