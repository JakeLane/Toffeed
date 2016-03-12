package me.jakelane.wrapperforfacebook;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.CookieManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class NotificationService extends IntentService {
    private static final String USERAGENT = System.getProperty("http.agent");
    private static final int NOTIFICATION_ID = 0;
    private static final int MESSAGE_ID = 0;

    private SharedPreferences mPreferences;
    private String lastNotificationID = "";
    private String lastMessageID = "";

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(Helpers.LogTag, "Notification alarm running");
        try {
            Elements navbar = Jsoup.connect(MainActivity.FACEBOOK_URL_BASE).userAgent(USERAGENT).timeout(10000)
                    .cookie(MainActivity.FACEBOOK_URL_BASE, CookieManager.getInstance().getCookie(MainActivity.FACEBOOK_URL_BASE)).get()
                    .select("div#mJewelNav");

            if (mPreferences.getBoolean("notifications_enabled", false)) {
                Log.v(Helpers.LogTag, "Attempting to find notifications");
                Elements notificationJewel = navbar.select("div#notifications_jewel");
                int notificationNumber = getServiceNumber(notificationJewel);

                Element notificationElements = notificationJewel.select("div#notifications_flyout").select("ol[data-sigil=contents]").get(0);

                String firstNotificationText = null;
                Uri link = null;
                String notificationID = "";
                for (org.jsoup.nodes.Element element : notificationElements.children()) {
                    if (element.hasClass("aclb")) {
                        Log.v(Helpers.LogTag, "Found 1st notification");
                        // If notification is unread (this is unclicked, not necessarily unseen)
                        firstNotificationText = element.children().select(".c").text();
                        Log.v(Helpers.LogTag, firstNotificationText);
                        link = Uri.parse(MainActivity.FACEBOOK_URL_BASE + element.children().select("div > a").attr("href"));
                        Log.v(Helpers.LogTag, link.toString());
                        notificationID = element.id();
                        Log.v(Helpers.LogTag, notificationID);
                        break;
                    }
                }

                if (notificationNumber > 0 && !lastNotificationID.equals(notificationID)) {
                    // There is a notification, and it has not already been posted
                    lastNotificationID = notificationID;
                    sendNotification(false, notificationNumber, firstNotificationText, link);
                }
            }

            if (mPreferences.getBoolean("message_notifications_enabled", false)) {
                Log.v(Helpers.LogTag, "Attempting to find messages");
                Elements messagesJewel = navbar.select("div#messages_jewel");
                int messagesNumber = getServiceNumber(messagesJewel);

                String firstMessageText = null;
                Uri link = null;
                String messageID = null;
                Element messagesElements = messagesJewel.select("div.flyout").select("li[data-sigil=marea]").get(0);
                for (org.jsoup.nodes.Element element : messagesElements.children()) {
                    if (element.child(0).hasClass("aclb")) {
                        // If notification is unread (this is unclicked, not necessarily unseen)
                        firstMessageText = element.child(0).children().select(".oneLine").text();
                        link = Uri.parse(MainActivity.FACEBOOK_URL_BASE + element.child(0).children().select("a").attr("href"));
                        messageID = element.child(0).id();
                        break;
                    }
                }

                if (messagesNumber > 0 && !lastMessageID.equals(messageID)) {
                    lastMessageID = messageID;
                    sendNotification(true, messagesNumber, firstMessageText, link);
                }
            }

            Log.i(Helpers.LogTag, "Completed notification check");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Helpers.LogTag, "Failed to check notifications");
        }
    }

    private int getServiceNumber(Elements jewel) {
        String notificationCountString = jewel.select("a > div > span[data-sigil=count]").text();
        if (notificationCountString.length() > 0 && android.text.TextUtils.isDigitsOnly(notificationCountString)) {
            return Integer.valueOf(notificationCountString);
        } else {
            return 0;
        }
    }

    private void sendNotification(boolean isMessage, int number, String text, Uri link) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setSmallIcon(R.drawable.notify_logo)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setDefaults(-1);

        // Intent depends on context
        Intent resultIntent;

        if (number > 1) {
            // If there are multiple notifications, mention the number
            String multiple_text;
            if (isMessage) {
                multiple_text = getString(R.string.message_multiple_text, number);
            } else {
                multiple_text = getString(R.string.notification_multiple_text, number);
            }
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(multiple_text)).setContentText(multiple_text);

            // Set the url to the notification centre
            resultIntent = new Intent(this, MainActivity.class);
            if (isMessage) {
                resultIntent.setData(Uri.parse(MainActivity.FACEBOOK_URL_BASE + "messages/"));
            } else {
                resultIntent.setData(Uri.parse(MainActivity.FACEBOOK_URL_BASE + "notifications/"));
            }

        } else {
            // Set the title
            mBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(text)).setContentText(text);

            // View all notifications/messages button
            Intent viewNotificationsIntent = new Intent(this, MainActivity.class);
            if (isMessage) {
                viewNotificationsIntent.setData(Uri.parse(MainActivity.FACEBOOK_URL_BASE + "messages/"));
            } else {
                viewNotificationsIntent.setData(Uri.parse(MainActivity.FACEBOOK_URL_BASE + "notifications/"));
            }

            PendingIntent pendingViewNotifications = PendingIntent.getActivity(getApplicationContext(), 0, viewNotificationsIntent, 0);
            if (isMessage) {
                mBuilder.addAction(R.drawable.ic_menu_notifications_active, getString(R.string.message_view_all), pendingViewNotifications);
            } else {
                mBuilder.addAction(R.drawable.ic_menu_notifications_active, getString(R.string.notification_view_all), pendingViewNotifications);
            }

            // Creates an explicit intent for an Activity in your app
            resultIntent = new Intent(this, MainActivity.class);
            resultIntent.setData(link);
        }

        // Notification Priority (make LED blink)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        // Create TaskStack to ensure correct back button behaviour
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        // Build the notification
        Notification notification = mBuilder.build();

        // Set the LED colour
        notification.ledARGB = ContextCompat.getColor(this, R.color.colorPrimary);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (isMessage) {
            mNotificationManager.notify(MESSAGE_ID, notification);
        } else {
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }

        Log.i(Helpers.LogTag, "Notification posted");
    }
}