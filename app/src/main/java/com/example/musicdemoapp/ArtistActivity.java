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

import java.util.ArrayList;

import Utilities.AlertHandler;

public class ArtistActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView artistTv, albumTv, trackTv, noTrackTv;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    ArrayList<String> albumsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_artist);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkIntent();

        //query albums by default
        getAlbumsByArtist();

        buildRecyclerView(0, albumsList);
    }

    private void checkIntent(){
        if(getIntent() == null){
            AlertDialog.Builder builder = AlertHandler.okAlert(
                    ArtistActivity.this,
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

    private void getAlbumsByArtist(){
        recyclerView = findViewById(R.id.recycler_view);
        artistTv = findViewById(R.id.artist_text);

        String artistName = (String) getIntent().getSerializableExtra("ARTIST_NAME");
        artistTv.setText(artistName);

        String[] projection = {
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.ALBUM
        };

        String where = MediaStore.Audio.Media.ARTIST + "=?";
        String[] whereVal = { artistName };
        String sortOrder = MediaStore.Audio.Media.ALBUM + " ASC";

        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                where,
                whereVal,
                sortOrder
        );

        while(cursor.moveToNext()){
            if(!cursor.getString(0).isEmpty()){
                String album = cursor.getString(1);
                albumsList.add(album);
            }
        }
        cursor.close();
    }

    private void buildRecyclerView(int view, ArrayList<?> audioList){
        if(audioList.isEmpty()){
            noTrackTv.setVisibility(View.VISIBLE);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(view == 0){
            //album view will be the default
            recyclerView.setAdapter(new AlbumListAdapter(albumsList, ArtistActivity.this));
        }
        else {
            //show all tracks for artist
            recyclerView.setAdapter(new MusicListAdapter(songsList, ArtistActivity.this));
        }
    }
}