package com.jkhteam.jkh_monitoring.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jkhteam.jkh_monitoring.model.ElectricSupplySiteParser;
import com.jkhteam.jkh_monitoring.model.News;
import com.jkhteam.jkh_monitoring.R;
import com.jkhteam.jkh_monitoring.model.WaterSupplySiteParser;
import com.jkhteam.jkh_monitoring.services.BackgroundService;
import com.jkhteam.jkh_monitoring.support.SlidingTabLayout;
import com.jkhteam.jkh_monitoring.adapters.VPAdapterMain;

public class MainActivity extends ActionBarActivity implements ServiceConnection {

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_FIRST_RUN = "first_run";
    public static final String GET_WATERLIST = "get_waterlist";
    public static final String GET_POWERLIST = "get_powerlist";
    private static List<News> newsList = new ArrayList<>();
    private SharedPreferences prefs;

    public static final String LOGTAG = "MainActivity";
    private Messenger mServiceMessenger = null;
    private ServiceConnection mConnection = this;
    private final Messenger mMessenger = new Messenger(new IncomingMessageHandler());

    private static List<News> mNewsList;
    private boolean mIsBound = false;

    CharSequence Titles[]={"Вода","Электричество"};
    int Numboftabs =2;

    private VPAdapterMain adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            LinearLayout linearLayout1 = (LinearLayout)findViewById(R.id.ll1);
            LinearLayout linearLayout2 = (LinearLayout)findViewById(R.id.ll2);
            linearLayout1.setBackgroundColor(0xEFEDEB);
            linearLayout1.setBackgroundColor(0xEFEDEB);
        }*/
        Log.d(LOGTAG, "Starting activity...");


        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences(APP_PREFERENCES, MODE_PRIVATE);

        Toolbar toolbar;
        ViewPager pager;
        SlidingTabLayout tabs;

        //TODO - Dynamic ToolBar
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_settings) {
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
                return true;
            }
        });

        adapter =  new VPAdapterMain(getSupportFragmentManager(),Titles,Numboftabs);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        tabs.setViewPager(pager);
        autobind();
        requestNews();
    }

    public static List<News> getNews(String val) {
        if (mNewsList == null) return null;
        List<News> tmp = new LinkedList<>();
        switch (val) {
            case WaterSupplySiteParser.SOURCE_CODE:
                for (News news: mNewsList) {
                    if (news.getSource() == WaterSupplySiteParser.SOURCE_CODE)
                        tmp.add(news);
                }
                return tmp;
            case ElectricSupplySiteParser.SOURCE_CODE:
                    for (News news : mNewsList) {
                        if (news.getSource() == ElectricSupplySiteParser.SOURCE_CODE)
                            tmp.add(news);
                    }

                return tmp;
            default:
                return tmp;
        }
    }

    private void autobind() {
        if (!BackgroundService.isRunning()) {
            Intent intent = new Intent(MainActivity.this, BackgroundService.class);
            startService(intent);
        }
        doBindService();
    }

    /**
     * Bind Service to this Activity.
     */
    private void doBindService() {
        Intent intent = new Intent(this, BackgroundService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    /**
     * Unbind Service to this Activity.
     */
    private void doUnbindService() {
        if (mIsBound && mServiceMessenger != null) {
            try {
                // Request the service to unregister the activity.
                Message msg = Message.obtain(null, BackgroundService.MSG_UNREGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                // That means that the service get crashed. We just do nothing.
            }
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    /**
     * Send request to update news and launch view updating from Service.
     */
    private void requestNews() {
        Log.d(LOGTAG, "Requesting news.");
        if (mIsBound && mServiceMessenger != null) {
            try {
                Message msg = Message.obtain(null, BackgroundService.MSG_GET_NEWS);
                msg.replyTo = mMessenger;
                mServiceMessenger.send(msg);
                findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
            } catch (RemoteException e) {
                // As usual we do nothing.
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mServiceMessenger = new Messenger(service);
        try {
            Message msg = Message.obtain(null, BackgroundService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            // They said that service should got crashed before we could do anything with it.
            // So let it be blank.
        }
        requestNews(); // TODO!
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // This is called when the connection with the service has been UNEXPECTEDLY disconnected -
        // process crashed.
        mServiceMessenger = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            doUnbindService();
        } catch (Throwable t) {
            Log.e(LOGTAG, "Failed to unbind from the service ", t);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Handles incoming messages from Service.
     */
    class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Log.d(LOGTAG, "Handling message.");
            switch (msg.what) {
                case BackgroundService.MSG_POST_NEW_DATA:
                    // Get serializable news from service.
                    Bundle bundle = msg.getData();
                    // Manage serializable object.
                    mNewsList = (LinkedList<News>)bundle.getSerializable("value");
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.getBoolean(APP_PREFERENCES_FIRST_RUN, true)){
            startActivity(new Intent(MainActivity.this, FirstRunActivity.class));
            prefs.edit().putBoolean(APP_PREFERENCES_FIRST_RUN, false).apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}