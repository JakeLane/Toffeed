package me.jakelane.wrapperforfacebook;

import android.webkit.JavascriptInterface;

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
                        mContext.navigationView.setCheckedItem(R.id.nav_notifications);
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
}
