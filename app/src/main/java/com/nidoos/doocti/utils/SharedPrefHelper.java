package com.nidoos.doocti.utils;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.info.AutoDialInfo;
import com.nidoos.doocti.info.CallInfo;
import com.nidoos.doocti.info.CustomerInfo;
import com.nidoos.doocti.info.PbxInfo;
import com.nidoos.doocti.info.PermissionInfo;
import com.nidoos.doocti.info.UserInfo;

import static com.nidoos.doocti.constants.SharedPrefConstants.API_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.AUTO_DIAL_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.CALL_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.CUSTOMER_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.PBX_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.PERMISSION_INFO;
import static com.nidoos.doocti.constants.SharedPrefConstants.USER_INFO;

public class SharedPrefHelper {


    public static Object getObject(SharedPreferences sharedPreferences, String key, Class<?> className) {
        return new Gson().fromJson(
                sharedPreferences.getString(key, "Not found"),
                className
        );
    }

    public static UserInfo getUserInfo(SharedPreferences sharedPreferences) {
        return new Gson().fromJson(
                sharedPreferences.getString(USER_INFO, "userInfo Not found"),
                UserInfo.class);
    }

    public static ApiInfo getApiInfo(SharedPreferences sharedPreferences) {
        return new Gson().fromJson(
                sharedPreferences.getString(API_INFO, "apiInfo Not found"),
                ApiInfo.class);
    }

    public static AutoDialInfo getAutoDialInfo(SharedPreferences sharedPreferences) {
        return new Gson().fromJson(
                sharedPreferences.getString(AUTO_DIAL_INFO, "autoDialInfo Not found"),
                AutoDialInfo.class);
    }

    public static PbxInfo getPbxInfo(SharedPreferences sharedPreferences) {
        return new Gson().fromJson(
                sharedPreferences.getString(PBX_INFO, "pbxInfo Not found"),
                PbxInfo.class);
        
    }

    public static CallInfo getCallInfo(SharedPreferences sharedPreferences) {
        return new Gson().fromJson(
                sharedPreferences.getString(CALL_INFO, "callInfo Not found"),
               CallInfo.class);

    }
    public static CustomerInfo getCustomerInfo(SharedPreferences sharedPreferences) {
        return new Gson().fromJson(
                sharedPreferences.getString(PBX_INFO, "customerInfo Not found"),
                CustomerInfo.class);
    }

    public static PermissionInfo getPermissionInfo(SharedPreferences sharedPreferences) {
        return (PermissionInfo) getObject(sharedPreferences,PERMISSION_INFO,PermissionInfo.class);
    }

    public static void putObject(SharedPreferences sharedPreferences,String key,Object object) {
        sharedPreferences.edit()
                .putString(key,new Gson().toJson(object))
                .apply();
    }
    
    public static void putUserInfo(SharedPreferences sharedPreferences, UserInfo userInfo) {
        putObject(sharedPreferences,USER_INFO,userInfo);
    }

    public static void putApiInfo(SharedPreferences sharedPreferences, ApiInfo apiInfo) {
        putObject(sharedPreferences,API_INFO,apiInfo);
    }

    public static void putAutoDialInfo(SharedPreferences sharedPreferences, AutoDialInfo autoDialInfo) {
        putObject(sharedPreferences,AUTO_DIAL_INFO,autoDialInfo);
    }

    public static void putPbxInfo(SharedPreferences sharedPreferences, PbxInfo pbxInfo) {
        putObject(sharedPreferences,PBX_INFO,pbxInfo);
    }

    public static void putCallInfo(SharedPreferences sharedPreferences, CallInfo callInfo) {
        putObject(sharedPreferences,CALL_INFO,callInfo);
    }

    public static void putCustomerInfo(SharedPreferences sharedPreferences, CustomerInfo customerInfo) {
        putObject(sharedPreferences,CUSTOMER_INFO,customerInfo);
    }

    public static void putPermissionInfo(SharedPreferences sharedPreferences, PermissionInfo permissionInfo) {
        putObject(sharedPreferences,PERMISSION_INFO,permissionInfo);
    }

}
