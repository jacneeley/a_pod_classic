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

import apodrepo.APodRepo;
import utilities.AlertHandler;

public class AlbumActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    TextView albumTv, noTracksTextView;
    ArrayList<AudioModel> songsList;

    APodRepo aPodRepo = new APodRepo();

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

        songsList = aPodRepo.getSongsByAlbumName(this, albumName);

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