package me.jakelane.wrapperforfacebook;

import android.util.Log;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

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
    public void getNums(final String json) {
        try {
            final JSONObject nums = new JSONObject(json);
            final int notifications = nums.getInt("n");
            final int messages = nums.getInt("m");
            final int requests = nums.getInt("r");
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNotificationNum(notifications);
                    mContext.setMessagesNum(messages);
                    mContext.setRequestsNum(requests);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(Helpers.LogTag, "Getting numbers threw JSON Exception");
        }
    }
}
