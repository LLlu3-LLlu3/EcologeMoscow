package com.example.ecologemoscow;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Sugnup extends AppCompatActivity {
    private EditText emailTextView, passwordTextView, firstNameTextView, lastNameTextView, middleNameTextView;
    private Button Btn;
    private ProgressBar progressbar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugnup);

        // Инициализация Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Инициализация views
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.passwd);
        firstNameTextView = findViewById(R.id.first_name);
        lastNameTextView = findViewById(R.id.last_name);
        middleNameTextView = findViewById(R.id.middle_name);
        Btn = findViewById(R.id.btnregister);
        progressbar = findViewById(R.id.progressbar);

        // Обработчик нажатия на кнопку регистрации
        Btn.setOnClickListener(v -> registerNewUser());
    }

    private void registerNewUser() {
        // Показываем прогресс
        progressbar.setVisibility(View.VISIBLE);

        // Получаем значения из полей ввода
        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String firstName = firstNameTextView.getText().toString();
        String lastName = lastNameTextView.getText().toString();
        String middleName = middleNameTextView.getText().toString();

        // Валидация полей
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Пожалуйста, введите email", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Пожалуйста, введите пароль", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(getApplicationContext(), "Пожалуйста, введите имя", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(getApplicationContext(), "Пожалуйста, введите фамилию", Toast.LENGTH_LONG).show();
            return;
        }

        // Создаем нового пользователя
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Сохраняем дополнительные данные пользователя
                        String userId = mAuth.getCurrentUser().getUid();
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("firstName", firstName);
                        userData.put("lastName", lastName);
                        userData.put("middleName", middleName);
                        userData.put("email", email);

                        mDatabase.child("users").child(userId).setValue(userData)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Регистрация успешна!", Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Ошибка сохранения данных: " + task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getApplicationContext(), "Ошибка регистрации: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    progressbar.setVisibility(View.GONE);
                });
    }
}
