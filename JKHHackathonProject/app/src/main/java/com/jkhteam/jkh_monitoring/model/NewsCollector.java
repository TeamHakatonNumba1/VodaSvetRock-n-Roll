package com.jkhteam.jkh_monitoring.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.jkhteam.jkh_monitoring.activities.MainActivity;
import com.jkhteam.jkh_monitoring.activities.SettingsActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by npcipav on 02.12.2015.
 *
 * Collects news and manage parsers.
 */
public class NewsCollector {

    private List<News> mNewsList;
    private List<AbstractSiteParser> mParsers;

    private SharedPreferences mPreferences;
    public NewsCollector(SharedPreferences prefs) {
        Log.d(MainActivity.LOGTAG, "collector of news created");
        mNewsList = new LinkedList<News>();
        mParsers = new LinkedList<AbstractSiteParser>();
        mPreferences = prefs;

    }

    /**
     * Add teh site parser into the parser list.
     * @param parser Parser you want to add into the parser list.
     */
    public void addSiteParser(AbstractSiteParser parser) {
        mParsers.add(parser);
    }

    /**
     * Refresh all news in parser list.
     */
    public void refreshNews() {
        for (AbstractSiteParser parser : mParsers) {
            Log.d(MainActivity.LOGTAG, "news refreshing...");
            parser.refreshNews();
        }
    }

    /**
     * Add news into the news list and resort it.
     * @param news News you want to put in news list.
     */
    public void addNews(List<News> news) {
        mNewsList.addAll(news);
        Collections.sort(mNewsList, new Comparator<News>() {
            public int compare(News o1, News o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
    }

    /**
     * Get all updated news from parsers.
     * @return Last updated news.
     */
    public List<News> getNews() {
        Log.d(MainActivity.LOGTAG, "getting news...");
        return mNewsList;
    }

    /**
     * Check user location.
     * @return User location.
     */
    String getUserLocation() {
        return mPreferences.getString("streetkey","Ворошиловский район");
    }
}
