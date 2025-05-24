package com.example.ecologemoscow.firebase;

import androidx.annotation.NonNull;
import com.example.ecologemoscow.models.ChartDataModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;

public class FirebaseChartManager {
    private static final String TEST_PATH = "test";
    private final DatabaseReference databaseReference;
    private ChartDataListener listener;

    public interface ChartDataListener {
        void onDataReceived(ChartDataModel data);
        void onError(String error);
    }

    public FirebaseChartManager() {
        databaseReference = FirebaseDatabase.getInstance().getReference(TEST_PATH);
    }

    public void setListener(ChartDataListener listener) {
        this.listener = listener;
    }

    public void startListening() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (listener != null) {
                    ChartDataModel data = snapshot.getValue(ChartDataModel.class);
                    if (data != null) {
                        listener.onDataReceived(data);
                    } else {
                        listener.onError("Данные не найдены");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (listener != null) {
                    listener.onError(error.getMessage());
                }
            }
        });
    }

    public void stopListening() {
        databaseReference.removeEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Не используется
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Не используется
            }
        });
    }

    public void saveData(ChartDataModel data) {
        databaseReference.setValue(data)
            .addOnSuccessListener(aVoid -> {
                if (listener != null) {
                    listener.onDataReceived(data);
                }
            })
            .addOnFailureListener(e -> {
                if (listener != null) {
                    listener.onError(e.getMessage());
                }
            });
    }
} 