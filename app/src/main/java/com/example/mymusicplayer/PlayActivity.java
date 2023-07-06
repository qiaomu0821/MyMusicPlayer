package com.example.mymusicplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class PlayActivity extends AppCompatActivity {
    private boolean isBound = false;
    private boolean isPlaying = true;
    private ServiceConnection serviceConnection;
    private BroadcastReceiver songImageCircleReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String songName = intent.getStringExtra("songName");
            String songArtist = intent.getStringExtra("songArtist");
            updateSongInfo(songName, songArtist);
        }
    };
    private MusicService.MusicBinder binder;
    private MusicService musicService;
    private String songName;
    private String songArtist;
    private String songPath;
    private SeekBar seekBar;
    private TextView nameTextView;
    private TextView artistTextView;
    private ImageView imageView;
    private TextView progressTextView;
    private TextView totalTextView;
    private ImageButton pauseButton;
    private ImageButton lastSongButton;
    private ImageButton nextSongButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        songName = getIntent().getStringExtra("songName");
        songArtist = getIntent().getStringExtra("songArtist");
        songPath = getIntent().getStringExtra("songPath");
        nameTextView = findViewById(R.id.NameTextView);
        artistTextView = findViewById(R.id.AritistTextView);
        imageView = findViewById(R.id.imageView);
        seekBar = findViewById(R.id.seekBar);
        progressTextView = findViewById(R.id.progressTextView);
        totalTextView = findViewById(R.id.totalTextView);
        pauseButton = findViewById(R.id.pauseButton);
        lastSongButton = findViewById(R.id.lastSongButton);
        nextSongButton = findViewById(R.id.nextSongButton);
        IntentFilter filter = new IntentFilter("com.example.mymusicplayer.SONG_INFORMATION");
        LocalBroadcastManager.getInstance(this).registerReceiver(songImageCircleReceiver, filter);


        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                binder = (MusicService.MusicBinder) service;
                musicService = binder.getService();
                isBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isBound = false;
            }
        };

        nameTextView.setText(songName);
        artistTextView.setText(songArtist);
        imageView.setImageResource(R.drawable.circleimage);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    musicService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound) {
                    if (isPlaying) {
                        musicService.pause();
                        isPlaying = false;
                        pauseButton.setImageResource(R.drawable.baseline_play_circle_outline_24);
                    } else {
                        musicService.noPause();
                        isPlaying = true;
                        pauseButton.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                    }
                }
            }
        });

        lastSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound)
                    musicService.lastSong();
            }
        });

        nextSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isBound)
                    musicService.nextSong();
            }
        });

        updateSeekBar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 绑定 MusicService
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("songName", songName);
        intent.putExtra("songArtist", songArtist);
        intent.putExtra("songPath", songPath);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        startService(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 解绑 MusicService
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 注销广播接收器
        LocalBroadcastManager.getInstance(this).unregisterReceiver(songImageCircleReceiver);
    }

    private void updateSeekBar() {
        new Thread(() -> {
            while (isPlaying) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(() -> {
                    int currentPosition = musicService.getCurrentPosition();
                    int totalDuration = musicService.getDuration();
                    int durationMinutes = (totalDuration / 1000) / 60;
                    int durationSeconds = (totalDuration / 1000) % 60;
                    int currentPositionMinutes = (currentPosition / 1000) / 60;
                    int currentPositionSeconds = (currentPosition / 1000) % 60;
                    String durationString = String.format("%02d:%02d", durationMinutes, durationSeconds);
                    String currentPositionString = String.format("%02d:%02d", currentPositionMinutes, currentPositionSeconds);
                    progressTextView.setText("" + currentPositionString);
                    totalTextView.setText("" + durationString);
                    seekBar.setMax(totalDuration);
                    seekBar.setProgress(currentPosition);
                });
            }
        }).start();
    }

    private void updateSongInfo(String songName, String songArtist) {
        nameTextView.setText(songName);
        artistTextView.setText(songArtist);
    }
}

