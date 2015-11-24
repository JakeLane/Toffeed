package me.jakelane.wrapperforfacebook;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
    private WebView myWebView;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load the WebView
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setWebViewClient(new MyWebViewClient());

        // Settings
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("http://m.facebook.com/");
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().contains("facebook.com")) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }

        public void onPageFinished(WebView view, String url) {
//            myWebView.loadUrl("javascript:(function(){document.querySelector('div._129-').style.display = 'none';})();");
            myWebView.loadUrl("javascript:(function(){document.getElementById('page').style.top = '-45px';})();");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_news) {
            myWebView.loadUrl("javascript:(function(){document.querySelector('#feed_jewel > a').click();})();");
            item.setChecked(true);
        } else if (id == R.id.nav_friendreq) {
            myWebView.loadUrl("javascript:(function(){document.querySelector('#requests_jewel > a').click();})();");
            item.setChecked(true);
        } else if (id == R.id.nav_messages) {
            myWebView.loadUrl("javascript:(function(){document.querySelector('#messages_jewel > a').click();})();");
            item.setChecked(true);
        } else if (id == R.id.nav_notifications) {
            myWebView.loadUrl("javascript:(function(){document.querySelector('#notifications_jewel > a').click();})();");
            item.setChecked(true);
        } else if (id == R.id.nav_search) {
            myWebView.loadUrl("javascript:(function(){document.querySelector('#search_jewel > a').click();})();");
            item.setChecked(true);
        } else if (id == R.id.nav_mainmenu) {
            myWebView.loadUrl("javascript:(function(){document.querySelector('#bookmarks_jewel > a').click();})();");
            item.setChecked(true);
        } else if (id == R.id.nav_reload) {
            myWebView.reload();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
