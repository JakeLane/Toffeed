package me.jakelane.wrapperforfacebook;

import android.util.Log;
import android.webkit.JavascriptInterface;

@SuppressWarnings("unused")
class JavaScriptInterfaces {
    private final MainActivity mContext;

    // Instantiate the interface and set the context
    JavaScriptInterfaces(MainActivity c) {
        mContext = c;
    }

    @JavascriptInterface
    public void loadingCompleted() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.setLoading(false);
            }
        });
    }

    @JavascriptInterface
    public void getCurrent(final String value) {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (value) {
                    case "feed_jewel":
                        mContext.mNavigationView.setCheckedItem(R.id.nav_news);
                        break;
                    case "requests_jewel":
                        mContext.mNavigationView.setCheckedItem(R.id.nav_friendreq);
                        break;
                    case "messages_jewel":
                        mContext.mNavigationView.setCheckedItem(R.id.nav_messages);
                        break;
                    case "notifications_jewel":
                        Helpers.uncheckRadioMenu(mContext.mNavigationView.getMenu());
                        break;
                    case "search_jewel":
                        mContext.mNavigationView.setCheckedItem(R.id.nav_search);
                        break;
                    case "bookmarks_jewel":
                        mContext.mNavigationView.setCheckedItem(R.id.nav_mainmenu);
                        break;
                    default:
                        Helpers.uncheckRadioMenu(mContext.mNavigationView.getMenu());
                        break;
                }
            }
        });
    }

    @JavascriptInterface
    public void getNotifications(final String number) {
        try {
            final int num = Integer.parseInt(number);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNotificationNum(num);
                }
            });
            Log.v(Helpers.LogTag, number + " notifications");
        } catch (NumberFormatException e) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNotificationNum(0);
                }
            });
            Log.v(Helpers.LogTag, 0 + " notifications");
        }
    }

    @JavascriptInterface
    public void getMessages(final String number) {
        try {
            final int num = Integer.parseInt(number);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setMessagesNum(num);
                }
            });
            Log.v(Helpers.LogTag, number + " messages");
        } catch (NumberFormatException e) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setMessagesNum(0);
                }
            });
            Log.v(Helpers.LogTag, 0 + " messages");
        }
    }
}
