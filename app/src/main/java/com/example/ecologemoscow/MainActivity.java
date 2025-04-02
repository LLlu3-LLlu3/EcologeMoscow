package com.example.ecologemoscow;

import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {
    private MusicService musicService;

    public static class MusicService {
        private final MediaPlayer mediaPlayer;

        public MusicService(android.content.Context context) {
            mediaPlayer = MediaPlayer.create(context, R.raw.zvukprirody);
            mediaPlayer.setLooping(true);
        }

        public void start() {
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        }

        public void stop() {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maint);

        // Add map fragment
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.map_container, new MapNavFragment());
            transaction.commit();

            // Add bottom navigation fragment
            FragmentTransaction bottomNavTransaction = getSupportFragmentManager().beginTransaction();
            bottomNavTransaction.replace(R.id.bottom_nav_container, new BottomNavFragment());
            bottomNavTransaction.commit();
        }

        // Start music service
        musicService = new MusicService(this);
        musicService.start();
    }

    @Override
    protected void onDestroy() {
        musicService.stop();
        super.onDestroy();
    }
}

