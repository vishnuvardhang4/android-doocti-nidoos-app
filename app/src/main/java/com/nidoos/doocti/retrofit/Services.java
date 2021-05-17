package com.nidoos.doocti.retrofit;

import com.nidoos.doocti.request.CallActivityRequest;
import com.nidoos.doocti.request.CallCountRequest;
import com.nidoos.doocti.request.CallLogRequest;
import com.nidoos.doocti.request.ConferenceRequest;
import com.nidoos.doocti.request.CreateMeetingRequest;
import com.nidoos.doocti.request.FetchLeadsRequest;
import com.nidoos.doocti.request.LoginRequest;
import com.nidoos.doocti.request.LogoutRequest;
import com.nidoos.doocti.request.TimelineRequest;
import com.nidoos.doocti.request.UpdateCallActivityRequest;
import com.nidoos.doocti.request.UpdateCampaignRequest;
import com.nidoos.doocti.request.UpdateLeadstatusRequest;
import com.nidoos.doocti.response.CallActivityResponse;
import com.nidoos.doocti.response.CallCountResponse;
import com.nidoos.doocti.response.CallLogResponse;
import com.nidoos.doocti.response.CallStatusResponse;
import com.nidoos.doocti.response.CampaignResponse;
import com.nidoos.doocti.response.CampaignUpdateResponse;
import com.nidoos.doocti.response.ConferenceResponse;
import com.nidoos.doocti.response.CreateMeetingResponse;
import com.nidoos.doocti.response.CrmInfoResponse;
import com.nidoos.doocti.response.CrmUrlResponse;
import com.nidoos.doocti.response.CustomerInfoResponse;
import com.nidoos.doocti.response.FetchLeadsResponse;
import com.nidoos.doocti.response.GetContactsReponse;
import com.nidoos.doocti.response.GetMeetingResponse;
import com.nidoos.doocti.response.LeadView;
import com.nidoos.doocti.response.LoginResponse;
import com.nidoos.doocti.response.LogoutResponse;
import com.nidoos.doocti.response.MainFilterResponse;
import com.nidoos.doocti.response.PhoneNumberResponse;
import com.nidoos.doocti.response.PlayRecordingResponse;
import com.nidoos.doocti.response.PerformanceResponse;
import com.nidoos.doocti.response.SubFilterResponse;
import com.nidoos.doocti.response.TimelineResponse;
import com.nidoos.doocti.response.UpdateCallActivityResponse;
import com.nidoos.doocti.response.UpdateLeadstatusResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Services {


    //aster-dialer-api
    @POST("/login")
    Call<LoginResponse> login(@Header("Authorization") String token, @Body LoginRequest loginRequest);

    @POST("/logout")
    Call<LogoutResponse> logout(@Header("Authorization") String token, @Body LogoutRequest logoutRequest);

    @POST("/call_count")
    Call<CallCountResponse> callCount(@Header("Authorization") String token, @Body CallCountRequest callCountRequest);

    @POST("/calllog")
    Call<CallLogResponse> callLog(@Header("Authorization") String token, @Body CallLogRequest callLogRequest);

    @GET("recordingUrl")
    Call<PlayRecordingResponse> getRecordingURL(@Header("Authorization") String token, @Query("date") String date, @Query("id") String id);

    @GET("/agentPerformance")
    Call<PerformanceResponse> getPerformance(@Header("Authorization") String token, @Query("fromdate") String startTime, @Query("todate") String endTime, @Query("agent") String agent);

    //crm-api
    @GET("/salesforce/lead/search")
    Call<LeadView> getLeadsInfo(@Header("Authorization") String token, @Query("userID") String id);

    @PUT("salesforce/call/activity/{id}")
    Call<UpdateCallActivityResponse> updateCallActivity(@Header("Authorization") String token, @Path("id") String id, @Body UpdateCallActivityRequest updateCallActivityRequest);

    @POST("/{crmDomain}/call/activity")
    Call<CallActivityResponse> createCallActivity(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Body CallActivityRequest callActivityRequest);

    @POST("/otherAgentStatus")
    Call<ConferenceResponse> getOtherAgents(@Header("Authorization") String token, @Body ConferenceRequest ConferenceRequest);

    @GET("/{crmDomain}/autodial/filters")
    Call<MainFilterResponse> getMainFilter(@Header("Authorization") String token, @Path("crmDomain") String crmDomain);

    @GET("/{crmDomain}/autodial/sub-filters")
    Call<SubFilterResponse> getSubFilter(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Query("id") String id);

    @GET("/{crmDomain}/call/status")
    Call<CallStatusResponse> getCallStatus(@Header("Authorization") String token, @Path("crmDomain") String crmDomain);

    @POST("/{crmDomain}/lead/search")
    Call<FetchLeadsResponse> searchLeads(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Body FetchLeadsRequest fetchLeadsRequest);

    @GET("/{crmDomain}/customer")
    Call<CustomerInfoResponse> getCustomerInfo(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Query("number") String number);

    @GET("/{crmDomain}/customer/{customer_id}/timeline")
    Call<TimelineResponse> getTimeline(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Path("customer_id") String customer_id);

    @POST("/{crmDomain}/customer/timeline")
    Call<TimelineResponse> getAsterTimeline(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Body TimelineRequest timelineRequest);

    @POST("/{crmDomain}/meeting/schedule")
    Call<CreateMeetingResponse> createMeeting(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Body CreateMeetingRequest createMeetingRequest);

    @GET("/{crmDomain}/crm/url")
    Call<CrmUrlResponse> getCrmUrl(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Query("module") String module, @Query("id") String id);

    @GET("/{crmDomain}/contacts")
    Call<GetContactsReponse> getContacts(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Query("userID") String userID, @Query("name") String name, @Query("number") String number, @Query("skip") Integer skip, @Query("take") Integer take);

    @GET("/{crmDomain}/meetings")
    Call<GetMeetingResponse> getMeetings(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Query("userID") String userID, @Query("skip") Integer skip, @Query("take") Integer take);

    @GET("/{crmDomain}/meeting/number")
    Call<PhoneNumberResponse> getPhoneNumber(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Query("leadModule") String leadModule, @Query("leadID") String leadID);

    //common-api
    @GET("/common/project/config")
    Call<CrmInfoResponse> getCrmInfo(@Header("Authorization") String token, @Query("tenant_code") String id, @Query("user_email") String user_email);

    //aster-dialer-api
    @GET("/asterdialer/campaigns")
    Call<CampaignResponse> campaigns(@Header("Authorization") String token);

    @PUT("/{crmDomain}/lead/status")
    Call<UpdateLeadstatusResponse> updateLeadStatus(@Header("Authorization") String token, @Path("crmDomain") String crmDomain, @Body UpdateLeadstatusRequest updateLeadstatusRequest );

    //aster-dialer-api
    @PUT("/asterdialer/agent/live/{id}")
    Call<CampaignUpdateResponse> CampaignUpdate(@Header("Authorization") String token, @Path("id") String id, @Body UpdateCampaignRequest updateCampaignRequest );


}
