package com.jkhteam.jkh_monitoring.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Created by npcipav on 29.11.2015.
 *
 * Represents news in code.
 */
public class News {
    private Date mDate;
    private String mSource;
    private boolean mIsRelatedToUser;
    private String mText;

    public News(String date, String source, boolean relatedToUser, String text) {
        try {
            mDate = DateFormat.getDateInstance().parse(date);
        } catch (ParseException e) {
            mDate = new Date();
        }
        mSource = source;
        mIsRelatedToUser = relatedToUser;
        mText = text;
    }

    public Date getDate() {return mDate;}
    public String getSource() {return mSource;}
    public boolean isRelatedToUser() {return mIsRelatedToUser;}
    public String getText() {return mText;}
}
