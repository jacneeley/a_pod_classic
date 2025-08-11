package com.example.musicdemoapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import Utilities.AlertHandler;

public class MusicActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView noMusicTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    ArrayList<String> albumsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_song);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //activity IDs
        recyclerView = findViewById(R.id.recycler_view);
        noMusicTextView = findViewById((R.id.no_songs_text));

        if(getIntent() == null){
            //TODO: add alert
            AlertDialog.Builder builder = AlertHandler.okAlert(
                    MusicActivity.this,
                    "",
                    "");

            builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                finish();
                startActivity(new Intent(this, MainActivity.class));
            });

            AlertDialog alert = builder.create();
            alert.show();
        }

        int viewSelection = (int) getIntent().getSerializableExtra("selection");
        if(viewSelection == 0){
            String[] projection = {
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.DURATION,
            };

            //access tracks
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
            else {
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
            }
        }
        else if(viewSelection == 1){
            String[] projection = {
                    MediaStore.Audio.Albums._ID,
                    MediaStore.Audio.Albums.ALBUM
            };

            Cursor cursor = getContentResolver().query(
                    MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    MediaStore.Audio.Albums.ALBUM + " ASC"
            );

            while(cursor.moveToNext()){
                if(!cursor.getString(0).isEmpty()){
                    String album = cursor.getString(1);
                    albumsList.add(album);
                }
            }

            cursor.close();
        }
    }
}