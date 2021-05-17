package com.nidoos.doocti.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CrmInfoResponse {
    @SerializedName("tenant_code")
    @Expose
    private String tenantCode;
    @SerializedName("projects")
    @Expose
    private List<Project> projects = null;

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public class Project {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("company_name")
        @Expose
        private String companyName;
        @SerializedName("tenant_code")
        @Expose
        private String tenantCode;
        @SerializedName("tenant_id")
        @Expose
        private Integer tenantId;
        @SerializedName("tenant_name")
        @Expose
        private String tenantName;
        @SerializedName("service_id")
        @Expose
        private Integer serviceId;
        @SerializedName("service_name")
        @Expose
        private String serviceName;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("created_by")
        @Expose
        private Integer createdBy;
        @SerializedName("updated_by")
        @Expose
        private Integer updatedBy;
        @SerializedName("last_start_time")
        @Expose
        private Object lastStartTime;
        @SerializedName("last_end_time")
        @Expose
        private Object lastEndTime;
        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("amount")
        @Expose
        private Object amount;
        @SerializedName("config")
        @Expose
        private List<Config> config = null;
        @SerializedName("userInfo")
        @Expose
        private List<UserInfo> userInfo = null;
        @SerializedName("userPermission")
        @Expose
        private UserPermission userPermission;

        @SerializedName("queue")
        @Expose
        private List<Queue> queue;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getTenantCode() {
            return tenantCode;
        }

        public void setTenantCode(String tenantCode) {
            this.tenantCode = tenantCode;
        }

        public Integer getTenantId() {
            return tenantId;
        }

        public void setTenantId(Integer tenantId) {
            this.tenantId = tenantId;
        }

        public String getTenantName() {
            return tenantName;
        }

        public void setTenantName(String tenantName) {
            this.tenantName = tenantName;
        }

        public Integer getServiceId() {
            return serviceId;
        }

        public void setServiceId(Integer serviceId) {
            this.serviceId = serviceId;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public Integer getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(Integer updatedBy) {
            this.updatedBy = updatedBy;
        }

        public Object getLastStartTime() {
            return lastStartTime;
        }

        public void setLastStartTime(Object lastStartTime) {
            this.lastStartTime = lastStartTime;
        }

        public Object getLastEndTime() {
            return lastEndTime;
        }

        public void setLastEndTime(Object lastEndTime) {
            this.lastEndTime = lastEndTime;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Object getAmount() {
            return amount;
        }

        public void setAmount(Object amount) {
            this.amount = amount;
        }

        public List<Config> getConfig() {
            return config;
        }

        public void setConfig(List<Config> config) {
            this.config = config;
        }

        public List<UserInfo> getUserInfo() {
            return userInfo;
        }

        public void setUserInfo(List<UserInfo> userInfo) {
            this.userInfo = userInfo;
        }

        public UserPermission getUserPermission() {
            return userPermission;
        }

        public void setUserPermission(UserPermission userPermission) {
            this.userPermission = userPermission;
        }


        public List<Queue> getQueue() {
            return queue;
        }

        public void setQueue(List<Queue> queue) {
            this.queue = queue;
        }
    }

    public static class UserInfo {

        @SerializedName("tenant_code")
        @Expose
        private String tenantCode;
        @SerializedName("user_phone_number")
        @Expose
        private String userPhoneNumber;
        @SerializedName("project_id")
        @Expose
        private Integer projectId;
        @SerializedName("user_id")
        @Expose
        private String userId;
        @SerializedName("user_email")
        @Expose
        private String userEmail;
        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("created_at")
        @Expose
        private Object createdAt;
        @SerializedName("updated_at")
        @Expose
        private Object updatedAt;
        @SerializedName("user_name")
        @Expose
        private String userName;

        @SerializedName("queue")
        @Expose
        private List<UserInfo.Queue> queue;



        public class Queue {


            @SerializedName("queue")
            @Expose
            private String queue;

            @SerializedName("user_rank")
            @Expose
            private Integer userRank;

            public String getQueue() {
                return queue;
            }

            public void setQueue(String queue) {
                this.queue = queue;
            }

            public Integer getUserRank() {
                return userRank;
            }

            public void setUserRank(Integer userRank) {
                this.userRank = userRank;
            }

        }



        public String getTenantCode() {
            return tenantCode;
        }

        public void setTenantCode(String tenantCode) {
            this.tenantCode = tenantCode;
        }

        public String getUserPhoneNumber() {
            return userPhoneNumber;
        }

        public void setUserPhoneNumber(String userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
        }

        public Integer getProjectId() {
            return projectId;
        }

        public void setProjectId(Integer projectId) {
            this.projectId = projectId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Object getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(Object createdAt) {
            this.createdAt = createdAt;
        }

        public Object getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(Object updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public List<Queue> getQueue() {
            return queue;
        }

        public void setQueue(List<Queue> queue) {
            this.queue = queue;
        }

    }

    public class UserPermission {

        @SerializedName("queue")
        @Expose
        private Boolean queue;
        @SerializedName("showStatusChange")
        @Expose
        private Boolean showStatusChange;
        @SerializedName("agent_status")
        @Expose
        private Boolean agentStatus;
        @SerializedName("view_dialer_screen")
        @Expose
        private Boolean viewDialerScreen;
        @SerializedName("view_call_log")
        @Expose
        private Boolean viewCallLog;
        @SerializedName("auto_dial")
        @Expose
        private Boolean autoDial;
        @SerializedName("click_to_call")
        @Expose
        private Boolean clickToCall;
        @SerializedName("recording_play")
        @Expose
        private Boolean recordingPlay;
        @SerializedName("view_contacts")
        @Expose
        private Boolean viewContacts;
        @SerializedName("view_meeting")
        @Expose
        private Boolean viewMeeting;
        @SerializedName("create_meeting")
        @Expose
        private Boolean createMeeting;
        @SerializedName("create_note")
        @Expose
        private Boolean createNote;
        @SerializedName("view_timeline")
        @Expose
        private Boolean viewTimeline;
        @SerializedName("view_user_info")
        @Expose
        private Boolean viewUserInfo;
        @SerializedName("crm_enable")
        @Expose
        private Boolean crmEnable;
        @SerializedName("popup")
        @Expose
        private Boolean popup;
        @SerializedName("crm_tab")
        @Expose
        private Boolean crmTab;
        @SerializedName("view_park")
        @Expose
        private Boolean viewPark;
        @SerializedName("view_transfer")
        @Expose
        private Boolean viewTransfer;
        @SerializedName("agent_transfer")
        @Expose
        private Boolean agentTransfer;
        @SerializedName("agent_tranfer_status")
        @Expose
        private Boolean agentTranferStatus;
        @SerializedName("agent_tranfer_call")
        @Expose
        private Boolean agentTranferCall;
        @SerializedName("queue_transfer")
        @Expose
        private Boolean queueTransfer;
        @SerializedName("other_transfer")
        @Expose
        private Boolean otherTransfer;
        @SerializedName("did_tranfer")
        @Expose
        private Boolean didTranfer;
        @SerializedName("auto_dispo")
        @Expose
        private Boolean autoDispo;
        @SerializedName("agent_conference")
        @Expose
        private Boolean agent_conference;
        @SerializedName("enable_conference")
        @Expose
        private Boolean enable_conference;
        @SerializedName("auto_dial_main_filter")
        @Expose
        private Boolean auto_dial_main_filter;
        @SerializedName("auto_dial_sub_filter")
        @Expose
        private Boolean auto_dial_sub_filter;
        @SerializedName("auto_dial_status_filter")
        @Expose
        private Boolean auto_dial_status_filter;
        @SerializedName("iframe_popup")
        @Expose
        private Boolean iframe_popup;
        @SerializedName("no_dispo")
        @Expose
        private Boolean no_dispo;
        @SerializedName("crm_api")
        @Expose
        private Boolean crm_api;
        @SerializedName("waiting_calls")
        @Expose
        private Boolean waiting_calls;

        public Boolean getQueue() {
            return queue;
        }

        public void setShowStatusChange(Boolean showStatusChange) {
            this.showStatusChange = showStatusChange;
        }

        public Boolean getShowStatusChange() {
            return showStatusChange;
        }

        public void setQueue(Boolean queue) {
            this.queue = queue;
        }

        public Boolean getAgentStatus() {
            return agentStatus;
        }

        public void setAgentStatus(Boolean agentStatus) {
            this.agentStatus = agentStatus;
        }

        public Boolean getViewDialerScreen() {
            return viewDialerScreen;
        }

        public void setViewDialerScreen(Boolean viewDialerScreen) {
            this.viewDialerScreen = viewDialerScreen;
        }

        public Boolean getViewCallLog() {
            return viewCallLog;
        }

        public void setViewCallLog(Boolean viewCallLog) {
            this.viewCallLog = viewCallLog;
        }

        public Boolean getAutoDial() {
            return autoDial;
        }

        public void setAutoDial(Boolean autoDial) {
            this.autoDial = autoDial;
        }

        public Boolean getClickToCall() {
            return clickToCall;
        }

        public void setClickToCall(Boolean clickToCall) {
            this.clickToCall = clickToCall;
        }

        public Boolean getRecordingPlay() {
            return recordingPlay;
        }

        public void setRecordingPlay(Boolean recordingPlay) {
            this.recordingPlay = recordingPlay;
        }

        public Boolean getViewContacts() {
            return viewContacts;
        }

        public void setViewContacts(Boolean viewContacts) {
            this.viewContacts = viewContacts;
        }

        public Boolean getViewMeeting() {
            return viewMeeting;
        }

        public void setViewMeeting(Boolean viewMeeting) {
            this.viewMeeting = viewMeeting;
        }

        public Boolean getCreateMeeting() {
            return createMeeting;
        }

        public void setCreateMeeting(Boolean createMeeting) {
            this.createMeeting = createMeeting;
        }

        public Boolean getCreateNote() {
            return createNote;
        }

        public void setCreateNote(Boolean createNote) {
            this.createNote = createNote;
        }

        public Boolean getViewTimeline() {
            return viewTimeline;
        }

        public void setViewTimeline(Boolean viewTimeline) {
            this.viewTimeline = viewTimeline;
        }

        public Boolean getViewUserInfo() {
            return viewUserInfo;
        }

        public void setViewUserInfo(Boolean viewUserInfo) {
            this.viewUserInfo = viewUserInfo;
        }

        public Boolean getCrmEnable() {
            return crmEnable;
        }

        public void setCrmEnable(Boolean crmEnable) {
            this.crmEnable = crmEnable;
        }

        public Boolean getPopup() {
            return popup;
        }

        public void setPopup(Boolean popup) {
            this.popup = popup;
        }

        public Boolean getCrmTab() {
            return crmTab;
        }

        public void setCrmTab(Boolean crmTab) {
            this.crmTab = crmTab;
        }

        public Boolean getViewPark() {
            return viewPark;
        }

        public void setViewPark(Boolean viewPark) {
            this.viewPark = viewPark;
        }

        public Boolean getViewTransfer() {
            return viewTransfer;
        }

        public void setViewTransfer(Boolean viewTransfer) {
            this.viewTransfer = viewTransfer;
        }

        public Boolean getAgentTransfer() {
            return agentTransfer;
        }

        public void setAgentTransfer(Boolean agentTransfer) {
            this.agentTransfer = agentTransfer;
        }

        public Boolean getAgentTranferStatus() {
            return agentTranferStatus;
        }

        public void setAgentTranferStatus(Boolean agentTranferStatus) {
            this.agentTranferStatus = agentTranferStatus;
        }

        public Boolean getAgentTranferCall() {
            return agentTranferCall;
        }

        public void setAgentTranferCall(Boolean agentTranferCall) {
            this.agentTranferCall = agentTranferCall;
        }

        public Boolean getQueueTransfer() {
            return queueTransfer;
        }

        public void setQueueTransfer(Boolean queueTransfer) {
            this.queueTransfer = queueTransfer;
        }

        public Boolean getOtherTransfer() {
            return otherTransfer;
        }

        public void setOtherTransfer(Boolean otherTransfer) {
            this.otherTransfer = otherTransfer;
        }

        public Boolean getDidTranfer() {
            return didTranfer;
        }

        public void setDidTranfer(Boolean didTranfer) {
            this.didTranfer = didTranfer;
        }

        public Boolean getAutoDispo() {
            return autoDispo;
        }

        public void setAutoDispo(Boolean autoDispo) {
            this.autoDispo = autoDispo;
        }

        public Boolean getAgentConference() {
            return agent_conference;
        }

        public void setAgentConference(Boolean agent_conference) {
            this.agent_conference = agent_conference;
        }

        public Boolean getEnableConference() {
            return enable_conference;
        }

        public void setEnableConference(Boolean enable_conference) {
            this.enable_conference = enable_conference;
        }

        public Boolean getAutoDialMainFilter() {
            return auto_dial_main_filter;
        }

        public void setAutoDialMainFilter(Boolean auto_dial_main_filter) {
            this.auto_dial_main_filter = auto_dial_main_filter;
        }

        public Boolean getAutoDialSubFilter() {
            return auto_dial_sub_filter;
        }

        public void setAutoDialSubFilter(Boolean auto_dial_sub_filter) {
            this.auto_dial_sub_filter = auto_dial_sub_filter;
        }

        public Boolean getAutoDialStatusFilter() {
            return auto_dial_status_filter;
        }

        public void setAutoDialStatusFilter(Boolean auto_dial_status_filter) {
            this.auto_dial_status_filter = auto_dial_status_filter;
        }

        public Boolean getIframePopup() {
            return iframe_popup;
        }

        public void setIframePopup(Boolean iframe_popup) {
            this.iframe_popup = iframe_popup;
        }

        public Boolean getNoDispo() {
            return no_dispo;
        }

        public void setNoDispo(Boolean no_dispo) {
            this.no_dispo = no_dispo;
        }

        public Boolean getCrmApi() {
            return crm_api;
        }

        public void setCrmApi(Boolean crm_api) {
            this.crm_api = crm_api;
        }

        public Boolean getWaitingCalls() {
            return waiting_calls;
        }

        public void setWaitingCalls(Boolean waiting_calls) {
            this.waiting_calls = waiting_calls;
        }

    }

    public class Config {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("env")
        @Expose
        private String env;
        @SerializedName("project_id")
        @Expose
        private Integer projectId;
        @SerializedName("project_key")
        @Expose
        private String projectKey;
        @SerializedName("project_version")
        @Expose
        private String projectVersion;
        @SerializedName("project_value")
        @Expose
        private String projectValue;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("created_by")
        @Expose
        private Integer createdBy;
        @SerializedName("updated_by")
        @Expose
        private Integer updatedBy;
        @SerializedName("enabled")
        @Expose
        private Boolean enabled;
        @SerializedName("sticky_agent")
        @Expose
        private Object stickyAgent;
        @SerializedName("did_number")
        @Expose
        private String didNumber;
        @SerializedName("ring_api_url")
        @Expose
        private Object ringApiUrl;
        @SerializedName("ring_api_parameter")
        @Expose
        private Object ringApiParameter;
        @SerializedName("ans_api_url")
        @Expose
        private Object ansApiUrl;
        @SerializedName("ans_api_parameter")
        @Expose
        private Object ansApiParameter;
        @SerializedName("cdr_api_url")
        @Expose
        private Object cdrApiUrl;
        @SerializedName("cdr_api_parameter")
        @Expose
        private Object cdrApiParameter;
        @SerializedName("get_agent_api")
        @Expose
        private Object getAgentApi;
        @SerializedName("get_agent_parameter")
        @Expose
        private Object getAgentParameter;
        @SerializedName("ring_api_method")
        @Expose
        private Object ringApiMethod;
        @SerializedName("ans_api_method")
        @Expose
        private Object ansApiMethod;
        @SerializedName("cdr_api_method")
        @Expose
        private Object cdrApiMethod;
        @SerializedName("ring_api_type")
        @Expose
        private Object ringApiType;
        @SerializedName("ans_api_type")
        @Expose
        private Object ansApiType;
        @SerializedName("cdr_api_type")
        @Expose
        private Object cdrApiType;
        @SerializedName("get_agent_type")
        @Expose
        private Object getAgentType;
        @SerializedName("get_agent_method")
        @Expose
        private Object getAgentMethod;
        @SerializedName("crm_name")
        @Expose
        private String crmName;
        @SerializedName("crm_url")
        @Expose
        private String crmUrl;
        @SerializedName("crm_domain")
        @Expose
        private String crmDomain;
        @SerializedName("organization_id")
        @Expose
        private String organizationId;
        @SerializedName("cloud_id")
        @Expose
        private Integer cloudId;
        @SerializedName("app_key")
        @Expose
        private String appKey;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getEnv() {
            return env;
        }

        public void setEnv(String env) {
            this.env = env;
        }

        public Integer getProjectId() {
            return projectId;
        }

        public void setProjectId(Integer projectId) {
            this.projectId = projectId;
        }

        public String getProjectKey() {
            return projectKey;
        }

        public void setProjectKey(String projectKey) {
            this.projectKey = projectKey;
        }

        public String getProjectVersion() {
            return projectVersion;
        }

        public void setProjectVersion(String projectVersion) {
            this.projectVersion = projectVersion;
        }

        public String getProjectValue() {
            return projectValue;
        }

        public void setProjectValue(String projectValue) {
            this.projectValue = projectValue;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Integer getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(Integer createdBy) {
            this.createdBy = createdBy;
        }

        public Integer getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(Integer updatedBy) {
            this.updatedBy = updatedBy;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public Object getStickyAgent() {
            return stickyAgent;
        }

        public void setStickyAgent(Object stickyAgent) {
            this.stickyAgent = stickyAgent;
        }

        public String getDidNumber() {
            return didNumber;
        }

        public void setDidNumber(String didNumber) {
            this.didNumber = didNumber;
        }

        public Object getRingApiUrl() {
            return ringApiUrl;
        }

        public void setRingApiUrl(Object ringApiUrl) {
            this.ringApiUrl = ringApiUrl;
        }

        public Object getRingApiParameter() {
            return ringApiParameter;
        }

        public void setRingApiParameter(Object ringApiParameter) {
            this.ringApiParameter = ringApiParameter;
        }

        public Object getAnsApiUrl() {
            return ansApiUrl;
        }

        public void setAnsApiUrl(Object ansApiUrl) {
            this.ansApiUrl = ansApiUrl;
        }

        public Object getAnsApiParameter() {
            return ansApiParameter;
        }

        public void setAnsApiParameter(Object ansApiParameter) {
            this.ansApiParameter = ansApiParameter;
        }

        public Object getCdrApiUrl() {
            return cdrApiUrl;
        }

        public void setCdrApiUrl(Object cdrApiUrl) {
            this.cdrApiUrl = cdrApiUrl;
        }

        public Object getCdrApiParameter() {
            return cdrApiParameter;
        }

        public void setCdrApiParameter(Object cdrApiParameter) {
            this.cdrApiParameter = cdrApiParameter;
        }

        public Object getGetAgentApi() {
            return getAgentApi;
        }

        public void setGetAgentApi(Object getAgentApi) {
            this.getAgentApi = getAgentApi;
        }

        public Object getGetAgentParameter() {
            return getAgentParameter;
        }

        public void setGetAgentParameter(Object getAgentParameter) {
            this.getAgentParameter = getAgentParameter;
        }

        public Object getRingApiMethod() {
            return ringApiMethod;
        }

        public void setRingApiMethod(Object ringApiMethod) {
            this.ringApiMethod = ringApiMethod;
        }

        public Object getAnsApiMethod() {
            return ansApiMethod;
        }

        public void setAnsApiMethod(Object ansApiMethod) {
            this.ansApiMethod = ansApiMethod;
        }

        public Object getCdrApiMethod() {
            return cdrApiMethod;
        }

        public void setCdrApiMethod(Object cdrApiMethod) {
            this.cdrApiMethod = cdrApiMethod;
        }

        public Object getRingApiType() {
            return ringApiType;
        }

        public void setRingApiType(Object ringApiType) {
            this.ringApiType = ringApiType;
        }

        public Object getAnsApiType() {
            return ansApiType;
        }

        public void setAnsApiType(Object ansApiType) {
            this.ansApiType = ansApiType;
        }

        public Object getCdrApiType() {
            return cdrApiType;
        }

        public void setCdrApiType(Object cdrApiType) {
            this.cdrApiType = cdrApiType;
        }

        public Object getGetAgentType() {
            return getAgentType;
        }

        public void setGetAgentType(Object getAgentType) {
            this.getAgentType = getAgentType;
        }

        public Object getGetAgentMethod() {
            return getAgentMethod;
        }

        public void setGetAgentMethod(Object getAgentMethod) {
            this.getAgentMethod = getAgentMethod;
        }

        public String getCrmName() {
            return crmName;
        }

        public void setCrmName(String crmName) {
            this.crmName = crmName;
        }

        public String getCrmUrl() {
            return crmUrl;
        }

        public void setCrmUrl(String crmUrl) {
            this.crmUrl = crmUrl;
        }

        public String getCrmDomain() {
            return crmDomain;
        }

        public void setCrmDomain(String crmDomain) {
            this.crmDomain = crmDomain;
        }

        public String getOrganizationId() {
            return organizationId;
        }

        public void setOrganizationId(String organizationId) {
            this.organizationId = organizationId;
        }

        public Integer getCloudId() {
            return cloudId;
        }

        public void setCloudId(Integer cloudId) {
            this.cloudId = cloudId;
        }

        public String getAppKey() {
            return appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

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
}
