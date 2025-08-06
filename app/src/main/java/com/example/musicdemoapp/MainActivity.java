package com.example.musicdemoapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("Startup Successful!");

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //activity IDs
        recyclerView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById((R.id.no_songs_text));

        //allow permission
        if(!this.checkPermissions()){
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
        };


        //access audio
        String selection = MediaStore.Audio.Media.IS_MUSIC + " !=0";

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                MediaStore.Audio.Media.TITLE + " ASC");

        while(cursor.moveToNext()){
            String path = cursor.getString(0);
            String title = cursor.getString(1);
            String artist = cursor.getString(2);
            String album = cursor.getString(3);
            String duration = cursor.getString(4);
            if (new File(path).exists()) {
                songsList.add(new AudioModel(path, title, artist, album, duration));
            }
        }
        cursor.close();

        if(songsList.isEmpty()){
            noMusicTextView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }

    private boolean checkPermissions(){
        int result = ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        );

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(
            MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
        )){
            Toast.makeText(MainActivity.this,
                    "Read Permission is required. Please give the app read permissions.",
                    Toast.LENGTH_LONG).show();

        }
        else{
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},200);
        }
    }
}