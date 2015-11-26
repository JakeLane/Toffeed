package me.jakelane.wrapperforfacebook;

import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
class JavaScriptInterfaces {
    private final MainActivity mContext;

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
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNotificationNum(0);
                }
            });
            Log.v("FBWrapper", 0 + " notifications");
        }
    }

    @JavascriptInterface
    public void getNotificationsNum(final String number) {
        try {
            final int num = Integer.parseInt(number);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNotificationNum(num);
                }
            });
            Log.v("FBWrapper", number + " notifications");
        } catch (NumberFormatException e) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNotificationNum(0);
                }
            });
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

        if (name != null) {
            final String finalName = name;
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) mContext.findViewById(R.id.profile_name)).setText(finalName);
                }
            });
        }

        // Profile picture regex
        pattern = Pattern.compile("url\\(&quot;(.[^\"]*)&quot;\\)");
        matcher = pattern.matcher(htmlElement);

        String profile_url = null;
        if (matcher.find()) {
            profile_url = android.text.Html.fromHtml(matcher.group(1)).toString();
        }

        if (profile_url != null) {
            final String finalProfile_url = profile_url;
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(mContext).load(finalProfile_url).error(R.mipmap.ic_launcher).into((ImageView) mContext.findViewById(R.id.profile_picture));
                }
            });
        }

    }
}
