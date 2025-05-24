package com.example.ecologemoscow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private TextView emailTextView;
    private Button loginButton;
    private Button logoutButton;
    private Button qrScanButton;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profil_l, container, false);
        
        emailTextView = view.findViewById(R.id.emailtext);
        loginButton = view.findViewById(R.id.butlogout);
        logoutButton = view.findViewById(R.id.qr_scan_button);
        qrScanButton = view.findViewById(R.id.qr_scan_button);

        updateUI();

        return view;
    }

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Пользователь авторизован
            emailTextView.setText(currentUser.getEmail());
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            qrScanButton.setVisibility(View.VISIBLE);
            
            logoutButton.setText("выйти");
            logoutButton.setOnClickListener(v -> {
                mAuth.signOut();
                updateUI();
                Toast.makeText(getContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            });

            qrScanButton.setOnClickListener(v -> {
                // Здесь будет логика сканирования QR-кода
                Toast.makeText(getContext(), "Сканирование QR-кода", Toast.LENGTH_SHORT).show();
            });
        } else {
            // Пользователь не авторизован
            emailTextView.setText("");
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            qrScanButton.setVisibility(View.GONE);
            
            loginButton.setText("войти");
            loginButton.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), Sugnup.class);
                startActivity(intent);
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }
} 