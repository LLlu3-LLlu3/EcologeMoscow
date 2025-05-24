package com.example.ecologemoscow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class EventDetailsFragment extends Fragment {
    private static final String ARG_EVENT = "event";
    private Event event;

    public static EventDetailsFragment newInstance(Event event) {
        EventDetailsFragment fragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putString("event_id", event.getId());
        args.putString("event_title", event.getTitle());
        args.putString("event_description", event.getDescription());
        args.putString("event_address", event.getAddress());
        args.putString("event_time", event.getTime());
        args.putString("event_image", event.getImageUri());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            event = new Event(
                getArguments().getString("event_id"),
                getArguments().getString("event_title"),
                getArguments().getString("event_description"),
                getArguments().getString("event_address"),
                getArguments().getString("event_time"),
                getArguments().getString("event_image")
            );
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.event_image);
        TextView titleTextView = view.findViewById(R.id.event_title);
        TextView timeTextView = view.findViewById(R.id.event_time);
        TextView addressTextView = view.findViewById(R.id.event_address);
        TextView descriptionTextView = view.findViewById(R.id.event_description);

        if (event != null) {
            titleTextView.setText(event.getTitle());
            timeTextView.setText(event.getTime());
            addressTextView.setText(event.getAddress());
            descriptionTextView.setText(event.getDescription());

            imageView.setImageResource(R.drawable.default_image_background);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
} 