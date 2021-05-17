package com.nidoos.doocti.retrofit;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;

public class JWTUtils {
    public static String decoded(String JWTEncoded) throws Exception {
        String details = null;

        try {
            String[] split = JWTEncoded.split("\\.");
            details = getJson(split[1]);
//            JSONObject object = new JSONObject(details);
            Log.d("JWT_DECODED", "Header: " + getJson(split[0]));
            Log.d("JWT_DECODED", "Body: " + getJson(split[1]));
        } catch (UnsupportedEncodingException e) {
            //Error
        }
        return details;
    }

    private static String getJson(String strEncoded) throws UnsupportedEncodingException {
        byte[] decodedBytes = Base64.decode(strEncoded, Base64.URL_SAFE);
        return new String(decodedBytes, "UTF-8");
    }
}
