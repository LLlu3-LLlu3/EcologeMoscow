package com.example.ecologemoscow;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShopsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap googleMap;
    private DatabaseReference shopsRef;
    private List<Shop> shops;
    private View shopInfoView;
    private boolean isInfoVisible = false;
    private BitmapDescriptor customMarkerIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.shops_fragment, container, false);
        
        // Инициализация Firebase
        shopsRef = FirebaseDatabase.getInstance().getReference("shops");
        shops = new ArrayList<>();

        //
        customMarkerIcon = getBitmapDescriptor(R.drawable.iconshop);

        // Инициализация карты
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.shops_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Инициализация view для информации о магазине
        shopInfoView = view.findViewById(R.id.shop_info_container);
        shopInfoView.setVisibility(View.GONE);

        // Кнопка закрытия информации о магазине
        view.findViewById(R.id.close_shop_info).setOnClickListener(v -> {
            shopInfoView.setVisibility(View.GONE);
            isInfoVisible = false;
        });

        return view;
    }

    private BitmapDescriptor getBitmapDescriptor(int vectorResId) {
        if (getContext() == null) return null;
        
        Drawable vectorDrawable = ContextCompat.getDrawable(getContext(), vectorResId);
        if (vectorDrawable == null) return null;
        
        // Размер иконки
        int size = 120;
        
        // Создаем квадратный битмап
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        // Устанавливаем размеры drawable
        vectorDrawable.setBounds(0, 0, size, size);
        
        // Рисуем drawable на канвасе
        vectorDrawable.draw(canvas);
        
        // Создаем круглый битмап
        Bitmap outputBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas outputCanvas = new Canvas(outputBitmap);
        
        // Настраиваем Paint для создания круглой маски
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        outputCanvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
        
        // Настраиваем Paint для наложения изображения
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        outputCanvas.drawBitmap(bitmap, 0, 0, paint);
        
        return BitmapDescriptorFactory.fromBitmap(outputBitmap);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Центрируем карту на Москве
        LatLng moscow = new LatLng(55.751999, 37.617499);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(moscow, 10));

        // Добавляем тестовые магазины
        addTestShops();

        // Обработчик клика по маркеру
        googleMap.setOnMarkerClickListener(marker -> {
            Shop shop = (Shop) marker.getTag();
            if (shop != null) {
                showShopInfo(shop);
                return true;
            }
            return false;
        });
    }

    private void addTestShops() {
        // Тестовые магазины
        Shop[] testShops = {
            new Shop("ЭкоМаркет Южное Бутово", "09:00", "21:00", 55.5425, 37.5308),
            new Shop("Зеленый Мир", "10:00", "22:00", 55.5725, 37.5608),
            new Shop("ЭкоПром", "08:00", "20:00", 55.5575, 37.5458),
            new Shop("Природа", "09:00", "21:00", 55.5625, 37.5358),
            new Shop("ЭкоПлюс", "10:00", "22:00", 55.5475, 37.5508)
        };

        // Добавляем маркеры для тестовых магазинов
        for (Shop shop : testShops) {
            addShopMarker(shop);
        }
    }

    private void addShopMarker(Shop shop) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(shop.getLatitude(), shop.getLongitude()))
                .icon(customMarkerIcon)
                .title(shop.getName());

        Marker marker = googleMap.addMarker(markerOptions);
        if (marker != null) {
            marker.setTag(shop);
        }
    }

    private void showShopInfo(Shop shop) {
        // Заполняем информацию о магазине
        TextView shopName = shopInfoView.findViewById(R.id.shop_name);
        TextView shopTime = shopInfoView.findViewById(R.id.shop_time);

        shopName.setText(shop.getName());
        shopTime.setText(String.format("Время работы: %s - %s", shop.getOpenTime(), shop.getCloseTime()));

        // Показываем информацию
        shopInfoView.setVisibility(View.VISIBLE);
        isInfoVisible = true;

        // Обработчик закрытия окна информации
        View closeButton = shopInfoView.findViewById(R.id.close_shop_info);
        closeButton.setOnClickListener(v -> {
            shopInfoView.setVisibility(View.GONE);
            isInfoVisible = false;
        });
    }

    // Класс для хранения информации о магазине
    public static class Shop {
        private String name;
        private String openTime;
        private String closeTime;
        private double latitude;
        private double longitude;

        public Shop() {}

        public Shop(String name, String openTime, String closeTime, double latitude, double longitude) {
            this.name = name;
            this.openTime = openTime;
            this.closeTime = closeTime;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getOpenTime() { return openTime; }
        public void setOpenTime(String openTime) { this.openTime = openTime; }
        public String getCloseTime() { return closeTime; }
        public void setCloseTime(String closeTime) { this.closeTime = closeTime; }
        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }
        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }
}
