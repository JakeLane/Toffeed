package me.jakelane.wrapperforfacebook;

import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

abstract class WrapperWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Uri.parse(url).getHost().contains("facebook.com")) {
            // This is the facebook website, so do not override; let my WebView load the page
            return false;
        }
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        launchExternalBrowser(url);
        return true;
    }

    public abstract void launchExternalBrowser(String url);

    public void onPageFinished(WebView view, String url) {
        // Hide the menu bar
        JavaScriptHelpers.hideMenuBar(view);

        // Get the currently open tab and check on the navigation menu
        JavaScriptHelpers.updateCurrentTab(view);

        // Get the notifications
        JavaScriptHelpers.updateNotifications(view);

        // Get logged in info
        JavaScriptHelpers.updateUserInfo(view);
    }
}
