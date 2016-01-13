package me.jakelane.wrapperforfacebook;

import android.util.Log;
import android.webkit.CookieManager;

class Helpers {
    public static final String LogTag = "FBWrapper";

    // Method to retrieve a single cookie
    public static String getCookie(String cookieName){
        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(MainActivity.FACEBOOK_URL_BASE);
        Log.v(Helpers.LogTag, cookies);
        String[] temp = cookies.split(";");
        for (String ar1 : temp) {
            if (ar1.contains(cookieName)) {
                String[] temp1 = ar1.split("=");
                return temp1[1];
            }
        }
        // Return null as we found no cookie
        return null;
    }
}
