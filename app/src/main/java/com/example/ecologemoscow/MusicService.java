package com.example.ecologemoscow;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicService extends Service {
    private static final String TAG = "MusicService";
    private final IBinder binder = new LocalBinder();
    private MediaPlayer mediaPlayer;
    private boolean wasPlaying = false;

    public class LocalBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.zvukprirody);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating MediaPlayer", e);
        }
    }

    public void startMusic() {
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error starting music", e);
        }
    }

    public void pauseMusic() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    wasPlaying = true;
                    mediaPlayer.pause();
                } else {
                    wasPlaying = false;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error pausing music", e);
        }
    }

    public void resumeMusic() {
        try {
            if (mediaPlayer != null && wasPlaying) {
                mediaPlayer.start();
                wasPlaying = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error resuming music", e);
        }
    }

    @Override
    public void onDestroy() {
        try {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping music", e);
        }
        super.onDestroy();
    }
} 