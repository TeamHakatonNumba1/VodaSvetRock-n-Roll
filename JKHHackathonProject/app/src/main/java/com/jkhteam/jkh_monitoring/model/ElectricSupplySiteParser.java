package com.jkhteam.jkh_monitoring.model;

import android.util.Log;

import com.jkhteam.jkh_monitoring.activities.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.LinkedList;
import java.util.List;
import java.io.IOException;

/**
 * Created by npcipav on 02.12.2015.
 *
 * Parses news from the site of DonEnergo.
 * Url: http://www.donenergo.ru/consumer/graphs_of_the_time_constraints/40/1/
 */
public class ElectricSupplySiteParser extends AbstractSiteParser {

    public static final String SOURCE_CODE = "electric_supply";
    private static final String URL = "http://www.donenergo.ru/consumer/graphs_of_the_time_constraints/40/1/";

    public ElectricSupplySiteParser(NewsCollector collector) {
        super(collector);
    }

    void refreshNews() {
        Log.d(MainActivity.LOGTAG, "refreshElectric");
        List<News> newsList = new LinkedList<News>();
        // Подсасывает Document из данной ссылки.
        try {
            Document doc = Jsoup.connect(ElectricSupplySiteParser.URL).get();
            // Потрошим таблицу, выбираем tr, только находящиеся внутри tbody,
            // исключаем .bottom,.top
            Elements events = doc.select("tbody tr").not(".bottom,.top");

            for (Element event : events) {
                //print("\nDescription: %s",event.text());
                // TODO! Выпотрошить дальше отдельные элементы.

                String date = event.select(".date-s").first().text();
                String borders = event.select(".granici").first().text();
                String text = event.select(".date-s").first().text() +
                    " - " + event.select(".date-po").first().text() + "\n" +
                    event.select(".prichina").first().text() + "\n" +
                    borders;
					
				//Геокодинг (получаем координаты)
				//формируем URL
				String street = borders;
				if (street.indexOf(";") != -1){
					street = street.substring(0,street.indexOf(";"));
				}
				street = street.substring(0,street.lastIndexOf(" "));
				
				while (street.indexOf(",")>-1){
					street = street.substring(0,street.lastIndexOf(" "));
				}
				
                boolean relation = isRelatedToUser(street);
				Log.d(LOGTAG,"Electricity date: " + date);
                News news = new News(date, ElectricSupplySiteParser.SOURCE_CODE,
                    relation, text);
                newsList.add(news);
            }
            mCollector.addNews(newsList);
        } catch (IOException e) {
            System.out.println(
                "[ERROR!] IOException while parsing DonEnergo site.");
        }
    }
}
