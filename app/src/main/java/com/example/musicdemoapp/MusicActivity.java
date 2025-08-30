package com.example.musicdemoapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
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

import apodrepo.APodRepo;
import utilities.AlertHandler;

public class MusicActivity extends AppCompatActivity {
    Context context;
    RecyclerView recyclerView;
    TextView noMusicTextView, titleTv;
    ArrayList<AudioModel> songsList;
    ArrayList<String> albumsList;
    ArrayList<String> artistList;
    private final MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    APodRepo aPodRepo = new APodRepo();

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
        noMusicTextView = findViewById(R.id.no_songs_text);
        titleTv = findViewById(R.id.title_text);

        if(getIntent() == null){
            handleIntentFailure();
        }

        int viewSelection = (int) getIntent().getSerializableExtra("selection");
        if(viewSelection == 0){
            titleTv.setText("TRACKS");
            showAllMusic();
        }
        else if(viewSelection == 1){
            titleTv.setText("ALBUMS");
            showAllAlbums();
        }
        else if(viewSelection == 2){
            titleTv.setText("ARTISTS");
            showAllArtists();
        }
        else if (viewSelection == 3){
            nowPlaying();
        }
    }

    private void showAllMusic(){
        songsList = aPodRepo.getAllMusic(this);
        buildRecyclerView("All", songsList, getApplicationContext());
    }

    private void showAllAlbums(){
        albumsList = aPodRepo.getAllAlbums(this);
        buildRecyclerView("Album", albumsList, MusicActivity.this);
    }

    private void showAllArtists(){
        artistList = aPodRepo.getAllArtists(this);
        buildRecyclerView("Artist", artistList, MusicActivity.this);
    }

    private void nowPlaying(){
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            Intent intent = new Intent(context, MusicPlayerActivity.class);
            intent.setFlags(0);
            context.startActivity(intent);
        }
    }

    private void buildRecyclerView(String type, ArrayList<?> objList, Context context){
        if(objList.isEmpty()){
            noMusicTextView.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            switch(type){
                case "Artist":
                    recyclerView.setAdapter(new ArtistListAdapter(this.artistList, context));
                    break;
                case "Album":
                    recyclerView.setAdapter(new AlbumListAdapter(this.albumsList, context));
                    break;
                case"All":
                    recyclerView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
                    break;
            }
        }
    }

    private void handleIntentFailure(){
        AlertDialog.Builder builder = AlertHandler.okAlert(
                MusicActivity.this,
                "ERROR:",
                "Unexpected Error Occurred...");

        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}