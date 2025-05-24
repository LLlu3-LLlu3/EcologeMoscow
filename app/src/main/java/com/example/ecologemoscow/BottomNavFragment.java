package com.example.ecologemoscow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BottomNavFragment extends Fragment {
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public BottomNavFragment() {
        // mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bottom_nav, container, false);
        
        ImageButton btnEvents = view.findViewById(R.id.btn_events);
        ImageButton btnSettings = view.findViewById(R.id.btn_settings);
        ImageButton btnShops = view.findViewById(R.id.btn_shops);
        ImageButton btnMap = view.findViewById(R.id.mapbut);

        // Обработка нажатий
        btnEvents.setOnClickListener(v -> navigateToFragment(new EventsFragment()));
        btnShops.setOnClickListener(v -> navigateToFragment(new ShopsFragment()));
        btnMap.setOnClickListener(v -> navigateToFragment(new MapNavFragment()));
        btnSettings.setOnClickListener(v -> registerSettingsClick());

        return view;
    }

    private void registerSettingsClick() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Intent intent;
        if (currentUser != null) {
            intent = new Intent(getActivity(), profil.class);
        } else {
            intent = new Intent(getActivity(), Sugnup.class);
        }
        startActivity(intent);
    }

    private void navigateToFragment(Fragment fragment) {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
