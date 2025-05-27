package com.example.ecologemoscow.utils;

import android.os.Looper;
import android.widget.Toast;
import com.example.ecologemoscow.models.EcoEvent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.json.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class EcoEventParser {
    public static List<EcoEvent> fetchEcoEvents() {
        List<EcoEvent> events = new ArrayList<>();
        try {
            String url = "https://mosvolonter.ru/events?search_type=events&e_d[]=2";
            Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                .timeout(10000)
                .get();

            String html = doc.html();
            int start = html.indexOf("let events = ");
            if (start == -1) return events;
            int jsonStart = start + "let events = ".length();
            int jsonEnd = html.indexOf("};", jsonStart) + 1;
            if (jsonEnd <= jsonStart) return events;
            String jsonString = html.substring(jsonStart, jsonEnd);

            JSONObject eventsObj = new JSONObject(jsonString);
            JSONObject eventsList = eventsObj.getJSONObject("eventsList");

            Iterator<String> monthKeys = eventsList.keys();
            while (monthKeys.hasNext()) {
                String monthKey = monthKeys.next();
                JSONObject monthObj = eventsList.getJSONObject(monthKey);
                
                Iterator<String> dayKeys = monthObj.keys();
                while (dayKeys.hasNext()) {
                    String dayKey = dayKeys.next();
                    JSONArray dayArr = monthObj.getJSONArray(dayKey);
                    for (int i = 0; i < dayArr.length(); i++) {
                        JSONObject event = dayArr.getJSONObject(i);
                        String title = event.optString("title");
                        String date = event.optString("dateStart");
                        String link = "https://mosvolonter.ru" + event.optString("url");
                        String description = event.optString("content").replaceAll("<.*?>", "");
                        events.add(new EcoEvent(title, date, link, description));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Временно выводим ошибку в лог и через Toast (если Looper доступен)
            if (Looper.myLooper() != null) {
                Toast.makeText(null, "Ошибка загрузки мероприятий: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return events;
    }
} 