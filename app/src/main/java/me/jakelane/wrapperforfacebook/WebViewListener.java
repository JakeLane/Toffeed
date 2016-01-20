package me.jakelane.wrapperforfacebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;

import im.delight.android.webview.AdvancedWebView;

class WebViewListener implements AdvancedWebView.Listener {
    private final MainActivity mActivity;
    private final SharedPreferences mPreferences;
    private final WebView mWebView;

    WebViewListener(MainActivity activity, WebView view) {
        mActivity = activity;
        mWebView = view;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        // Show the spinner and hide the WebView
        mActivity.setLoading(true);
    }

    @Override
    public void onPageFinished(String url) {
        // Hide the menu bar (but not on the composer)
        JavaScriptHelpers.hideMenuBar(mWebView);

        // Get the currently open tab and check on the navigation menu
        JavaScriptHelpers.updateCurrentTab(mWebView);

        int update_interval = Integer.parseInt(mPreferences.getString(SettingsActivity.KEY_PREF_UPDATE_INTERVAL, "45000"));

        // Get the notification number
        JavaScriptHelpers.updateNotificationsService(mWebView, update_interval);

        // Get the messages number
        if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false)) {
            JavaScriptHelpers.updateMessagesService(mWebView, update_interval);
        }

        // Make sure the user is logged in
        mActivity.checkLoggedInState();
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
