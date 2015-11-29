package me.jakelane.wrapperforfacebook;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;

import java.util.List;

import im.delight.android.webview.AdvancedWebView;

class WebViewListener implements AdvancedWebView.Listener {
    private final Activity mActivity;
    private final SharedPreferences mPreferences;
    private final WebView mWebView;

    WebViewListener(Activity activity, WebView view) {
        mActivity = activity;
        mWebView = view;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {}

    @Override
    public void onPageFinished(String url) {
        List<String> uri_segments = Uri.parse(url).getPathSegments();
        if (!(uri_segments.size() > 0 && uri_segments.get(0).equals("composer"))) {
            // Hide the menu bar (but not on the composer)
            JavaScriptHelpers.hideMenuBar(mWebView);
        }

        // Get the currently open tab and check on the navigation menu
        JavaScriptHelpers.updateCurrentTab(mWebView);

        int update_interval = Integer.parseInt(mPreferences.getString(SettingsActivity.KEY_PREF_UPDATE_INTERVAL, "45000"));

        // Get the notification number
        JavaScriptHelpers.updateNotificationsService(mWebView, update_interval);

        // Get the messages number
        if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false)) {
            JavaScriptHelpers.updateMessagesService(mWebView, update_interval);
        }

        // Get logged in info
        JavaScriptHelpers.updateUserInfo(mWebView);
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {}

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {}

    @Override
    public void onExternalPageRequest(String url) {
        Log.v("FBWrapper", "External page: " + url);
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mActivity.startActivity(intent);
    }
}
