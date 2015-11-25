package me.jakelane.wrapperforfacebook;

import android.webkit.JavascriptInterface;
import android.widget.Toast;

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
            mContext.wrapperWebView.loadUrl("javascript:android.getNotificationsNum(document.querySelector('#notifications_jewel > a > div > span[data-sigil=count]').innerHTML)");
        } else {
            Toast.makeText(mContext, "FALSE", Toast.LENGTH_SHORT).show();
        }
    }

    @JavascriptInterface
    public void getNotificationsNum(final String number) {
        if (!number.equals("undefined")) {
            Toast.makeText(mContext, number, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "FALSE", Toast.LENGTH_SHORT).show();
        }
    }
}
