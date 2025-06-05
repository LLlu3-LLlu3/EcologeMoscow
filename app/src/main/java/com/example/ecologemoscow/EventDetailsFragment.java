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
        args.putString("event_location", event.getLocation());
        args.putString("event_date", event.getDate());
        args.putString("event_image", event.getImageUri());
        args.putString("creator_id", event.getCreatorId());
        args.putString("creator_first_name", event.getCreatorFirstName());
        args.putString("creator_last_name", event.getCreatorLastName());
        args.putString("creator_middle_name", event.getCreatorMiddleName());
        args.putBoolean("is_city_event", event.isCityEvent());
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
                getArguments().getString("event_location"),
                getArguments().getString("event_date"),
                getArguments().getString("event_image"),
                getArguments().getString("creator_id"),
                getArguments().getString("creator_first_name"),
                getArguments().getString("creator_last_name"),
                getArguments().getString("creator_middle_name"),
                getArguments().getBoolean("is_city_event"),
                getArguments().getBoolean("is_city_event") ? "city" : "user"
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
        TextView dateTextView = view.findViewById(R.id.event_date);
        TextView locationTextView = view.findViewById(R.id.event_location);
        TextView descriptionTextView = view.findViewById(R.id.event_description);
        TextView creatorTextView = view.findViewById(R.id.event_creator);

        if (event != null) {
            titleTextView.setText(event.getTitle());
            dateTextView.setText(event.getDate());
            locationTextView.setText(event.getLocation());
            descriptionTextView.setText(event.getDescription());
            creatorTextView.setText(event.getCreatorFullName());

            if (event.getImageUri() != null && !event.getImageUri().isEmpty()) {
                // Здесь можно добавить загрузку изображения с помощью Glide или Picasso
                imageView.setImageResource(R.drawable.default_image_background);
            } else {
                imageView.setImageResource(R.drawable.default_image_background);
            }
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
} 