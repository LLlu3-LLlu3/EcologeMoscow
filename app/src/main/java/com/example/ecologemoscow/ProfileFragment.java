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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private TextView emailTextView;
    private TextView nameTextView;
    private Button loginButton;
    private Button logoutButton;
    private Button qrScanButton;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference userRef;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userRef = FirebaseDatabase.getInstance().getReference("users");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profil_l, container, false);
        
        emailTextView = view.findViewById(R.id.emailtext);
        nameTextView = view.findViewById(R.id.name_text);
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
            
            // Загружаем данные пользователя из Firebase
            userRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String firstName = snapshot.child("firstName").getValue(String.class);
                        String lastName = snapshot.child("lastName").getValue(String.class);
                        String middleName = snapshot.child("middleName").getValue(String.class);
                        
                        if (firstName != null && lastName != null) {
                            String fullName = String.format("%s %s %s", 
                                lastName, firstName, middleName != null ? middleName : "");
                            nameTextView.setText(fullName);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
                }
            });
            
            logoutButton.setText("выйти");
            logoutButton.setOnClickListener(v -> {
                mAuth.signOut();
                updateUI();
                Toast.makeText(getContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            });


        } else {
            // Пользователь не авторизован
            emailTextView.setText("");
            nameTextView.setText("");
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