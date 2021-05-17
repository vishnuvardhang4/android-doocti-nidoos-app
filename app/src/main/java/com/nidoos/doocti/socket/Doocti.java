package com.nidoos.doocti.socket;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.nidoos.doocti.info.ApiInfo;
import com.nidoos.doocti.constants.SharedPrefConstants;
import com.nidoos.doocti.utils.SharedPrefHelper;
import com.nidoos.doocti.info.UserInfo;

public class Doocti extends Application {

    private SharedPreferences sharedpreferences;
    private Socket mSocket;
    private UserInfo userInfo;
    private ApiInfo apiInfo;
    private String token;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedpreferences = getSharedPreferences(SharedPrefConstants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        try {
            userInfo = SharedPrefHelper.getUserInfo(sharedpreferences);
            apiInfo = SharedPrefHelper.getApiInfo(sharedpreferences);

            if (userInfo != null) {
                token = userInfo.getToken().replace("Bearer ", "");
                socketConnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void socketConnect() {
            try {

                if(SharedPrefHelper.getUserInfo(sharedpreferences) != null)
                    token = SharedPrefHelper.getUserInfo(sharedpreferences).getToken().replace("Bearer ", "");

                IO.Options opts = new IO.Options();
                opts.forceNew = true;
                opts.timeout = 1000;
                opts.reconnection = true;
                opts.query = "token=" + token + "&type=mobile_doocti";
                Log.i("options", opts.query);
                mSocket = IO.socket(SharedPrefHelper.getApiInfo(sharedpreferences).getSocketDomain(), opts);
                Log.i("conn", "soc" + mSocket.connected());
            } catch (Exception e) {
                e.printStackTrace();
            }

    }


    public  Socket getSocket() {
        return mSocket;
    }
}
