package com.example.ecologemoscow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

public class CreateEventActivity extends AppCompatActivity {
    private TextInputEditText titleInput, descriptionInput, addressInput, timeInput;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Инициализация Firebase
        eventsRef = FirebaseDatabase.getInstance().getReference("events");

        // Инициализация views
        titleInput = findViewById(R.id.event_title_input);
        descriptionInput = findViewById(R.id.event_description_input);
        addressInput = findViewById(R.id.event_address_input);
        timeInput = findViewById(R.id.event_time_input);
        Button createEventButton = findViewById(R.id.btn_create_event);


        createEventButton.setOnClickListener(v -> createEvent());
    }

    private void createEvent() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String address = addressInput.getText().toString().trim();
        String time = timeInput.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty() || address.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        String eventId = UUID.randomUUID().toString();
        String creatorId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        Event event = new Event(
            eventId,
            title,
            description,
            address,
            time,
            null, // imageUri
            creatorId,
            "", // creatorFirstName
            "", // creatorLastName
            "", // creatorMiddleName
            false, // isCityEvent
            "user" // type - пользовательское событие
        );

        eventsRef.child(eventId).setValue(event)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CreateEventActivity.this, "Мероприятие успешно создано", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CreateEventActivity.this, "Ошибка создания мероприятия: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
} 