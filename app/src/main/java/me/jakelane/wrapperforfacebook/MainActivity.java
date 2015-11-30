package me.jakelane.wrapperforfacebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;

import java.util.Arrays;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String FACEBOOK_URL_BASE = "https://m.facebook.com/";
    private static final List<String> HOSTNAMES = Arrays.asList("facebook.com", "*.facebook.com");
    private final BadgeStyle BADGE_GRAY_FULL = new BadgeStyle(BadgeStyle.Style.LARGE, R.layout.menu_badge_full, Color.parseColor("#8A000000"), Color.parseColor("#8A000000"), Color.WHITE);

    // Members
    private AdvancedWebView mWebView;
    NavigationView mNavigationView;
    private MenuItem mNotificationButton;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceChangeListener preferenceListener = new PreferenceChangeListener();
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener);

        // Setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup the DrawLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        // Create the badge for messages
        ActionItemBadge.update(this, mNavigationView.getMenu().findItem(R.id.nav_messages), (Drawable) null, BADGE_GRAY_FULL, Integer.MIN_VALUE);

        // Hide buttons if they are disabled
        if (!preferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_messages).setVisible(false);
        }
        if (!preferences.getBoolean(SettingsActivity.KEY_PREF_BACK_BUTTON, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_back).setVisible(false);
        }

        // Load the WebView
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.addPermittedHostnames(HOSTNAMES);
        mWebView.setGeolocationEnabled(preferences.getBoolean(SettingsActivity.KEY_PREF_LOCATION, false));

        mWebView.setListener(this, new WebViewListener(this, mWebView));
        mWebView.addJavascriptInterface(new JavaScriptInterfaces(this), "android");

        // Get the url to start with
        mWebView.loadUrl(chooseUrl());
    }

    private String chooseUrl() {
        // Handle intents
        Intent intent = getIntent();

        // If there is a intent containing a facebook link, go there
        if (intent.getData() != null) {
            return intent.getData().toString();
        }

        // If nothing has happened at this point, we want the default url
        return FACEBOOK_URL_BASE;
    }

    // Handle preferences changes
    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            switch (key) {
                case SettingsActivity.KEY_PREF_BACK_BUTTON:
                    mNavigationView.getMenu().findItem(R.id.nav_back).setVisible(preferences.getBoolean(SettingsActivity.KEY_PREF_BACK_BUTTON, false));
                    break;
                case SettingsActivity.KEY_PREF_MESSAGING:
                    mNavigationView.getMenu().findItem(R.id.nav_messages).setVisible(preferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false));
                    break;
                case SettingsActivity.KEY_PREF_LOCATION:
                    mWebView.setGeolocationEnabled(preferences.getBoolean(SettingsActivity.KEY_PREF_LOCATION, false));
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mNotificationButton = menu.findItem(R.id.action_notifications);

        ActionItemBadge.update(this, mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_notifications, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_notifications) {
            // Load the notification page
            mWebView.loadUrl("javascript:try{document.querySelector('#notifications_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK_URL_BASE + "notifications.php';}");

            // Uncheck other menu items (sorry)
            for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
                if (mNavigationView.getMenu().getItem(i).isChecked()) {
                    mNavigationView.getMenu().getItem(i).setChecked(false);
                }
            }
        }

        // Update the notifications
        JavaScriptHelpers.updateNotifications(mWebView);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_news:
                mWebView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK_URL_BASE + "home.php';}");
                item.setChecked(true);
                break;
            case R.id.nav_friendreq:
                mWebView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK_URL_BASE + "friends/center/requests/';}");
                item.setChecked(true);
                break;
            case R.id.nav_messages:
                mWebView.loadUrl("javascript:try{document.querySelector('#messages_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK_URL_BASE + "messages/';}");
                JavaScriptHelpers.updateMessages(mWebView);
                item.setChecked(true);
                break;
            case R.id.nav_search:
                mWebView.loadUrl("javascript:try{document.querySelector('#search_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK_URL_BASE + "search/';}");
                item.setChecked(true);
                break;
            case R.id.nav_mainmenu:
                mWebView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK_URL_BASE + "home.php';}");
                item.setChecked(true);
                break;
            case R.id.nav_back:
                mWebView.goBack();
                break;
            case R.id.nav_reload:
                mWebView.reload();
                break;
            case R.id.nav_forward:
                mWebView.goForward();
                break;
            case R.id.nav_settings:
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setNotificationNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_notifications_active, null), num);
        } else {
            // Hide the badge and show the washed-out button
            ActionItemBadge.update(mNotificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_notifications, null), Integer.MIN_VALUE);
        }
    }

    public void setMessagesNum(int num) {
        if (num > 0) {
            ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_messages), num);
        } else {
            // Hide the badge and show the washed-out button
            ActionItemBadge.update(mNavigationView.getMenu().findItem(R.id.nav_messages), Integer.MIN_VALUE);
        }
    }
}
