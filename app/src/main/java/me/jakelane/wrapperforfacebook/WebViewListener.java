package me.jakelane.wrapperforfacebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.webkit.WebView;

import im.delight.android.webview.AdvancedWebView;

class WebViewListener implements AdvancedWebView.Listener {
    // #page{top:-45px;}
    private static final String HIDE_MENU_BAR_CSS = "%23page%7Btop%3A-45px%7D";
    // #mbasic_inline_feed_composer{display:none}
    private static final String HIDE_COMPOSER_CSS = "%23mbasic_inline_feed_composer%7Bdisplay%3Anone%7D";

    private final MainActivity mActivity;
    private final SharedPreferences mPreferences;
    private final AdvancedWebView mWebView;
    private final FloatingActionButton mWebViewFAB;
    private final int mScrollThreshold;

    WebViewListener(MainActivity activity, WebView view) {
        mActivity = activity;
        mWebView = (AdvancedWebView) view;
        mPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mScrollThreshold = activity.getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold);
        mWebViewFAB = activity.mFAB;
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        // Show the spinner and hide the WebView
        mActivity.setLoading(true);
    }

    @Override
    public void onPageFinished(String url) {
        // Only do things if logged in
        if (mActivity.checkLoggedInState()) {
            // Load the composer if there is the 'loadcomposer' param
            JavaScriptHelpers.paramLoader(mWebView, url);

            // Build a CSS string for injection
            String css = "";

            // Hide the menu bar (but not on the composer)
            if (!url.contains("/composer/")) {
                css += HIDE_MENU_BAR_CSS;
            }

            // Hide the status editor on the News Feed if setting is enabled
            if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_HIDE_EDITOR, true)) {
                css += HIDE_COMPOSER_CSS;
            }

            // Inject the css
            if (!css.isEmpty()) {
                JavaScriptHelpers.loadCSS(mWebView, css);
            }

            // Get the currently open tab and check on the navigation menu
            JavaScriptHelpers.updateCurrentTab(mWebView);

            // Get the notification number
            JavaScriptHelpers.updateNotificationsService(mWebView);

            // Get the messages number
            if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false)) {
                JavaScriptHelpers.updateMessagesService(mWebView);
            }
        }
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        mActivity.setLoading(false);
    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
    }

    @Override
    public void onExternalPageRequest(String url) {
        Log.v(Helpers.LogTag, "External page: " + url);
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mActivity.startActivity(intent);
    }

    @Override
    public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        // Make sure the hiding is enabled and the scroll was significant
        if (mPreferences.getBoolean(SettingsActivity.KEY_PREF_FAB_SCROLL, false) && Math.abs(oldScrollY - scrollY) > mScrollThreshold) {
            if (scrollY > oldScrollY) {
                // User scrolled down, hide the button
                mWebViewFAB.hide();
            } else if (scrollY < oldScrollY) {
                // User scrolled up, show the button
                mWebViewFAB.show();
            }
        }
    }
}
