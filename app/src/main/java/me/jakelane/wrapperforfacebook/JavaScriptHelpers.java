package me.jakelane.wrapperforfacebook;

import android.net.UrlQuerySanitizer;
import android.webkit.WebView;

class JavaScriptHelpers {
    private static final int BADGE_UPDATE_INTERVAL = 30000;

    public static void updateCurrentTab(WebView view) {
        // Get the currently open tab and check on the navigation menu
        view.loadUrl("javascript:(function()%7Btry%7Bandroid.getCurrent(document.querySelector('.popoverOpen').id)%7Dcatch(_)%7Bandroid.getCurrent('null')%7D%7D)()");
    }

    public static void updateNotificationsService(WebView view) {
        // Start the notification service");
        view.loadUrl("javascript:(function()%7Bfunction%20notification_service()%7Bandroid.getNotifications(document.querySelector('%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D').innerHTML)%2CsetTimeout(notification_service%2C" + BADGE_UPDATE_INTERVAL + ")%7Dtry%7Bnotification_service()%7Dcatch(_)%7B%7D%7D)()");
    }

    public static void updateNotifications(WebView view) {
        // Run the notification check once
        view.loadUrl("javascript:(function()%7Btry%7Bandroid.getNotifications(document.querySelector('%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D').innerHTML)%7Dcatch(_)%7B%7D%7D)()");
    }

    public static void updateMessagesService(WebView view) {
        // Start the message service
        view.loadUrl("javascript:(function()%7Bfunction%20message_service()%7Bandroid.getMessages(document.querySelector('%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D').innerHTML)%2CsetTimeout(message_service%2C" + BADGE_UPDATE_INTERVAL + ")%7Dtry%7Bmessage_service()%7Dcatch(_)%7B%7D%7D)()");
    }

    public static void updateMessages(WebView view) {
        // Run the message check once
        view.loadUrl("javascript:(function()%7Btry%7Bandroid.getMessages(document.querySelector('%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D').innerHTML)%7Dcatch(_)%7B%7D%7D)()");
    }

    public static void paramLoader(WebView view, String url) {
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
        sanitizer.parseUrl(url);
        String param = sanitizer.getValue("pageload");
        if (param != null) {
            switch (param) {
                case "composer":
                    view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7B%7D%7D)()");
                    break;
                default:
                    break;
            }
        }

    }

    public static void loadCSS(WebView view, String css) {
        // Inject CSS string to the HEAD of the webpage
        view.loadUrl("javascript:(function()%7Bvar%20styles%3Ddocument.createElement(%22style%22)%3Bstyles.innerHTML%3D%22" + css + "%22%2Cdocument.getElementsByTagName(%22head%22)%5B0%5D.appendChild(styles)%2Candroid.loadingCompleted()%7D)()");
    }
}
