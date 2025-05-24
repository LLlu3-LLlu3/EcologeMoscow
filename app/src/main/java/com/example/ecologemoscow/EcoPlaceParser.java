package com.example.ecologemoscow;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EcoPlaceParser {
    private static final String TAG = "EcoPlaceParser";
    private static final int TIMEOUT = 10000; // 10 секунд
    
    public interface OnParsingCompleteListener {
        void onParsingComplete(List<EcoPlace> places);
        void onParsingError(String error);
    }

    public void parseEcoPlaces(OnParsingCompleteListener listener) {
        new ParseEcoPlacesTask(listener).execute();
    }

    private static class ParseEcoPlacesTask extends AsyncTask<Void, Void, List<EcoPlace>> {
        private final OnParsingCompleteListener listener;
        private String errorMessage;

        ParseEcoPlacesTask(OnParsingCompleteListener listener) {
            this.listener = listener;
        }

        @Override
        protected List<EcoPlace> doInBackground(Void... voids) {
            List<EcoPlace> places = new ArrayList<>();
            
            try {
                // Парсинг с mos.ru
                places.addAll(parseMosRu());
                
                // Парсинг с moscowparks.ru
                places.addAll(parseMoscowParks());
                
                // Если не удалось получить данные, используем демо-данные
                if (places.isEmpty()) {
                    places.addAll(getDemoData());
                }
                
            } catch (Exception e) {
                errorMessage = e.getMessage();
                Log.e(TAG, "Error parsing eco places", e);
                return getDemoData();
            }
            
            return places;
        }

        @Override
        protected void onPostExecute(List<EcoPlace> places) {
            if (errorMessage != null) {
                listener.onParsingError(errorMessage);
            } else {
                listener.onParsingComplete(places);
            }
        }
    }

    private static List<EcoPlace> parseMosRu() throws IOException {
        List<EcoPlace> places = new ArrayList<>();
        String url = "https://www.mos.ru/eco/parks/";
        
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .get();
            
            Elements parkElements = doc.select(".park-card");
            
            for (Element park : parkElements) {
                String name = park.select(".park-name").text();
                String description = park.select(".park-description").text();
                String imageUrl = park.select(".park-image img").attr("src");
                String address = park.select(".park-address").text();
                String workingHours = park.select(".working-hours").text();
                
                if (name != null && !name.isEmpty()) {
                    String[] coords = extractCoordinates(address);
                    places.add(new EcoPlace(
                        name,
                        description,
                        imageUrl,
                        address,
                        workingHours,
                        coords != null ? coords[0] : null,
                        coords != null ? coords[1] : null
                    ));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error parsing mos.ru: " + e.getMessage());
        }
        
        return places;
    }

    private static List<EcoPlace> parseMoscowParks() throws IOException {
        List<EcoPlace> places = new ArrayList<>();
        String url = "https://moscowparks.ru/parks/";
        
        try {
            Document doc = Jsoup.connect(url)
                    .timeout(TIMEOUT)
                    .get();
            
            Elements parkElements = doc.select(".park-item");
            
            for (Element park : parkElements) {
                String name = park.select(".park-title").text();
                String description = park.select(".park-description").text();
                String imageUrl = park.select(".park-image img").attr("src");
                String address = park.select(".park-address").text();
                String workingHours = park.select(".working-hours").text();
                
                if (name != null && !name.isEmpty()) {
                    String[] coords = extractCoordinates(address);
                    places.add(new EcoPlace(
                        name,
                        description,
                        imageUrl,
                        address,
                        workingHours,
                        coords != null ? coords[0] : null,
                        coords != null ? coords[1] : null
                    ));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error parsing moscowparks.ru: " + e.getMessage());
        }
        
        return places;
    }

    private static String[] extractCoordinates(String address) {
        // Здесь можно добавить логику для получения координат по адресу
        // Например, через Google Maps Geocoding API
        return null;
    }

    public static List<EcoPlace> getDemoData() {
        List<EcoPlace> places = new ArrayList<>();
        
        places.add(new EcoPlace(
            "Парк Горького",
            "Центральный парк культуры и отдыха имени Горького - один из самых известных парков Москвы. Здесь есть зоны для отдыха, спортивные площадки, музеи и кафе.",
            "https://example.com/park_gorky.jpg",
            "ул. Крымский Вал, 9",
            "Круглосуточно",
            "55.7287",
            "37.6038"
        ));

        places.add(new EcoPlace(
            "Воробьевы горы",
            "Воробьевы горы - это природный заказник, с которого открывается прекрасный вид на Москву. Здесь есть смотровая площадка и экологические тропы.",
            "https://example.com/sparrow_hills.jpg",
            "Воробьёвская наб., 1",
            "Круглосуточно",
            "55.7100",
            "37.5594"
        ));

        places.add(new EcoPlace(
            "Лосиный остров",
            "Национальный парк 'Лосиный остров' - это уникальный природный комплекс в черте города. Здесь обитают лоси, кабаны и другие животные.",
            "https://example.com/losiny_ostrov.jpg",
            "ул. Поперечный просек, 1Г",
            "Круглосуточно",
            "55.8833",
            "37.7833"
        ));

        places.add(new EcoPlace(
            "Битцевский лес",
            "Природно-исторический парк 'Битцевский лес' - это крупнейший лесной массив в черте Москвы. Здесь есть экологические тропы и места для отдыха.",
            "https://example.com/bitsevsky_forest.jpg",
            "Новоясеневский тупик, 1",
            "Круглосуточно",
            "55.6000",
            "37.5500"
        ));

        places.add(new EcoPlace(
            "Серебряный бор",
            "Природный заказник 'Серебряный бор' - это островной лесной массив с уникальной природой. Здесь есть пляжи и места для отдыха.",
            "https://example.com/serebryany_bor.jpg",
            "Таманская ул., 2А",
            "Круглосуточно",
            "55.7833",
            "37.4333"
        ));

        places.add(new EcoPlace(
            "Царицыно",
            "Музей-заповедник 'Царицыно' - это дворцово-парковый ансамбль с богатой историей и прекрасной природой. Здесь есть дворцы, парки и пруды.",
            "https://example.com/tsaritsyno.jpg",
            "ул. Дольская, 1",
            "6:00 - 24:00",
            "55.6167",
            "37.6833"
        ));

        places.add(new EcoPlace(
            "Сокольники",
            "Парк 'Сокольники' - это один из старейших парков Москвы. Здесь есть спортивные площадки, музеи и места для отдыха.",
            "https://example.com/sokolniki.jpg",
            "ул. Сокольнический Вал, 1",
            "Круглосуточно",
            "55.8000",
            "37.6833"
        ));

        places.add(new EcoPlace(
            "Долина реки Сетунь",
            "Природный заказник 'Долина реки Сетунь' - это уникальный природный комплекс с богатой флорой и фауной. Здесь есть экологические тропы.",
            "https://example.com/setun_river.jpg",
            "ул. Островитянова, 10",
            "Круглосуточно",
            "55.7000",
            "37.4000"
        ));

        return places;
    }
} 