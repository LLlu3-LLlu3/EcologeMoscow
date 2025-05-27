package com.example.ecologemoscow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecologemoscow.adapters.EcoEventAdapter;
import com.example.ecologemoscow.models.EcoEvent;
import com.example.ecologemoscow.utils.EcoEventParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private EcoEventAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_events);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new EcoEventAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Загрузка событий в отдельном потоке
        new Thread(() -> {
            List<EcoEvent> events = EcoEventParser.fetchEcoEvents();
            requireActivity().runOnUiThread(() -> adapter.setEvents(events));
        }).start();

        // FAB для добавления своих событий (если нужно)
        FloatingActionButton fab = view.findViewById(R.id.fab_add_event);
        fab.setOnClickListener(v -> {
            // Здесь можно добавить переход к созданию своего события
        });

        return view;
    }
}
