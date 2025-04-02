package com.example.ecologemoscow;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MapNavFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private Double s1;
    private String s2;
    private DatabaseReference mDatabase;
    private Polygon butovoPolygon, butforestPolygon;

    // Координаты
    private final List<LatLng> butovoBounds = Arrays.asList(
        new LatLng(55.5725, 37.5308),  // Северо-западная точка
        new LatLng(55.5725, 37.5608),  // Северо-восточная точка
        new LatLng(55.5425, 37.5608),  // Юго-восточная точка
        new LatLng(55.5425, 37.5308)   // Юго-западная точка
    );
    private final List<LatLng> butovoforest=Arrays.asList(
            new LatLng(55.640466244231696, 37.619747316614465),  // Северо-западная точка
            new LatLng(55.67004634003862, 37.520732775716574),  // Северо-восточная точка
            new LatLng(55.58417205887529, 37.44140450921575),  // Юго-восточная точка
            new LatLng(55.555558647717156, 37.60458290129722)   // Юго-западная точка
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map_nav, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Firebase setup
        mDatabase = FirebaseDatabase.getInstance().getReference("test");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double valuefloat = dataSnapshot.child("float").getValue(Double.class);
                    Integer valueInt = dataSnapshot.child("int").getValue(Integer.class);
                    
                    if (valuefloat != null) {
                        s1 = valuefloat;
                        updatePolygonColor();
                    } else {
                        s1 = 0.0;
                    }

                    if (valueInt != null) {
                        s2 = String.valueOf(valueInt);
                    } else {
                        s2 = "нет данных";
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (getActivity() != null) {
                    AlertDialog.Builder errorlog = new AlertDialog.Builder(getActivity());
                    errorlog.setTitle("ERROR CODE")
                            .setMessage("" + error)
                            .setCancelable(false)
                            .setPositiveButton("Понял", (dialog, which) -> {
                                // Handle error
                            });
                    AlertDialog errorlogt = errorlog.create();
                    errorlogt.show();
                }
            }
        });
    }

    private void updatePolygonColor() {
        if (googleMap != null && butovoPolygon != null && s1 >= 0) {
            butovoPolygon.setFillColor(Color.GREEN);
            butovoPolygon.setStrokeColor(Color.GREEN);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        // полигон
        butovoPolygon = googleMap.addPolygon(new PolygonOptions()
                .addAll(butovoBounds)
                .strokeColor(Color.WHITE)
                .fillColor(Color.WHITE)
                .clickable(true));
        butovoPolygon.setTag("pol2");

        butforestPolygon= googleMap.addPolygon(new PolygonOptions()
                .addAll(butovoforest)
                .strokeColor(Color.WHITE)
                .fillColor(Color.WHITE)
                .clickable(true));
        butforestPolygon.setTag("butovoforest");

        googleMap.setOnPolygonClickListener(polygon -> {
            if (getActivity() != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if ("pol2".equals(polygon.getTag())) {
                    if (s1 >= 0) {
                        polygon.setFillColor(Color.GREEN);
                        polygon.setStrokeColor(Color.GREEN);
                    }
                    builder.setTitle("Южное Бутово")
                            .setMessage(s1 + " " + s2)
                            .setCancelable(true);
                    AlertDialog buildert = builder.create();
                    buildert.show();
                }
                if ("butovoforest".equals(polygon.getTag())) {
                    builder.setTitle("чё за Район")
                            .setMessage("Здесь инфы нет :(")
                            .setCancelable(true);
                    AlertDialog buildert = builder.create();
                    buildert.show();
                }
            }
        });

        // Центрируем карту на Южном Бутово
        LatLng butovoCenter = new LatLng(55.5575, 37.5458);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(butovoCenter, 13));

        // Добавляем маркер в центр Южного Бутово
        googleMap.addMarker(new MarkerOptions()
                .position(butovoCenter)
                .title("Южное Бутово"));
    }
} 