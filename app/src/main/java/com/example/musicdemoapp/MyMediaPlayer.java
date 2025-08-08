package com.example.musicdemoapp;

import android.media.MediaPlayer;

public class MyMediaPlayer {
    private static MediaPlayer mpInstance;

    public static MediaPlayer getInstance(){
        if(mpInstance == null){
            mpInstance = new MediaPlayer();
        }
        return mpInstance;
    }

    public static int currentIndex = -1; //no song is playing
    public static String currentlyPlaying;
}
