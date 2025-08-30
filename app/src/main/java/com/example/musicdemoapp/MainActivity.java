package com.example.musicdemoapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

import apodrepo.APodRepo;
import utilities.AlbumArtMap;
import utilities.AlertHandler;

public class MainActivity extends AppCompatActivity {
    Context context;
    CardView songView, albumView, artistView, mediaView;
    APodRepo aPodRepo = new APodRepo();

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

        init();

        handleSelection();
    }

    private void init(){
        if(!this.checkPermissions()){
            requestPermission();
            return;
        }
        aPodRepo.getAllAlbumsArt(this);
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
            handleRejection();
        }
        else{
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},200);
        }
    }

    private void startActivityWithSelection(int selection){
        Intent intent = new Intent(this, MusicActivity.class);
        intent.putExtra("selection", selection);
        startActivity(intent);
    }

    private void handleSelection(){
        songView = findViewById(R.id.songs_card);
        songView.setOnClickListener(sv -> {
            startActivityWithSelection(0);
        });

        albumView = findViewById(R.id.albums_card);
        albumView.setOnClickListener(av -> {
            startActivityWithSelection(1);
        });

        artistView = findViewById(R.id.artists_card);
        artistView.setOnClickListener(arv -> {
            startActivityWithSelection(2);
        });

//        mediaView = findViewById(R.id.now_playing_card);
//        mediaView.setOnClickListener(arv -> {
//            startActivityWithSelection(3);
//        });
    }

    private void handleRejection(){
        AlertDialog.Builder builder = AlertHandler.okAlert(
                MainActivity.this,
                "Alert:",
                "aPod requires read permissions to access your device's audio files. You must grant access to continue using aPod.");

        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
            finish();
            startActivity(getIntent());
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}