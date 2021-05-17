package com.nidoos.doocti.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.linphone.core.AVPFMode;
import org.linphone.core.Address;
import org.linphone.core.AuthInfo;
import org.linphone.core.AuthMethod;
import org.linphone.core.Call;
import org.linphone.core.CallLog;
import org.linphone.core.CallStats;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatRoom;
import org.linphone.core.ConfiguringState;
import org.linphone.core.Content;
import org.linphone.core.Core;
import org.linphone.core.CoreListener;
import org.linphone.core.EcCalibratorStatus;
import org.linphone.core.Event;
import org.linphone.core.Factory;
import org.linphone.core.Friend;
import org.linphone.core.FriendList;
import org.linphone.core.GlobalState;
import org.linphone.core.InfoMessage;
import org.linphone.core.PresenceModel;
import org.linphone.core.ProxyConfig;
import org.linphone.core.PublishState;
import org.linphone.core.RegistrationState;
import org.linphone.core.SubscriptionState;
import org.linphone.core.Transports;
import org.linphone.core.VersionUpdateCheckResult;

import java.util.Timer;
import java.util.TimerTask;

public class SipManager implements CoreListener {

    //CONSTANTS
    final static String SHARED_PREF_SIP = "SHARED_PREF_SIP";
    final static String USERNAME_SIP = "USERNAME_SIP";
    final static String PASSWORD_SIP = "PASSWORD_SIP";
    final static String DOMAIN_SIP = "DOMAIN_SIP";
    final static String DEFAULT = "DEFAULT";

    //VARIABLES
    static Core core=null;
    String usernameSip,passwordSip,domainSip;
    Context context;
    Timer timer;
    boolean isAutoAnswerEnabled;
    Call call = null;

    public SipManager(Context context) {
        this.context = context;
    }

    private boolean getValues() {

        SharedPreferences sp = context.getSharedPreferences(SHARED_PREF_SIP,Context.MODE_PRIVATE);
        usernameSip = sp.getString(USERNAME_SIP,DEFAULT);
        passwordSip = sp.getString(PASSWORD_SIP,DEFAULT);
        domainSip = sp.getString(DOMAIN_SIP,DEFAULT);

        if(usernameSip.equals(DEFAULT) || passwordSip.equals(DEFAULT) || domainSip.equals(DEFAULT)) {
            return false;
        }
        return true;
    }

    public boolean register(){

        if(!getValues()) {
//            Toast.makeText(context,"Provide the details for SIP Registration",Toast.LENGTH_LONG).show();
           // context.startActivity(new Intent(context, RegisterSipActivity.class));
        }

        Factory.instance().setDebugMode(true,"Linphone");
        if(core==null) {
            core = Factory.instance().createCore(null, null, context);
            core.start();
        }
        core.addListener(this);




        String identity = "sip:" + usernameSip + "@" + domainSip;
        String proxy = "sip:" + domainSip;

        Transports transports = core.getTransports();
        transports.setUdpPort(-1);
        core.setTransports(transports);
        core.setNetworkReachable(true);

        ProxyConfig proxyconfig = core.createProxyConfig();
        Address proxyaddress = core.createAddress(proxy);
        Address identityaddress = core.createAddress(identity);


        AuthInfo authinfo = Factory.instance().createAuthInfo(usernameSip,usernameSip,passwordSip,
                null, null, domainSip);



        proxyconfig.edit();
        proxyconfig.setIdentityAddress(identityaddress);
        String serveraddress = proxyaddress.asStringUriOnly();
        proxyconfig.setServerAddr(serveraddress);
        proxyconfig.setAvpfMode(AVPFMode.Disabled);
        proxyconfig.setAvpfRrInterval(0);
        proxyconfig.setQualityReportingCollector(null);
        proxyconfig.setQualityReportingInterval(0);
        proxyconfig.enableRegister(true);
        proxyconfig.done();


        core.addProxyConfig(proxyconfig);
        core.addAuthInfo(authinfo);
        core.setDefaultProxyConfig(proxyconfig);

        startTimer();



        return true;
    }

    public void unRegister() {

        ProxyConfig proxyconfig = core.createProxyConfig();
        AuthInfo authinfo = Factory.instance().createAuthInfo(usernameSip,usernameSip,passwordSip,
                null, null, domainSip);
        proxyconfig.edit();
        proxyconfig.enableRegister(false);
        proxyconfig.done();

        core.removeAuthInfo(authinfo);
    }


