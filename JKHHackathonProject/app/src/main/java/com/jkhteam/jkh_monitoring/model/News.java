package com.jkhteam.jkh_monitoring.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private final DateFormat dateFormatWater = new SimpleDateFormat("d MMMMM yyyy", new Locale("ru"));

    public News(String date, String source, boolean relatedToUser, String text) {
        try {
            if (source.equals(WaterSupplySiteParser.SOURCE_CODE)){
                mDate = dateFormatWater.parse(date);
            }else {
                mDate = DateFormat.getDateInstance().parse(date);
            }
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
