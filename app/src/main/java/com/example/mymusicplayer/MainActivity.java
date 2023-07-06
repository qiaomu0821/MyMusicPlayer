package com.example.mymusicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView songListView;
    private SimpleAdapter songListAdapter;
    private List<Map<String, String>> songList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        songListView = findViewById(R.id.songListView);
        songList = initData();
        songListAdapter = new SimpleAdapter(
                this,
                songList,
                R.layout.item_main,
                new String[]{"songName", "songArtist", "songURL"},
                new int[]{R.id.songNameTextView, R.id.songArtistTextView, R.id.songURLTextView});
        songListView.setAdapter(songListAdapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                checkAndPlaySong(position);
            }
        });
    }
    private void checkAndPlaySong(int position) {
        // 检查歌曲文件是否存在，如果存在直接进入播放页面，如果不存在则下载后再进入播放页面
        String songName = songList.get(position).get("songName");
        String songArtist = songList.get(position).get("songArtist");
        String songURL = songList.get(position).get("songURL");
        String songPath = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), songName + ".mp3").getPath();
        boolean songExists = checkSongExists(songName);
        if (songExists) {
            // 进入播放页面
            Intent intent = new Intent(MainActivity.this, PlayActivity.class);
            intent.putExtra("songName", songName);
            intent.putExtra("songArtist", songArtist);
            intent.putExtra("songPath", songPath);
            startActivity(intent);
            Toast.makeText(this, "歌曲准备播放咯！", Toast.LENGTH_SHORT).show();
        } else {
            // 启动下载服务下载歌曲
            Intent intent = new Intent(MainActivity.this, DownloadService.class);
            intent.putExtra("songName", songName);
            intent.putExtra("songURL", songURL);
            startService(intent);
            Toast.makeText(this, "正在下载此歌曲...", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean checkSongExists(String songName) {
        File musicSaveDirectory = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), songName + ".mp3");
        boolean isDownloaded = musicSaveDirectory.exists();
        if (isDownloaded) {
            // 歌曲已经下载
            Log.d("GAO", "已下载: " + songName);
            Log.d("GAO", "保存路径: " + musicSaveDirectory);
            return true;
        } else {
            // 歌曲未下载
            Log.d("GAO", songName + " 未下载");
            return false;
        }
    }
    private List<Map<String, String>> initData() {
        List<Map<String, String>> list = new ArrayList<>();
        Map<String, String> map;
        map = new HashMap<>();
        map.put("songName", "江南");
        map.put("songArtist", "林俊杰");
        map.put("songURL", "https://s81.lanzoug.com/07052200116692048bb/2023/05/18/23b562adf60ee9da59dacb653f26f7dd.mp3?st=-8mfOOFO-6oHt1TMquPCLg&e=1688569748&b=UeUOvwKdB_bcC2l6fUC0D4VSfAcoE4Qe_bVYwI4wKYV_bVRLQw9ATMHOlUuVm1WdgA3&fi=116692048&pid=123-124-246-82&up=2&mp=0&co=1");
        list.add(map);
        map = new HashMap<>();
        map.put("songName", "认真的雪");
        map.put("songArtist", "薛之谦");
        map.put("songURL", "https://develope9.lanzoug.com/0706000090794629bb/2022/11/27/e28833c9e808276e48e4a153254f838c.mp3?st=pP7W9ZqFBgFMDl6QhPkBIg&e=1688575212&b=BL5c8lD0WLoEzV6XArVQzgTVWu9XzQSoBngO6waXV84Bt13kAokD7lC1VqYFewI3UjUJbQUqVD9ScQs1&fi=90794629&pid=123-124-246-82&up=2&mp=0&co=1");
        list.add(map);
        map = new HashMap<>();
        map.put("songName", "情非得已");
        map.put("songArtist", "庾澄庆");
        map.put("songURL", "https://develope2.lanzoug.com/0706000089857092bb/2022/11/21/522c7652ba53df1b995fee994fe1efe3.mp3?st=Y9C_wKGHJJyDpIRsl_rbhg&e=1688575380&b=VeEOjVPWVrpSml6WVuNV71GTCLFQ5gSwB3kA6FbrBLhR5QC_bUNRYuALtBNRQLlRhUTYOalZ5A2gIKwE_a&fi=89857092&pid=123-124-246-82&up=2&mp=0&co=1");
        list.add(map);
        map = new HashMap<>();
        map.put("songName", "简简单单");
        map.put("songArtist", "林俊杰");
        map.put("songURL", "https://develope9.lanzoug.com/0705220090966589bb/2022/11/28/5eddd1d17a800c51cf31f47c3f1d0704.mp3?st=DnXUZiejCyFmGKEWI_NCpQ&e=1688570225&b=BrMMolXVVLZTqAPVA7YOh1OTW_bIFiQGSVSsP5FTNX8oAtlrlUthQswTMAOYAfgYzUjUPawYpUTpTcFtl&fi=90966589&pid=123-124-246-82&up=2&mp=0&co=1");
        list.add(map);
        map = new HashMap<>();
        map.put("songName", "第二天堂");
        map.put("songArtist", "林俊杰");
        map.put("songURL", "https://develope71.lanzoug.com/0705220094646237bb/2022/12/24/0e0d90c0a9f420e1909f883e89cde583.mp3?st=Q-f7WsUZpgcx6Zm3Wihi0w&e=1688570316&b=VeBb9wmlU7ID7ALYBrMFpQP_aWuMFpAKGA30K4QeeBJFU4g_bwBowE5wfPBeMHeQI3BmEAZFF_bB2wAIwk3&fi=94646237&pid=123-124-246-82&up=2&mp=0&co=1");
        list.add(map);
        return list;
    }
}
