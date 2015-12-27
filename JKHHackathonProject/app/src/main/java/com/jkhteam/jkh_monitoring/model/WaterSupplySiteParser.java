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
 * Parses news from the site of Vodokonal.
 * Url: http://vodokanalrnd.ru/press-tsentr/operative-monitor/?year=2015
 */
public class WaterSupplySiteParser extends AbstractSiteParser {

    public static final String SOURCE_CODE = "water_supply";
    private static final String URL = "http://vodokanalrnd.ru/press-tsentr/operative-monitor/?year=2015";

    public WaterSupplySiteParser(NewsCollector collector) {
        super(collector);
    }

    void refreshNews() {
        Log.d(MainActivity.LOGTAG, "refreshWater");
        List<News> newsList = new LinkedList<News>();
        // Подсасывает Document из данной ссылки.
        try {
            Document doc = Jsoup.connect(WaterSupplySiteParser.URL).get();
            // Потрошит страницу, ищет ссылки на отдельные события по CSS-классу
            // li-link.
            Elements links = doc.select(".li-link");
            for (Element link : links) {
                Document tDoc = Jsoup.connect(link.attr("abs:href")).get();
                // Выбирает коллекцию дат по классу date.
                Elements dateE = tDoc.select(".date");
                // Выбирает коллекцию описаний по классу li-dsc.
                Elements descrE = tDoc.select(".li-dsc div");
                // Т.к. на странице событие только одно, выбираем его из
                // коллекции.
                Element date = dateE.first();
                // Подбираем и его описание.
                Element descr = descrE.first();
				if (descr == null){
				continue;
				}
                // Далее приводим текст в божеский вид.
                // Метод text() выдает текст полученного элемента без тегов html
                String dateS = date.text();
                // Дата вылазит в виде 22ноября 2015, поэтому вставляем пробел.
                // npcipav: Руки бы поотрывать за это дерьмо с датой... /О
                dateS = dateS.substring(0, 1) + " " +
                    dateS.substring(2, dateS.length());
                // У текста описания удаляем лишние подписи об извинениях
                // Водоканала.
                // Метод html() выдает текст элемента с тегами html.
                String descrS = descr.html();
				
				if (descrS.indexOf("<br>") != -1){
					descrS = descrS.substring(0,descrS.indexOf("<br>"));
				}

                descrS = descrS.replaceAll("&nbsp;","");
				
				//Геокодинг (получаем координаты)
				//формируем URL
				String street = descr.text();
				street = street.substring(street.indexOf("."),street.length()-1);
				street = street.substring(1, street.indexOf(","));

				boolean relation = isRelatedToUser(street);
                Log.d(LOGTAG,"Water date: " + dateS);
                //Добавляем новость в список.
                News news = new News(dateS, WaterSupplySiteParser.SOURCE_CODE,
                    relation, descrS);
                newsList.add(news);
            }
            // Отправляем новости в NewsCollector.
            mCollector.addNews(newsList);
        } catch (IOException e) {
            System.out.println(
                "[ERROR!] IOException while parsing Vodokanal site.");
        }
    }
}
