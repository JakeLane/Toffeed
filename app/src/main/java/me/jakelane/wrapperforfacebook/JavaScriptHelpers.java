package me.jakelane.wrapperforfacebook;

import android.webkit.WebView;

class JavaScriptHelpers {
    public static void hideMenuBar(WebView view) {
        // Hide the menu bar
        view.loadUrl("javascript:(function(){document.getElementById('page').style.top='-45px';})();");
    }

    public static void updateCurrentTab(WebView view) {
        // Get the currently open tab and check on the navigation menu
        view.loadUrl("javascript:try{android.getCurrent(document.querySelector('.popoverOpen').id);}catch(e){null;}");
    }

    public static void updateNotificationsService(WebView view, int interval) {
        // Start the notification service
        view.loadUrl("javascript:function notification_service(){android.getNotifications(document.querySelector('#notifications_jewel > a > div > span[data-sigil=count]').innerHTML);setTimeout(notification_service, " + interval + ");}try{notification_service();}catch(e){}");
    }

    public static void updateNotifications(WebView view) {
        // Run the notification check once
        view.loadUrl("javascript:android.getNotifications(document.querySelector('#notifications_jewel > a > div > span[data-sigil=count]').innerHTML);");
    }

    public static void updateMessagesService(WebView view, int interval) {
        // Start the message service
        view.loadUrl("javascript:function message_service(){android.getMessages(document.querySelector('#messages_jewel > a > div > span[data-sigil=count]').innerHTML);setTimeout(message_service, " + interval + ");}try{message_service();}catch(e){}");
    }

    public static void updateMessages(WebView view) {
        // Run the message check once
        view.loadUrl("javascript:android.getMessages(document.querySelector('#messages_jewel > a > div > span[data-sigil=count]').innerHTML);");
    }

    public static void updateUserInfo(WebView view) {
        // Get logged in info
        view.loadUrl("javascript:try{android.getUserInfo(document.querySelector('form#mbasic_inline_feed_composer').getElementsByClassName('profpic')[0].outerHTML)}catch(e){null;}");
    }

}
