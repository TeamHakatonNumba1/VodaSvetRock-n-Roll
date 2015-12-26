package com.jkhteam.jkh_monitoring.model;

import android.util.Log;

import com.jkhteam.jkh_monitoring.activities.MainActivity;

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

    public NewsCollector() {
        Log.d(MainActivity.LOGTAG, "collector of news created");
        mNewsList = new LinkedList<News>();
        mParsers = new LinkedList<AbstractSiteParser>();
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
        return mNewsList;
    }

    /**
     * Check user location.
     * @return User location.
     */
    String getUserLocation() {
		//TODO! ������� ��������� userLocation �� ����������
	return "Ворошиловский район";
    }
}
