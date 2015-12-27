package com.jkhteam.jkh_monitoring.model;

import android.util.Log;

import com.jkhteam.jkh_monitoring.activities.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
/**
 * Created by npcipav on 02.12.2015.
 */
public abstract class AbstractSiteParser {

    protected NewsCollector mCollector;

	private static final String LOGTAG = "Parsing";
    public AbstractSiteParser(NewsCollector collector) {
        mCollector = collector;
    }

    abstract void refreshNews();
	
	protected boolean isRelatedToUser(String street){
			try{
				Log.d(LOGTAG, "Checking relation to user of " + street);
				String geocodeURL = "https://geocode-maps.yandex.ru/1.x/?geocode=";	
				geocodeURL = geocodeURL.concat(street);
				geocodeURL = geocodeURL.concat("+Ростов");
				geocodeURL.replace(" ","+");
				//Получаем координаты по URL
				Document geocoder = Jsoup.connect(geocodeURL).get();
				Element geocodeE = geocoder.select("pos").first();
				//TODO! нафигарить проверку на прохождение геокодинга, убрать костыль с координатами ВМ
				String geocode = "39.717178,47.226258";
				if (geocodeE != null){
				geocode = geocodeE.text();
				}
				//Обратный геокодинг (получаем район)
				//формируем URL
				geocodeURL = "https://geocode-maps.yandex.ru/1.x/?geocode=";	
				geocodeURL = geocodeURL.concat(geocode);
				geocodeURL.replace(" ",",");
				geocodeURL = geocodeURL.concat("&kind=district");
				//Получаем район по координатам
				geocoder = Jsoup.connect(geocodeURL).get();
				String uLocation = "микрорайон Северный";
				boolean isRelated = false;
				if (geocoder.select("DependentLocalityName").first()!=null){
					Elements geocodeEC = geocoder.select("DependentLocalityName");
					for (Element districtE: geocodeEC){
						isRelated = isRelated || (districtE.text().equals(mCollector.getUserLocation()));
					}
				}
				Log.d(LOGTAG, "Relation is " + Boolean.toString(isRelated));
				return isRelated;
			} catch (IOException e) {
				return false;
		}
	}
}
