package me.jakelane.wrapperforfacebook;

import android.webkit.WebView;

class JavaScriptHelpers {
    public static void hideMenuBar(WebView view) {
        // Hide the menu bar
        view.loadUrl("javascript:(function(){document.getElementById('page').style.top = '-45px';})();");
    }

    public static void updateCurrentTab(WebView view) {
        // Get the currently open tab and check on the navigation menu
        view.loadUrl("javascript:android.getCurrent(document.querySelector('.popoverOpen').id)");
    }

    public static void updateNotifications(WebView view) {
        // Get the notifications
        view.loadUrl("javascript:android.getNotifications(document.querySelector('#notifications_jewel').classList.contains('hasCount'))");
    }

    public static void updateUserInfo(WebView view) {
        // Get logged in info
        view.loadUrl("javascript:android.getUserInfo(document.querySelector('form#mbasic_inline_feed_composer').getElementsByClassName('profpic')[0].outerHTML)");
    }

}
