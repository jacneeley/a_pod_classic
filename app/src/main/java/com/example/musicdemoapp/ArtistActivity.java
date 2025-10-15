package com.example.musicdemoapp;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import apodrepo.APodRepo;
import utilities.AlertHandler;

public class ArtistActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView artistTv, albumTv, trackTv, noTrackTv;
    Button songsBtn, albumsBtn;
    boolean songSelected, albumSelected = false;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    ArrayList<ArrayList<String>> albumsList = new ArrayList<>();
    APodRepo aPodRepo = new APodRepo();
    String artistName = "";
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

        initUI();

        //query albums by default
        getAlbumsByArtist(artistName);
        buildRecyclerView(0, albumsList);

        handleSelection();
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

    private void initUI(){
        recyclerView = findViewById(R.id.recycler_view);
        artistTv = findViewById(R.id.artist_text);

        artistName = (String) getIntent().getSerializableExtra("ARTIST_NAME");
        artistTv.setText(artistName);

        albumsBtn = findViewById(R.id.albumBtn);
        albumsBtn.setBackgroundColor(Color.BLACK);
        albumsBtn.setTextColor(Color.WHITE);

        songsBtn = findViewById(R.id.songBtn);
        songsBtn.setBackgroundColor(Color.TRANSPARENT);
        songsBtn.setTextColor(Color.BLACK);
    }

    private void getAlbumsByArtist(String artistName){
        albumsList = aPodRepo.getAlbumsByArtist(this, artistName);
    }

    private void getSongsByArtist(String artistName){
        songsList = aPodRepo.getSongsByArtistName(this, artistName);
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
            recyclerView.setAdapter(new MusicListAdapter(songsList, ArtistActivity.this, "artist_actv"));
        }
    }

    private void handleSelection(){
        albumsBtn.setOnClickListener(ab -> {
            if(albumsList.isEmpty()){
                getAlbumsByArtist(artistName);
            }

            albumsBtn.setBackgroundColor(Color.BLACK);
            albumsBtn.setTextColor(Color.WHITE);
            songsBtn.setBackgroundColor(Color.TRANSPARENT);
            songsBtn.setTextColor(Color.BLACK);

            buildRecyclerView(0, albumsList);
        });

        songsBtn.setOnClickListener(sb -> {
            if(songsList.isEmpty()){
                getSongsByArtist(artistName);
            }

            songsBtn.setBackgroundColor(Color.BLACK);
            songsBtn.setTextColor(Color.WHITE);
            albumsBtn.setBackgroundColor(Color.TRANSPARENT);
            albumsBtn.setTextColor(Color.BLACK);

            buildRecyclerView(1, songsList);
        });
    }
}