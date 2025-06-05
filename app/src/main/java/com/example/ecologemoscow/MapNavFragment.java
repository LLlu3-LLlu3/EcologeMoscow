package com.example.ecologemoscow;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.example.ecologemoscow.charts.ChartsContainerFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import java.util.ArrayList;
import java.util.List;

public class MapNavFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = "MapNavFragment";
    private static final LatLng MOSCOW = new LatLng(55.7558, 37.6173);
    private static final float DEFAULT_ZOOM = 10f;
    private static final int MAX_RETRIES = 3;

    private GoogleMap mMap;
    private ProgressBar progressBar;
    private TextView errorView;
    private Button retryButton;
    private ImageButton toggleMapButton;
    private boolean isFragmentActive = false;
    private boolean isMapReady = false;
    private boolean isLocationEnabled = false;
    private int retryCount = 0;
    private Polygon southButovoPolygon;
    private boolean isShopsMapVisible = false;
    private SupportMapFragment mapFragment;
    private CameraPosition lastCameraPosition;

    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
        new ActivityResultContracts.RequestMultiplePermissions(),
        permissions -> {
            boolean allGranted = true;
            for (Boolean isGranted : permissions.values()) {
                if (!isGranted) {
                    allGranted = false;
                    break;
                }
            }
            if (allGranted) {
                enableLocationFeatures();
            }
        }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_nav, container, false);
        
        progressBar = view.findViewById(R.id.progress_bar);
        errorView = view.findViewById(R.id.error_view);
        retryButton = view.findViewById(R.id.retry_button);
        toggleMapButton = view.findViewById(R.id.toggle_map_button);
        
        toggleMapButton.setOnClickListener(v -> toggleMapView());
        retryButton.setOnClickListener(v -> {
            if (retryCount < MAX_RETRIES) {
                retryCount++;
                initializeMap();
            }
        });
        
        // Инициализируем карту с полигонами
        mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit();
        mapFragment.getMapAsync(this);
        
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isFragmentActive = true;
        
        // Инициализация карты
        mapFragment = SupportMapFragment.newInstance();
        getChildFragmentManager().beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commit();
        mapFragment.getMapAsync(this);
    }

    private void initializeMap() {
        if (!isFragmentActive) return;
        
        showLoading();
        
        // Проверяем подключение к интернету
        if (!isNetworkAvailable()) {
            showError("Нет подключения к интернету");
            return;
        }

        try {
            // Инициализация карты
            mapFragment = SupportMapFragment.newInstance();
            getChildFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            Log.e(TAG, "Error initializing map: " + e.getMessage());
            showError("Ошибка инициализации карты: " + e.getMessage());
        }
    }

    private void showLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (retryButton != null) {
            retryButton.setVisibility(View.GONE);
        }
    }

    private void hideLoading() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showError(String message) {
        hideLoading();
        if (errorView != null) {
            errorView.setText(message);
            errorView.setVisibility(View.VISIBLE);
        }
        if (retryButton != null && retryCount < MAX_RETRIES) {
            retryButton.setVisibility(View.VISIBLE);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (!isFragmentActive) return;
        
        try {
            Log.d(TAG, "onMapReady: Инициализация карты");
            mMap = googleMap;
            isMapReady = true;
            
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            
            // Если есть сохраненная позиция, используем её
            if (lastCameraPosition != null) {
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(lastCameraPosition));
            } else {
                // Иначе центрируем на Москве
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MOSCOW, DEFAULT_ZOOM));
            }
            
            // Проверяем разрешения на местоположение
            checkAndRequestLocationPermission();
            
            // Добавляем полигоны
            Log.d(TAG, "onMapReady: Добавление полигонов");
            addSouthButovoPolygon();
            addKommunarka();
            
            hideLoading();
            Log.d(TAG, "onMapReady: Карта успешно инициализирована");
        } catch (Exception e) {
            Log.e(TAG, "Error in onMapReady: " + e.getMessage());
            showError("Ошибка загрузки карты: " + e.getMessage());
        }
    }

    private void checkAndRequestLocationPermission() {
        if (!isFragmentActive) return;
        
        if (hasLocationPermission()) {
            isLocationEnabled = true;
            enableLocationFeatures();
        } else {
            requestLocationPermission();
        }
    }

    private boolean hasLocationPermission() {
        if (!isFragmentActive) return false;
        
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestLocationPermission() {
        if (!isFragmentActive) return;
        
        permissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private void enableLocationFeatures() {
        if (mMap == null || !isFragmentActive) return;
        
        try {
            if (hasLocationPermission()) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                isLocationEnabled = true;
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Error enabling location features: " + e.getMessage());
            showError("Ошибка включения геолокации: " + e.getMessage());
        }
    }

    private void addSouthButovoPolygon() {
        try {
            Log.d(TAG, "addSouthButovoPolygon: Начало создания полигона");
            List<LatLng> polygonPoints = new ArrayList<>();
            // Координаты полигона Южное Бутово
            polygonPoints.add(new LatLng(55.5634, 37.4707)); // Юго-запад
            polygonPoints.add(new LatLng(55.5734, 37.4707)); // Северо-запад
            polygonPoints.add(new LatLng(55.5734, 37.4907)); // Северо-восток
            polygonPoints.add(new LatLng(55.5634, 37.4907)); // Юго-восток
            polygonPoints.add(new LatLng(55.5634, 37.4707)); // Замыкаем полигон

            PolygonOptions polygonOptions = new PolygonOptions()
                    .addAll(polygonPoints)
                    .strokeColor(Color.GREEN)
                    .strokeWidth(5)
                    .fillColor(Color.argb(50, 0, 255, 0)); // Полупрозрачный зеленый

            southButovoPolygon = mMap.addPolygon(polygonOptions);
            southButovoPolygon.setClickable(true);

            // Добавляем обработчик нажатия на полигон
            mMap.setOnPolygonClickListener(polygon -> {
                if (polygon.equals(southButovoPolygon)) {
                    Log.d(TAG, "Полигон Южное Бутово нажат");
                    try {
                        // Создаем ChartsContainerFragment для отображения всех графиков
                        ChartsContainerFragment chartsFragment = new ChartsContainerFragment();
                        
                        // Передаем информацию о районе
                        Bundle args = new Bundle();
                        args.putString("district_name", "Южное Бутово");
                        args.putString("default_chart", "dust"); // По умолчанию показываем график пыли
                        chartsFragment.setArguments(args);
                        
                        // Отображаем фрагмент с графиками
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, chartsFragment)
                                .addToBackStack(null)
                                .commit();
                            Log.d(TAG, "ChartsContainerFragment успешно отображен");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка при обработке клика на полигон: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Ошибка при открытии графиков: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            Log.d(TAG, "addSouthButovoPolygon: Полигон успешно создан");
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при создании полигона Южного Бутово: " + e.getMessage(), e);
        }
    }

    private void addKommunarka() {
        try {
            // Создаем полигон для Коммунарки
            List<LatLng> polygonPoints = new ArrayList<>();
            polygonPoints.add(new LatLng(55.5317, 37.5217)); // Юго-запад
            polygonPoints.add(new LatLng(55.5317, 37.5517)); // Северо-запад
            polygonPoints.add(new LatLng(55.5617, 37.5517)); // Северо-восток
            polygonPoints.add(new LatLng(55.5617, 37.5217)); // Юго-восток
            polygonPoints.add(new LatLng(55.5317, 37.5217)); // Замыкаем полигон

            PolygonOptions polygonOptions = new PolygonOptions()
                    .addAll(polygonPoints)
                    .strokeColor(Color.GREEN)
                    .strokeWidth(5)
                    .fillColor(Color.argb(50, 0, 255, 0)); // Полупрозрачный зеленый

            Polygon kommunarkaPolygon = mMap.addPolygon(polygonOptions);
            kommunarkaPolygon.setClickable(true);

            // Добавляем обработчик нажатия на полигон
            mMap.setOnPolygonClickListener(polygon -> {
                if (polygon.equals(kommunarkaPolygon)) {
                    Log.d(TAG, "Полигон Коммунарка нажат");
                    try {
                        // Создаем ChartsContainerFragment для отображения всех графиков
                        ChartsContainerFragment chartsFragment = new ChartsContainerFragment();
                        
                        // Передаем информацию о районе
                        Bundle args = new Bundle();
                        args.putString("district_name", "Южное Бутово");
                        args.putString("default_chart", "dust"); // По умолчанию показываем график пыли
                        chartsFragment.setArguments(args);
                        
                        // Отображаем фрагмент с графиками
                        if (getActivity() != null) {
                            getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragment_container, chartsFragment)
                                .addToBackStack(null)
                                .commit();
                            Log.d(TAG, "ChartsContainerFragment успешно отображен");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Ошибка при обработке клика на полигон: " + e.getMessage(), e);
                        Toast.makeText(getContext(), "Ошибка при открытии графиков: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error adding Kommunarka: " + e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        isFragmentActive = true;
        if (mMap != null && isMapReady) {
            setupMapSettings();
        } else {
            initializeMap();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isFragmentActive = false;
        if (mMap != null) {
            try {
                mMap.setMyLocationEnabled(false);
            } catch (SecurityException e) {
                Log.e(TAG, "Error disabling location: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFragmentActive = false;
        mMap = null;
        isMapReady = false;
    }

    public void showPlace(double latitude, double longitude, String title) {
        if (mMap != null) {
            LatLng location = new LatLng(latitude, longitude);
            mMap.clear(); // Очищаем предыдущие маркеры
            mMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(title));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        }
    }

    private void setupMapSettings() {
        if (mMap != null) {
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(true);
        }
    }

    private void toggleMapView() {
        if (isShopsMapVisible) {
            // Сохраняем текущую позицию карты
            if (mMap != null) {
                lastCameraPosition = mMap.getCameraPosition();
            }
            
            // Возвращаемся к карте района
            getChildFragmentManager().beginTransaction()
                .replace(R.id.map_container, mapFragment)
                .commit();
            mapFragment.getMapAsync(this); // Перезагружаем карту с полигонами
            isShopsMapVisible = false;
        } else {
            // Сохраняем текущую позицию карты
            if (mMap != null) {
                lastCameraPosition = mMap.getCameraPosition();
            }
            
            // Показываем карту магазинов
            ShopsMapFragment shopsMapFragment = new ShopsMapFragment();
            if (lastCameraPosition != null) {
                shopsMapFragment.setLastCameraPosition(lastCameraPosition);
            }
            getChildFragmentManager().beginTransaction()
                .replace(R.id.map_container, shopsMapFragment)
                .commit();
            isShopsMapVisible = true;
        }
    }

} 