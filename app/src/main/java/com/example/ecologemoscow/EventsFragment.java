package com.example.ecologemoscow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventsAdapter adapter;
    private LinearLayout btnCityEvents;
    private LinearLayout btnUserEvents;
    private FloatingActionButton fab;
    private boolean isCityEvents = true;
    private DatabaseReference eventsRef;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        eventsRef = FirebaseDatabase.getInstance().getReference("events");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        
        // Инициализация кнопок навигации
        btnCityEvents = view.findViewById(R.id.btn_city_events);
        btnUserEvents = view.findViewById(R.id.btn_user_events);
        
        // Настройка кнопок
        btnCityEvents.setOnClickListener(v -> switchToCityEvents());
        btnUserEvents.setOnClickListener(v -> switchToUserEvents());
        
        // Инициализация RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EventsAdapter(event -> {
            EventDetailsFragment detailsFragment = EventDetailsFragment.newInstance(event);
            getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();
        });
        recyclerView.setAdapter(adapter);

        // FAB для добавления событий
        fab = view.findViewById(R.id.fab_add_event);
        fab.setVisibility(View.GONE); // По умолчанию скрыт
        fab.setOnClickListener(v -> {
            if (mAuth.getCurrentUser() == null) {
                Toast.makeText(getContext(), "Для создания мероприятия необходимо войти в аккаунт", Toast.LENGTH_LONG).show();
                return;
            }
            // Запуск CreateEventActivity
            Intent intent = new Intent(getActivity(), CreateEventActivity.class);
            startActivity(intent);
        });

        // Загружаем городские события по умолчанию
        loadCityEvents();

        return view;
    }

    private void switchToCityEvents() {
        isCityEvents = true;
        btnCityEvents.setSelected(true);
        btnUserEvents.setSelected(false);
        fab.setVisibility(View.GONE); // Скрываем FAB для городских событий
        loadCityEvents();
    }

    private void switchToUserEvents() {
        isCityEvents = false;
        btnCityEvents.setSelected(false);
        btnUserEvents.setSelected(true);
        fab.setVisibility(View.VISIBLE); // Показываем FAB для народных событий
        loadUserEvents();
    }

    private void loadCityEvents() {
        new Thread(() -> {
            try {
                Log.d("EventsFragment", "Начало загрузки городских мероприятий");
                List<Event> events = new ArrayList<>();
                String url = "https://mosvolonter.ru/events#line";
                
                Log.d("EventsFragment", "Подключение к URL: " + url);
                Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .timeout(10000)
                    .get();
                
                Log.d("EventsFragment", "HTML получен, начинаем парсинг");
                
                // Ищем карточки мероприятий
                Elements eventElements = doc.select(".event-card, .event-item, .event");
                Log.d("EventsFragment", "Найдено элементов: " + eventElements.size());

                for (Element element : eventElements) {
                    try {
                        Log.d("EventsFragment", "Обработка элемента: " + element.html());
                        
                        // Парсим данные из карточки мероприятия
                        String title = element.select(".event-card__title, .event-item__title, .event__title").text();
                        String date = element.select(".event-card__date, .event-item__date, .event__date").text();
                        String location = element.select(".event-card__location, .event-item__location, .event__location").text();
                        String description = element.select(".event-card__description, .event-item__description, .event__description").text();
                        String imageUrl = element.select(".event-card__image img, .event-item__image img, .event__image img").attr("src");
                        
                        // Если URL изображения относительный, добавляем домен
                        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
                            imageUrl = "https://mosvolonter.ru" + imageUrl;
                        }
                        
                        Log.d("EventsFragment", String.format(
                            "Распарсено: title='%s', date='%s', location='%s', description='%s', imageUrl='%s'",
                            title, date, location, description, imageUrl
                        ));

                        if (!title.isEmpty()) {
                            Event event = new Event();
                            event.setTitle(title);
                            event.setDate(date);
                            event.setLocation(location);
                            event.setDescription(description);
                            event.setImageUrl(imageUrl);
                            event.setType("city");
                            event.setCityEvent(true);
                            
                            events.add(event);
                            Log.d("EventsFragment", "Событие добавлено в список");
                        }
                    } catch (Exception e) {
                        Log.e("EventsFragment", "Ошибка при парсинге элемента: " + e.getMessage());
                    }
                }

                // Обновляем UI в главном потоке
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        adapter.setEvents(events);
                        if (events.isEmpty()) {
                            Toast.makeText(getContext(), "Не удалось загрузить мероприятия", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("EventsFragment", "Загружено мероприятий: " + events.size());
                        }
                    });
                }
            } catch (Exception e) {
                Log.e("EventsFragment", "Ошибка загрузки мероприятий: " + e.getMessage(), e);
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Ошибка загрузки мероприятий: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }
            }
        }).start();
    }

    private void loadUserEvents() {
        eventsRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                List<Event> events = new ArrayList<>();
                for (com.google.firebase.database.DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    Event event = eventSnapshot.getValue(Event.class);
                    if (event != null) {
                        events.add(event);
                    }
                }
                adapter.setEvents(events);
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка загрузки мероприятий", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
