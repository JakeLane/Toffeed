package me.jakelane.wrapperforfacebook;

import android.net.UrlQuerySanitizer;
import android.webkit.WebView;

class JavaScriptHelpers {
    private static final int BADGE_UPDATE_INTERVAL = 15000;

    public static void updateCurrentTab(WebView view) {
        // Get the currently open tab and check on the navigation menu
        view.loadUrl("javascript:(function()%7Btry%7Bandroid.getCurrent(document.querySelector('.popoverOpen').id)%7Dcatch(_)%7Bandroid.getCurrent('null')%7D%7D)()");
    }

    public static void mostRecentButton(WebView view) {
        // Show a Most Recent button on the News Feed
        view.loadUrl("javascript:(function()%7Bdocument.querySelector(%22._59e9%22)%7C%7C(document.querySelector(%22._181j%22).innerHTML%3D'%3Cdiv%20class%3D%22_59e9%20_55wr%20_4g33%20_400s%22%3E%3Cdiv%20class%3D%22_52jh%20_4g34%22%3E%3Ca%20href%3D%22%2Fhome.php%3Fsk%3Dh_chr%26amp%3Brefid%3D7%22%20class%3D%22sub%22%3E'%2Bdocument.querySelector(%22span%5Bdata-sigil%3Dmost_recent_bookmark%5D%22).innerHTML%2B%22%3C%2Fa%3E%3C%2Fdiv%3E%3C%2Fdiv%3E%22%2Bdocument.querySelector(%22._181j%22).innerHTML)%7D)()");
    }

    public static void updateNums(WebView view) {
        view.loadUrl("javascript:(function()%7Bandroid.getNums(document.querySelector(%22%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23requests_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML)%7D)()");
    }

    public static void updateNumsService(WebView view) {
        view.loadUrl("javascript:(function()%7Bfunction%20n_s()%7Bandroid.getNums(document.querySelector(%22%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23requests_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML)%2CsetTimeout(n_s%2C" + BADGE_UPDATE_INTERVAL + ")%7Dtry%7Bn_s()%7Dcatch(_)%7B%7D%7D)()");
    }

    public static void paramLoader(WebView view, String url) {
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
        sanitizer.setAllowUnregisteredParamaters(true);
        sanitizer.parseUrl(url);
        String param = sanitizer.getValue("pageload");
        if (param != null) {
            switch (param) {
                case "composer":
                    view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7B%7D%7D)()");
                    break;
                case "composer_photo":
                    view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7B%7D%7D)()");
                    break;
                case "composer_checkin":
                    view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7B%7D%7D)()");
                    break;
                default:
                    break;
            }
        }

    }

    public static void loadCSS(WebView view, String css) {
        // Inject CSS string to the HEAD of the webpage
        view.loadUrl("javascript:(function()%7Bvar%20styles%3Ddocument.createElement('style')%3Bstyles.innerHTML%3D'" + css + "'%2Cstyles.onload%3Dandroid.loadingCompleted()%2Cdocument.getElementsByTagName('head')%5B0%5D.appendChild(styles)%7D)()");
    }
}
