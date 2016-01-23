package me.jakelane.wrapperforfacebook;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.mikepenz.actionitembadge.library.utils.BadgeStyle;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import im.delight.android.webview.AdvancedWebView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static final String FACEBOOK_URL_BASE = "https://m.facebook.com/";
    private static final List<String> HOSTNAMES = Arrays.asList("facebook.com", "*.facebook.com");
    private final BadgeStyle BADGE_GRAY_FULL = new BadgeStyle(BadgeStyle.Style.LARGE, R.layout.menu_badge_full, Color.parseColor("#8A000000"), Color.parseColor("#8A000000"), Color.WHITE);

    // Members
    private AdvancedWebView mWebView;
    NavigationView mNavigationView;
    private MenuItem mNotificationButton;
    private SwipeRefreshLayout swipeView;
    private CallbackManager callbackManager;
    private Snackbar loginSnackbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);

        // Preferences
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
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
        if (!preferences.getBoolean(SettingsActivity.KEY_PREF_JUMP_TOP_BUTTON, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_jump_top).setVisible(false);
        }
        if (!preferences.getBoolean(SettingsActivity.KEY_PREF_BACK_BUTTON, false)) {
            mNavigationView.getMenu().findItem(R.id.nav_back).setVisible(false);
        }

        // Start the Swipe to reload listener
        swipeView = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeView.setColorSchemeResources(R.color.colorPrimary);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.reload();
            }
        });

        // Inflate the FAB
        FloatingActionButton webviewFab = (FloatingActionButton) findViewById(R.id.webviewFAB);
        webviewFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mWebView.loadUrl("javascript:try{document.querySelector('button[name=\"view_overview\"]').click();}catch(_){window.location.href='http://m.facebook.com/?loadcomposer';}");
            }
        });

        // Load the WebView
        mWebView = (AdvancedWebView) findViewById(R.id.webview);
        mWebView.addPermittedHostnames(HOSTNAMES);
        mWebView.setGeolocationEnabled(preferences.getBoolean(SettingsActivity.KEY_PREF_LOCATION, false));

        mWebView.setListener(this, new WebViewListener(this, mWebView));
        mWebView.addJavascriptInterface(new JavaScriptInterfaces(this), "android");

        mWebView.getSettings().setBlockNetworkImage(preferences.getBoolean(SettingsActivity.KEY_PREF_STOP_IMAGES, false));

        callbackManager = CallbackManager.Factory.create();

        FacebookCallback<LoginResult> loginResult = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                mWebView.loadUrl(chooseUrl());
                updateUserInfo();
            }

            @Override
            public void onCancel() {
                checkLoggedInState();
            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(mWebView, "Something went wrong, please try logging in again", Snackbar.LENGTH_LONG).show();
                Log.e(Helpers.LogTag, error.toString());
                LoginManager.getInstance().logOut();
                checkLoggedInState();
            }
        };

        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
        LoginManager.getInstance().registerCallback(callbackManager, loginResult);

        if (checkLoggedInState()) {
            mWebView.loadUrl(chooseUrl());
            updateUserInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        mWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWebView.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
            case R.id.nav_fblogin:
                LoginManager.getInstance().logInWithReadPermissions(this, Helpers.FB_PERMISSIONS);
                break;
            case R.id.nav_jump_top:
                mWebView.scrollTo(0, 0);
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
                Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsActivity);
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Handle preferences changes
    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            switch (key) {
                case SettingsActivity.KEY_PREF_JUMP_TOP_BUTTON:
                    mNavigationView.getMenu().findItem(R.id.nav_jump_top).setVisible(prefs.getBoolean(key, false));
                    break;
                case SettingsActivity.KEY_PREF_STOP_IMAGES:
                    mWebView.getSettings().setBlockNetworkImage(prefs.getBoolean(key, false));
                case SettingsActivity.KEY_PREF_BACK_BUTTON:
                    mNavigationView.getMenu().findItem(R.id.nav_back).setVisible(prefs.getBoolean(key, false));
                    break;
                case SettingsActivity.KEY_PREF_MESSAGING:
                    mNavigationView.getMenu().findItem(R.id.nav_messages).setVisible(prefs.getBoolean(key, false));
                    break;
                case SettingsActivity.KEY_PREF_LOCATION:
                    mWebView.setGeolocationEnabled(prefs.getBoolean(key, false));
                    break;
                default:
                    break;
            }
        }
    }

    public void setLoading(boolean loading) {
        // Toggle the WebView and Spinner visibility
        mWebView.setVisibility(loading ? View.GONE : View.VISIBLE);
        swipeView.setRefreshing(loading);
    }

    public boolean checkLoggedInState() {
        if (loginSnackbar != null) {
            loginSnackbar.dismiss();
        }

        if (AccessToken.getCurrentAccessToken() != null && Helpers.getCookie() != null) {
            // Not logged in (possibly logged into Facebook OAuth and/or webapp)
            mWebView.setVisibility(View.VISIBLE);
            mNavigationView.getMenu().findItem(R.id.nav_fblogin).setVisible(false);
            Log.v(Helpers.LogTag, "LOGGED IN");
            return true;
        } else {
            loginSnackbar = Helpers.loginPrompt(swipeView);
            mWebView.setVisibility(View.GONE);
            mNavigationView.getMenu().findItem(R.id.nav_fblogin).setVisible(true);

            Log.v(Helpers.LogTag, "LOGGED OUT");
            return false;
        }
    }

    private void updateUserInfo() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                // Update header
                try {
                    // Set the user's name under the header
                    ((TextView) findViewById(R.id.profile_name)).setText(object.getString("name"));

                    // Set the cover photo with resizing
                    final View header = findViewById(R.id.header_layout);
                    Picasso.with(getApplicationContext()).load(object.getJSONObject("cover").getString("source")).resize(header.getWidth(), header.getHeight()).centerCrop().into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            header.setBackground(new BitmapDrawable(getResources(), bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });

                    Picasso.with(getApplicationContext()).load("https://graph.facebook.com/" + object.getString("id") + "/picture?type=large").error(R.drawable.side_profile).into((ImageView) findViewById(R.id.profile_picture));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,cover");
        request.setParameters(parameters);
        request.executeAsync();
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
}
