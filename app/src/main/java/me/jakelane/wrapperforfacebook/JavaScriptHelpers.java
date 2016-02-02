package me.jakelane.wrapperforfacebook;

import android.webkit.WebView;

class JavaScriptHelpers {
    private static final int BADGE_UPDATE_INTERVAL = 30000;

    public static void hideMenuBar(WebView view) {
        // Hide the menu bar
        view.loadUrl("javascript:try{if(!document.URL.match('facebook\\.com\\/composer')){document.getElementById('page').style.top='-45px';android.isComposer(false)}else{android.isComposer(true)}}catch(e){}android.loadingCompleted();");
    }

    public static void updateCurrentTab(WebView view) {
        // Get the currently open tab and check on the navigation menu
        view.loadUrl("javascript:try{android.getCurrent(document.querySelector('.popoverOpen').id);}catch(e){android.getCurrent('null');}");
    }

    public static void updateNotificationsService(WebView view) {
        // Start the notification service
        view.loadUrl("javascript:function notification_service(){android.getNotifications(document.querySelector('#notifications_jewel > a > div > span[data-sigil=count]').innerHTML);setTimeout(notification_service, " + BADGE_UPDATE_INTERVAL + ");}try{notification_service();}catch(e){}");
    }

    public static void updateNotifications(WebView view) {
        // Run the notification check once
        view.loadUrl("javascript:android.getNotifications(document.querySelector('#notifications_jewel > a > div > span[data-sigil=count]').innerHTML);");
    }

    public static void updateMessagesService(WebView view) {
        // Start the message service
        view.loadUrl("javascript:function message_service(){android.getMessages(document.querySelector('#messages_jewel > a > div > span[data-sigil=count]').innerHTML);setTimeout(message_service, " + BADGE_UPDATE_INTERVAL + ");}try{message_service();}catch(e){}");
    }

    public static void updateMessages(WebView view) {
        // Run the message check once
        view.loadUrl("javascript:android.getMessages(document.querySelector('#messages_jewel > a > div > span[data-sigil=count]').innerHTML);");
    }

    public static void loadComposer(WebView view) {
        // Load the composer if there is the 'loadcomposer' param
        view.loadUrl("javascript:if(location.search=='?loadcomposer'){document.querySelector('button[name=\"view_overview\"]').click();};");
    }
}
