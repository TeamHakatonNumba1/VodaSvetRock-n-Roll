package com.jkhteam.jkh_monitoring.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jkhteam.jkh_monitoring.R;
import com.jkhteam.jkh_monitoring.activities.MainActivity;
import com.jkhteam.jkh_monitoring.model.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service {

    private static final String LOGTAG = "BackgroundService";

    // Service codes:
    public static final int MSG_REGISTER_CLIENT = 1;
    public static final int MSG_UNREGISTER_CLIENT = 2;
    public static final int MSG_GET_NEWS = 3;
    // Activity codes:
    public static final int MSG_POST_NEW_DATA = 1;

    final Messenger mMessenger = new Messenger(new IncomingMessageHandler());

    private Timer mTimer;
    private Messenger mClientMessenger = null;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    private static boolean mIsRunning = false;
    private NewsCollector mNewsCollector;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOGTAG, "Service started");
        // Create news collector and add parsers.
        mNewsCollector = new NewsCollector();
        AbstractSiteParser parser = new ElectricSupplySiteParser(mNewsCollector);
        mNewsCollector.addSiteParser(parser);
        parser = new WaterSupplySiteParser(mNewsCollector);
        mNewsCollector.addSiteParser(parser);
        // For notifications.
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        mNotificationBuilder = new NotificationCompat.Builder(this);
        (new NewsNotificationBackgroundRefresher()).execute();
        // Extract updating time from preferences.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int mins = preferences.getInt("update_time", 0);
        Log.d(LOGTAG, "Autorefresh time was setted to " + mins + ".");
        mins = mins != 0 ? mins : 1; // TODO! Create preference with real values.
        // In next section we pretend to set a timer and news updating...
        // Set timer for automatic news updating.
        //mTimer = new Timer();
        // We're gonna repeat our task updating news, each <mins> minutes.
        //mTimer.scheduleAtFixedRate(new NewsUpdateTimerTask(), 0, mins * 60 * 1000);
        mIsRunning = true;
    }

    public static boolean isRunning() {
        return mIsRunning;
    }

    /**
     * Displays a notification in the notification bar.
     */
    private void showNotification(News news) {
        String msg = "Showing news notification. Date: " + news.getDate().toString() + "; Text :"
                + news.getText() + "; Relation to user: " + news.isRelatedToUser() + "; Source: "
                + news.getSource();
        Log.d(LOGTAG, msg);
        // Update title, text and icon for the new notification.
        String notificationTitle = news.getSource().equals(ElectricSupplySiteParser.SOURCE_CODE) ?
                "Электричество" : "Вода";
        int iconId = news.getSource().equals(ElectricSupplySiteParser.SOURCE_CODE) ?
                R.drawable.notification_icon_electro :
                R.drawable.notification_icon_water;
        mNotificationBuilder.setContentTitle(notificationTitle)
                .setContentText(news.getText())
                .setSmallIcon(iconId);
        Intent intent = new Intent(this, MainActivity.class);
        // To create multiple notifications we need to create unique id for each one.
        int uniqueId = news.hashCode();
        Log.d(LOGTAG, "Posting notification...\nUnique notification id: " +
                Integer.toString(uniqueId));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mNotificationBuilder.setContentIntent(pendingIntent);
        mNotificationManager.notify(uniqueId, mNotificationBuilder.build());
    }

    /**
     * Put news list into a bundle.
     * @param news The list of news we want to pack.
     * @return The bundle news object.
     */
    private Bundle packNews(List<News> news) {
        Bundle bundle = new Bundle();
        // All standard implementation of List interface already implements Serializable interface
        // so to serialize our list we should cast it to its real type.
        bundle.putSerializable("value", (LinkedList<News>) news);
        Log.d(LOGTAG, "News packed.");
        return bundle;
    }

    /**
     * Sends a news bundle (news in serialized representation) to the activity.
     */
    private void sendNewsToUI(Bundle bundle) {
        try {
            Message msg = Message.obtain(null, MSG_POST_NEW_DATA);
            msg.setData(bundle);
            mClientMessenger.send(msg);
        } catch (RemoteException e) {
            // That means that client is dead so we just delete it then.
            mClientMessenger = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOGTAG, "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOGTAG, "onBind");
        return mMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) mTimer.cancel();
        mNotificationManager.cancelAll();
        Log.d(LOGTAG, "Service stopped");
        mIsRunning = false;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Handles incoming messages from the activity.
     */
    class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(LOGTAG, "Handled message code: " + msg.what);
            switch (msg.what) {
                case MSG_REGISTER_CLIENT:
                    mClientMessenger = msg.replyTo;
                    break;
                case MSG_UNREGISTER_CLIENT:
                    mClientMessenger = null;
                    break;
                case MSG_GET_NEWS:
                    Log.d(LOGTAG, "Handling msg_get_news.");
                    // As refreshing news is hard work we should put it in new thread.
                    (new NewsBackgroundRefresher()).execute();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Refreshes news in new thread.
     *
     * Used in IncomingMessageHandler.
     */
    class NewsBackgroundRefresher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mNewsCollector.refreshNews();
            List<News> news = mNewsCollector.getNews();
            sendNewsToUI(packNews(news));
            return null;
        }
    }

    /**
     * Updates news and displays notifications.
     */
    /*class NewsUpdateTimerTask extends TimerTask {
        @Override
        synchronized public void run() {
            mNewsCollector.refreshNews();
            // TODO! Show related news in notifications.
            // For now it shows all of them.
            for (News news : mNewsCollector.getNews()) {
                showNotification(news);
            }
        }
    }*/
    class NewsNotificationBackgroundRefresher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Log.d(LOGTAG, "Refreshing news (called by timer)...");
            mNewsCollector.refreshNews();
            for (News news : mNewsCollector.getNews()) {
                // If the news item is related to user and we did not post its notification
                // then we show it.
                if (news.isRelatedToUser())
                    showNotification(news);
            }
            return null;
        }
    }
}