    public void startTimer() {

        cancelTimer();

        timer =new Timer(this.getClass().getName() + " Scheduler");
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        core.iterate();
                    }
                });
            }
        }, 0, 100);

    }

    public void cancelTimer() {
        if(timer != null) timer.cancel();
        timer = null;
    }

    public void callUsingSip(String to){
        if(core != null) {
            core.invite(to);
        }
        else {
            Log.d("vijay","Core is null");
        }
    }


    public void acceptCall() {

        if(core != null && call != null) {
            call.accept();
        }

    }

    public void hangCall() {
        if(core!=null && call!=null) {
            call.terminate();
        }
    }

    public boolean muteCall() {
         if(core!=null) {
            if(core.getCurrentCall() != null) {
                if(!core.getCurrentCall().getMicrophoneMuted()) {
                    core.getCurrentCall().setMicrophoneMuted(true);
                    return true;
                }
                else {
                    core.getCurrentCall().setMicrophoneMuted(false);
                    return false;
                }
            }
        }
         return false;
    }
    public boolean holdCall() {
        if(core!=null) {
            if(call!=null) {
                Log.d("vijay","call=" +call + " state=" + call.getState());
                //Log.d("vijay","Inside hold call:" + call.getState()+ "");
                if(call.getState() == Call.State.Paused) {
                    Log.d("vijay","resuming call");
                    call.resume();
                    return false;
                }
                else if(call.getState() == Call.State.StreamsRunning){
                    Log.d("Vijay","pausing call");
                    call.pause();
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isRegistered() {
        if(core.getDefaultProxyConfig() != null)
            return core.getDefaultProxyConfig().getState().equals(RegistrationState.Ok);
        else
            return false;
    }

    public void setAutoAnswerEnabled(boolean value) {
        isAutoAnswerEnabled = value;
    }

    public boolean getAutoAnswerEnabled() {
        Log.d("Vijay","Autoanswer:" + isAutoAnswerEnabled);
        return isAutoAnswerEnabled;
    }


    public void setCall() {
       if(core != null)
           call = core.getCurrentCall();
    }

    @Override
    public void onTransferStateChanged(Core core, Call call, Call.State state) {

    }

    @Override
    public void onFriendListCreated(Core core, FriendList friendList) {

    }

    @Override
    public void onSubscriptionStateChanged(Core core, Event event, SubscriptionState subscriptionState) {

    }

    @Override
    public void onCallLogUpdated(Core core, CallLog callLog) {

    }

    @Override
    public void onCallStateChanged(Core core, Call call, Call.State state, String s) {
//          Log.d("Vijay",s);
//          Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAuthenticationRequested(Core core, AuthInfo authInfo, AuthMethod authMethod) {

    }

    @Override
    public void onNotifyPresenceReceivedForUriOrTel(Core core, Friend friend, String s, PresenceModel presenceModel) {
 
    }

    @Override
    public void onChatRoomStateChanged(Core core, ChatRoom chatRoom, ChatRoom.State state) {

    }

    @Override
    public void onBuddyInfoUpdated(Core core, Friend friend) {

    }

    @Override
    public void onNetworkReachable(Core core, boolean b) {

    }

    @Override
    public void onNotifyReceived(Core core, Event event, String s, Content content) {

    }

    @Override
    public void onNewSubscriptionRequested(Core core, Friend friend, String s) {

    }

    @Override
    public void onRegistrationStateChanged(Core core, ProxyConfig proxyConfig, RegistrationState registrationState, String s) {
            if(registrationState == RegistrationState.Failed) {
//                Toast.makeText(context,s + " for " + core.getIdentity(),Toast.LENGTH_LONG).show();
                cancelTimer();
            }

           // if(registrationState == RegistrationState.Ok) cancelTimer();


    }

    @Override
    public void onNotifyPresenceReceived(Core core, Friend friend) {

    }

    @Override
    public void onEcCalibrationAudioInit(Core core) {

    }

    @Override
    public void onMessageReceived(Core core, ChatRoom chatRoom, ChatMessage chatMessage) {

    }

    @Override
    public void onEcCalibrationResult(Core core, EcCalibratorStatus ecCalibratorStatus, int i) {

    }

    @Override
    public void onSubscribeReceived(Core core, Event event, String s, Content content) {

    }

    @Override
    public void onInfoReceived(Core core, Call call, InfoMessage infoMessage) {

    }

    @Override
    public void onCallStatsUpdated(Core core, Call call, CallStats callStats) {

    }

    @Override
    public void onFriendListRemoved(Core core, FriendList friendList) {

    }

    @Override
    public void onReferReceived(Core core, String s) {

    }

    @Override
    public void onQrcodeFound(Core core, String s) {

    }

    @Override
    public void onConfiguringStatus(Core core, ConfiguringState configuringState, String s) {

    }

    @Override
    public void onCallCreated(Core core, Call call) {

    }

    @Override
    public void onPublishStateChanged(Core core, Event event, PublishState publishState) {

    }

    @Override
    public void onCallEncryptionChanged(Core core, Call call, boolean b, String s) {

    }

    @Override
    public void onIsComposingReceived(Core core, ChatRoom chatRoom) {

    }

    @Override
    public void onMessageReceivedUnableDecrypt(Core core, ChatRoom chatRoom, ChatMessage chatMessage) {

    }

    @Override
    public void onLogCollectionUploadProgressIndication(Core core, int i, int i1) {

    }

    @Override
    public void onVersionUpdateCheckResultReceived(Core core, VersionUpdateCheckResult versionUpdateCheckResult, String s, String s1) {

    }

    @Override
    public void onEcCalibrationAudioUninit(Core core) {

    }

    @Override
    public void onGlobalStateChanged(Core core, GlobalState globalState, String s) {

    }

    @Override
    public void onLogCollectionUploadStateChanged(Core core, Core.LogCollectionUploadState logCollectionUploadState, String s) {

    }

    @Override
    public void onDtmfReceived(Core core, Call call, int i) {

    }



}
