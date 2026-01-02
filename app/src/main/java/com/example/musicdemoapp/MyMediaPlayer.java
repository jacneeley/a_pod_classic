package com.example.musicdemoapp;

import android.media.MediaPlayer;

import java.util.LinkedList;

public class MyMediaPlayer {
    private static MediaPlayer mpInstance;
    public final static LinkedList<AudioModel> mainQueue = new LinkedList<>();
    public final static LinkedList<AudioModel> shuffleList = new LinkedList<>();
    public final static LinkedList<AudioModel> history = new LinkedList<>();
    public final static LinkedList<AudioModel> playing = new LinkedList<>();

    public static MediaPlayer getInstance(){
        if(mpInstance == null){
            mpInstance = new MediaPlayer();
        }
        return mpInstance;
    }

    public static int currentIndex = -1; //no song is playing
    public static String currentlyPlaying;
    public static int selectedIndex = -1; //if none negative 1
}
