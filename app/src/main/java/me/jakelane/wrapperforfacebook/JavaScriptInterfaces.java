package me.jakelane.wrapperforfacebook;

import android.util.Log;
import android.webkit.JavascriptInterface;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaScriptInterfaces {
    MainActivity mContext;

    // Instantiate the interface and set the context
    JavaScriptInterfaces(MainActivity c) {
        mContext = c;
    }

    @JavascriptInterface
    public void getCurrent(final String value) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (value) {
                    case "feed_jewel":
                        mContext.navigationView.setCheckedItem(R.id.nav_news);
                        break;
                    case "requests_jewel":
                        mContext.navigationView.setCheckedItem(R.id.nav_friendreq);
                        break;
                    case "messages_jewel":
                        mContext.navigationView.setCheckedItem(R.id.nav_messages);
                        break;
                    case "notifications_jewel":
                        break;
                    case "search_jewel":
                        mContext.navigationView.setCheckedItem(R.id.nav_search);
                        break;
                    case "bookmarks_jewel":
                        mContext.navigationView.setCheckedItem(R.id.nav_mainmenu);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @JavascriptInterface
    public void getNotifications(final boolean value) {
        if (value) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.wrapperWebView.loadUrl("javascript:android.getNotificationsNum(document.querySelector('#notifications_jewel > a > div > span[data-sigil=count]').innerHTML)");
                }
            });
        } else {
            Log.v("FBWrapper", 0 + " notifications");
        }
    }

    @JavascriptInterface
    public void getNotificationsNum(final String number) {
        if (!number.equals("undefined")) {
            Log.v("FBWrapper", number + " notifications");
        } else {
            Log.v("FBWrapper", 0 + " notifications");
        }
    }

    @JavascriptInterface
    public void getUserInfo(final String htmlElement) {
        // Name regex
        Pattern pattern = Pattern.compile("aria-label=\"(.[^\"]*)\"");
        Matcher matcher = pattern.matcher(htmlElement);

        String name = null;
        if (matcher.find()) {
            name = matcher.group(1);
        }

        // Profile picture regex
        pattern = Pattern.compile("url\\(&quot;(.[^\"]*)&quot;\\)");
        matcher = pattern.matcher(htmlElement);

        String profile_url = null;
        if (matcher.find()) {
            profile_url = android.text.Html.fromHtml(matcher.group(1)).toString();
        }

        Log.v("FBWrapper", name + ": " + profile_url);
    }
}
