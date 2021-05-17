package com.nidoos.doocti.info;

public class CustomerInfo {
    private String name;
    private String company;
    private String description;
    private String phoneNumber;
    private String leadId;
    private String module;
    private String crmUrl;
    private String addLeadUrl;
    private String email;
    private String ownerId;

    public CustomerInfo(String name, String company, String description, String phoneNumber, String leadId, String module, String crmUrl, String addLeadUrl, String email, String ownerId) {
        this.name = name;
        this.company = company;
        this.description = description;
        this.phoneNumber = phoneNumber;
        this.leadId = leadId;
        this.module = module;
        this.crmUrl = crmUrl;
        this.addLeadUrl = addLeadUrl;
        this.email = email;
        this.ownerId = ownerId;
    }

    public CustomerInfo() {
        this.name = "";
        this.company = "";
        this.description = "";
        this.phoneNumber = "";
        this.leadId = "";
        this.module = "";
        this.crmUrl = "";
        this.addLeadUrl = "";
        this.email = "";
        this.ownerId = "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getCrmUrl() {
        return crmUrl;
    }

    public void setCrmUrl(String crmUrl) {
        this.crmUrl = crmUrl;
    }

    public String getAddLeadUrl() {
        return addLeadUrl;
    }

    public void setAddLeadUrl(String addLeadUrl) {
        this.addLeadUrl = addLeadUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String toString() {
        return "CustomerInfo{" +
                "name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", description='" + description + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", leadId='" + leadId + '\'' +
                ", module='" + module + '\'' +
                ", crmUrl='" + crmUrl + '\'' +
                ", addLeadUrl='" + addLeadUrl + '\'' +
                ", email='" + email + '\'' +
                ", ownerId='" + ownerId + '\'' +
                '}';
    }
}
