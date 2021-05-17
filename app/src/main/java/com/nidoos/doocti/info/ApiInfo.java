package com.nidoos.doocti.info;

public class ApiInfo {
    private String dialerDomain;
    private String apiDomain;
    private String socketDomain;


    public ApiInfo(String dialerDomain, String apiDomain, String socketDomain) {
        this.dialerDomain = dialerDomain;
        this.apiDomain = apiDomain;
        this.socketDomain = socketDomain;
    }

    public ApiInfo() {
    }



    public String getDialerDomain() {
        return dialerDomain;
    }

    public void setDialerDomain(String dialerDomain) {
        this.dialerDomain = dialerDomain;
    }

    public String getApiDomain() {
        return apiDomain;
    }

    public void setApiDomain(String apiDomain) {
        this.apiDomain = apiDomain;
    }

    public String getSocketDomain() {
        return socketDomain;
    }

    public void setSocketDomain(String socketDomain) {
        this.socketDomain = socketDomain;
    }

    @Override
    public String toString() {
        return "ApiInfo{" +
                "dialerDomain='" + dialerDomain + '\'' +
                ", apiDomain='" + apiDomain + '\'' +
                ", socketDomain='" + socketDomain + '\'' +
                '}';
    }
}
