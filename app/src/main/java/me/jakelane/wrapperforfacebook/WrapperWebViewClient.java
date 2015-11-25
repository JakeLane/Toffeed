package me.jakelane.wrapperforfacebook;

import android.content.Intent;
import android.net.Uri;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public abstract class WrapperWebViewClient extends WebViewClient {
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
        view.loadUrl("javascript:(function(){document.getElementById('page').style.top = '-45px';})();");

        // Get the currently open tab and check on the navigation menu
        view.loadUrl("javascript:android.getCurrent(document.querySelector('.popoverOpen').id)");

        // Get the notifications
        view.loadUrl("javascript:android.getNotifications(document.querySelector('#notifications_jewel').classList.contains('hasCount'))");

        // Get logged in info
    }
}
