package com.example.musicdemoapp;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

public class AlbumActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView albumTv, noTracksTextView;
    ArrayList<AudioModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_album);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkIntent();

        queryCursor();

        buildRecyclerView();
    }

    private void checkIntent(){
        if(getIntent() == null){
            AlertDialog.Builder builder = AlertHandler.okAlert(
                    AlbumActivity.this,
                    "ERROR:",
                    "Unexpected Error Occurred...");

            builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
                finish();
                startActivity(new Intent(this, MainActivity.class));
            });

            AlertDialog alert = builder.create();
            alert.show();
            Log.e(TAG, "onCreate: Error with Intent.",new Throwable(new NullPointerException("Intent is null")));
        }
    }

    private void queryCursor(){
        recyclerView = findViewById(R.id.recycler_view);
        albumTv = findViewById(R.id.album_text);

        String albumName = (String) getIntent().getSerializableExtra("ALBUM_NAME");
        albumTv.setText(albumName);

        String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION
        };

        String where = MediaStore.Audio.Media.ALBUM + "=?";
        String[] whereVal = { albumName };
        //String orderBy = MediaStore.Audio.Media.CD_TRACK_NUMBER;

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                where,
                whereVal,
                null
        );

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
    }

    private void buildRecyclerView(){
        if(songsList.isEmpty()){
            noTracksTextView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(new MusicListAdapter(songsList, AlbumActivity.this));
        }
    }
}