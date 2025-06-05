package com.example.ecologemoscow;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";
    private LottieAnimationView animationView;
    private boolean isLoading = true;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final int SPLASH_DELAY = 2000; // 2 секунды

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            // Инициализация анимации
            animationView = findViewById(R.id.animationView);
            if (animationView != null) {
                animationView.setAnimation(R.raw.animation);
                animationView.playAnimation();
            }

            // Инициализация Firebase
            mDatabase = FirebaseDatabase.getInstance().getReference();
            
            // Начало загрузки данных
            loadData();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage());
            handler.postDelayed(this::goToMainActivity, SPLASH_DELAY);
        }
    }
    
    private void loadData() {
        try {
            // Проверка авторизации
            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
                mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(TAG, "User data loaded");
                        finishLoading();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e(TAG, "Error loading user data: " + databaseError.getMessage());
                        finishLoading();
                    }
                });
            } else {
                // Пользователь не авторизован
                handler.postDelayed(this::finishLoading, SPLASH_DELAY);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in loadData: " + e.getMessage());
            handler.postDelayed(this::finishLoading, SPLASH_DELAY);
        }
    }
    
    private void finishLoading() {
        if (isLoading) {
            isLoading = false;
            goToMainActivity();
        }
    }

    private void goToMainActivity() {
        try {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error starting MainActivity: " + e.getMessage());
            Toast.makeText(this, "Ошибка запуска приложения", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        if (animationView != null) {
            animationView.cancelAnimation();
        }
    }
}
