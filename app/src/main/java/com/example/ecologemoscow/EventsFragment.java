package com.example.ecologemoscow;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.ecologemoscow.adapters.EcoEventAdapter;
import com.example.ecologemoscow.models.EcoEvent;
import com.example.ecologemoscow.utils.EcoEventParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private EcoEventAdapter adapter;
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
        adapter = new EcoEventAdapter(getContext(), new ArrayList<>());
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
            List<EcoEvent> events = EcoEventParser.fetchEcoEvents();
            requireActivity().runOnUiThread(() -> adapter.setEvents(events));
        }).start();
    }

    private void loadUserEvents() {
        eventsRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                List<EcoEvent> events = new ArrayList<>();
                for (com.google.firebase.database.DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    EcoEvent event = eventSnapshot.getValue(EcoEvent.class);
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
