package me.jakelane.wrapperforfacebook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

class WrapperWebViewClient extends WebViewClient {
    private final Activity mActivity;
    private final SharedPreferences mPreferences;

    WrapperWebViewClient(Activity activity) {
        mActivity = activity;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Uri.parse(url).getHost().contains("facebook.com")) {
            // This is the facebook website, so do not override; let my WebView load the page
            return false;
        }
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mActivity.startActivity(intent);
        return true;
    }

    public void onPageFinished(WebView view, String url) {
        List<String> uri_segments = Uri.parse(url).getPathSegments();
        if (!(uri_segments.size() > 0 && uri_segments.get(0).equals("composer"))) {
            // Hide the menu bar (but not on the composer)
            JavaScriptHelpers.hideMenuBar(view);
        }

        // Get the currently open tab and check on the navigation menu
        JavaScriptHelpers.updateCurrentTab(view);

        int update_interval = Integer.parseInt(mPreferences.getString(SettingsActivity.KEY_PREF_UPDATE_INTERVAL, "45000"));

        // Get the notification number
        JavaScriptHelpers.updateNotificationsService(view, update_interval);

        // Get the messages number
        if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false)) {
            JavaScriptHelpers.updateMessagesService(view, update_interval);
        }

        // Get logged in info
        JavaScriptHelpers.updateUserInfo(view);
    }
}
