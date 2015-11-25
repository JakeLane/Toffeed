package me.jakelane.wrapperforfacebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Members
    private WebView wrapperWebView;
    NavigationView navigationView;
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Create a handler to provide access to the UI thread
        mHandler = new Handler();

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

        // Settings
        WebSettings webSettings = wrapperWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wrapperWebView.loadUrl("http://m.facebook.com/");
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            wrapperWebView.loadUrl("javascript:(function(){document.querySelector('#feed_jewel > a').click();})();");
        } else if (id == R.id.nav_friendreq) {
            wrapperWebView.loadUrl("javascript:(function(){document.querySelector('#requests_jewel > a').click();})();");
        } else if (id == R.id.nav_messages) {
            wrapperWebView.loadUrl("javascript:(function(){document.querySelector('#messages_jewel > a').click();})();");
        } else if (id == R.id.nav_notifications) {
            wrapperWebView.loadUrl("javascript:(function(){document.querySelector('#notifications_jewel > a').click();})();");
        } else if (id == R.id.nav_search) {
            wrapperWebView.loadUrl("javascript:(function(){document.querySelector('#search_jewel > a').click();})();");
        } else if (id == R.id.nav_mainmenu) {
            wrapperWebView.loadUrl("javascript:(function(){document.querySelector('#bookmarks_jewel > a').click();})();");
        } else if (id == R.id.nav_reload) {
            wrapperWebView.reload();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
