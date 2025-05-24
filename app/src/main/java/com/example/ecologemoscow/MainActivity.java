package com.example.ecologemoscow;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecologemoscow.charts.ButovoChartFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_ACTIVE_FRAGMENT = "active_fragment";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 123;
    
    private MusicService musicService;
    private boolean bound = false;
    private Fragment activeFragment;
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            bound = true;
            if (musicService != null) {
                try {
                    musicService.startMusic();
                } catch (Exception e) {
                    Log.e(TAG, "Error starting music: " + e.getMessage());
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
            musicService = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Нет подключения к интернету", Toast.LENGTH_LONG).show();
        }

        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        if (bottomNavigationView == null) {
            Log.e(TAG, "BottomNavigationView is null");
            return;
        }

        // Проверка разрешений для уведомлений на Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }

        // Восстанавливаем состояние при повороте экрана
        if (savedInstanceState != null) {
            String fragmentTag = savedInstanceState.getString(KEY_ACTIVE_FRAGMENT);
            if (fragmentTag != null) {
                activeFragment = fragmentManager.findFragmentByTag(fragmentTag);
                if (activeFragment != null) {
                    updateBottomNavigation();
                } else {
                    loadInitialFragment();
                }
            } else {
                loadInitialFragment();
            }
        } else {
            loadInitialFragment();
        }

        setupBottomNavigation();

        // Start music service
        try {
            Intent intent = new Intent(this, MusicService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
        } catch (Exception e) {
            Log.e(TAG, "Error binding music service: " + e.getMessage());
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void loadInitialFragment() {
        try {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            activeFragment = new MapNavFragment();
            transaction.add(R.id.fragment_container, activeFragment, "map");
            transaction.commit();
        } catch (Exception e) {
            Log.e(TAG, "Error loading initial fragment: " + e.getMessage());
            Toast.makeText(this, "Ошибка загрузки карты", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                String tag = null;

                int itemId = item.getItemId();
                if (itemId == R.id.navigation_map) {
                    selectedFragment = new MapNavFragment();
                    tag = "map";
                } else if (itemId == R.id.navigation_profile) {
                    selectedFragment = new ProfileFragment();
                    tag = "profile";
                } else if (itemId == R.id.navigation_events) {
                    selectedFragment = new EventsFragment();
                    tag = "events";
                } else if (itemId == R.id.navigation_news) {
                    selectedFragment = new OtherFragment();
                    tag = "other";
                }

                if (selectedFragment != null && (activeFragment == null || !selectedFragment.getClass().equals(activeFragment.getClass()))) {
                    switchFragment(selectedFragment, tag);
                    return true;
                }
                return false;
            }
        });
    }

    private void switchFragment(Fragment newFragment, String tag) {
        try {
            if (newFragment == null || fragmentManager == null) return;
            
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out
            );
            
            // Detach current fragment if it exists
            if (activeFragment != null) {
                transaction.detach(activeFragment);
            }
            
            // Check if fragment with this tag already exists
            Fragment existingFragment = fragmentManager.findFragmentByTag(tag);
            if (existingFragment != null) {
                transaction.attach(existingFragment);
                activeFragment = existingFragment;
            } else {
                transaction.add(R.id.fragment_container, newFragment, tag);
                activeFragment = newFragment;
            }
            
            transaction.commit();
            updateBottomNavigation();
        } catch (Exception e) {
            Log.e(TAG, "Error switching fragment: " + e.getMessage());
            Toast.makeText(this, "Ошибка переключения контента", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBottomNavigation() {
        if (activeFragment instanceof MapNavFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_map);
        } else if (activeFragment instanceof ProfileFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_profile);
        } else if (activeFragment instanceof EventsFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_events);
        } else if (activeFragment instanceof OtherFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_news);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (activeFragment != null) {
            String tag = activeFragment.getTag();
            if (tag != null) {
                outState.putString(KEY_ACTIVE_FRAGMENT, tag);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (musicService != null && bound) {
            musicService.pauseMusic();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicService != null && bound) {
            musicService.resumeMusic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Notification permission granted");
            } else {
                Log.d(TAG, "Notification permission denied");
            }
        }
    }

    public void openPlaceOnMap(double latitude, double longitude, String title) {
        try {
            Uri gmmIntentUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + latitude + "," + longitude + "(" + title + ")");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Google Maps не установлен", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening map: " + e.getMessage());
            Toast.makeText(this, "Ошибка открытия карты", Toast.LENGTH_SHORT).show();
        }
    }

    public void switchToMapFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (activeFragment != null) {
            transaction.hide(activeFragment);
        }
        MapNavFragment mapFragment = (MapNavFragment) fragmentManager.findFragmentByTag("map");
        if (mapFragment == null) {
            mapFragment = new MapNavFragment();
            transaction.add(R.id.fragment_container, mapFragment, "map");
        } else {
            transaction.show(mapFragment);
        }
        activeFragment = mapFragment;
        transaction.commit();
        bottomNavigationView.setSelectedItemId(R.id.navigation_map);
    }

    public void showPlaceOnMap(double latitude, double longitude, String title) {
        MapNavFragment mapFragment = (MapNavFragment) fragmentManager.findFragmentByTag("map");
        if (mapFragment != null) {
            mapFragment.showPlace(latitude, longitude, title);
        }
    }

    public void switchToButovoChartFragment() {
        Log.d("MainActivity", "Начало переключения на график");
        try {
            if (getSupportFragmentManager() != null) {
                Log.d("MainActivity", "Создание нового фрагмента графика");
                ButovoChartFragment chartFragment = new ButovoChartFragment();
                
                Log.d("MainActivity", "Начало транзакции фрагмента");
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, chartFragment)
                    .addToBackStack(null)
                    .commit();
                Log.d("MainActivity", "Транзакция фрагмента завершена");
                
                // Обновляем выбранный пункт в нижней навигации
                if (bottomNavigationView != null) {
                    Log.d("MainActivity", "Обновление нижней навигации");
                    bottomNavigationView.setSelectedItemId(R.id.navigation_map);
                } else {
                    Log.e("MainActivity", "bottomNavigationView is null");
                }
            } else {
                Log.e("MainActivity", "getSupportFragmentManager() вернул null");
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Ошибка при переключении на график: " + e.getMessage(), e);
        }
    }
}

