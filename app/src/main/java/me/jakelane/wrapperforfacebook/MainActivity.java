package me.jakelane.wrapperforfacebook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mikepenz.actionitembadge.library.ActionItemBadge;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Members
    WebView wrapperWebView;
    NavigationView navigationView;
    private MenuItem notificationButton;
    private SharedPreferences preferences;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        PreferenceChangeListener preferenceListener = new PreferenceChangeListener();
        preferences.registerOnSharedPreferenceChangeListener(preferenceListener);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (!preferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false)) {
            navigationView.getMenu().findItem(R.id.nav_messages).setVisible(false);
        }
        if (!preferences.getBoolean(SettingsActivity.KEY_PREF_BACK_BUTTON, false)) {
            navigationView.getMenu().findItem(R.id.nav_back).setVisible(false);
        }

        // Load the WebView
        wrapperWebView = (WebView) findViewById(R.id.webview);

        WebViewClient client = new WrapperWebViewClient() {
            @Override
            public void launchExternalBrowser(String url) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        };
        wrapperWebView.setWebViewClient(client);
        wrapperWebView.addJavascriptInterface(new JavaScriptInterfaces(this), "android");

        // Web Settings
        WebSettings webSettings = wrapperWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Get the url to start with
        wrapperWebView.loadUrl(chooseUrl());
    }

    private String chooseUrl() {
        // Handle intents
        Intent intent = getIntent();

        // Open the proper URL in case the user clicked on a link that brought us here
        if (intent.getData() != null) {
            return intent.getData().toString();
        }

        // If nothing has happened at this point, we want the default url
        return "https://m.facebook.com/";
    }

    // Handle preferences changes
    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            if (key.equals(SettingsActivity.KEY_PREF_BACK_BUTTON)) {
                navigationView.getMenu().findItem(R.id.nav_back).setVisible(preferences.getBoolean(SettingsActivity.KEY_PREF_BACK_BUTTON, false));
            } else if (key.equals(SettingsActivity.KEY_PREF_MESSAGING)) {
                navigationView.getMenu().findItem(R.id.nav_messages).setVisible(preferences.getBoolean(SettingsActivity.KEY_PREF_MESSAGING, false));
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (wrapperWebView.canGoBack()) {
            wrapperWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        notificationButton = menu.findItem(R.id.action_notifications);

        ActionItemBadge.update(this, notificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_notifications, null), ActionItemBadge.BadgeStyles.RED, Integer.MIN_VALUE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here
        int id = item.getItemId();
        if (id == R.id.action_notifications) {
            wrapperWebView.loadUrl("javascript:try{document.querySelector('#search_jewel > a').click();}catch(e){window.location.href='https://m.facebook.com/notifications.php';}");

            // Uncheck other menu items (sorry)
            for (int i = 0; i < navigationView.getMenu().size(); i++) {
                if (navigationView.getMenu().getItem(i).isChecked()) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
            }
        }

        // Update the notifications
        JavaScriptHelpers.updateNotifications(wrapperWebView);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_news:
                wrapperWebView.loadUrl("javascript:try{document.querySelector('#feed_jewel > a').click();}catch(e){window.location.href='https://m.facebook.com/home.php';}");
                item.setChecked(true);
                break;
            case R.id.nav_friendreq:
                wrapperWebView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='https://m.facebook.com/friends/center/requests/';}");
                item.setChecked(true);
                break;
            case R.id.nav_messages:
                wrapperWebView.loadUrl("javascript:try{document.querySelector('#messages_jewel > a').click();}catch(e){window.location.href='https://m.facebook.com/messages/';}");
                item.setChecked(true);
                break;
            case R.id.nav_search:
                wrapperWebView.loadUrl("javascript:try{document.querySelector('#search_jewel > a').click();}catch(e){window.location.href='https://m.facebook.com/search/';}");
                item.setChecked(true);
                break;
            case R.id.nav_mainmenu:
                wrapperWebView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='https://m.facebook.com/home.php';}");
                item.setChecked(true);
                break;
            case R.id.nav_back:
                wrapperWebView.goBack();
                break;
            case R.id.nav_reload:
                wrapperWebView.reload();
                break;
            case R.id.nav_forward:
                wrapperWebView.goForward();
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
            ActionItemBadge.update(notificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_notifications_active, null), num);
        } else {
            // Hide the badge and show the washed-out button
            ActionItemBadge.update(notificationButton, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu_notifications, null), Integer.MIN_VALUE);
        }
    }
}
