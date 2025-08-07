package com.example.musicdemoapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    private TextView titleTv, artistTv, currentTimeTv, totalTimeTv;
    private SeekBar seekBar;
    private ImageView playPause, nextBtn, previousBtn, art;

    private ArrayList<AudioModel> songsList;
    private AudioModel currentSong;

    private MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    private boolean shuffle = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_music_player);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleTv = findViewById(R.id.song_title);
        artistTv = findViewById(R.id.song_artist);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);

        seekBar = findViewById(R.id.seek_bar);

        playPause = findViewById(R.id.pause);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);

        art = findViewById(R.id.album_art);

        titleTv.setSelected(true);
        artistTv.setSelected(true);

        songsList = getIntent().getSerializableExtra("LIST") != null ? (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST") : new ArrayList<>();

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(mediaPlayer != null){
                    // linter will still complain about null pointer but this will handle it.
                    int prog = Integer.valueOf(mediaPlayer.getCurrentPosition()) != null ? mediaPlayer.getCurrentPosition() : 0;
                    seekBar.setProgress(prog);

                    int currentPos = mediaPlayer.getCurrentPosition();
                    currentTimeTv.setText(convertToMinutesAndSeconds(String.valueOf(currentPos)));

                    if(mediaPlayer.isPlaying()){
                        playPause.setImageResource(R.drawable.baseline_pause_24);
                    }
                    else {
                        currentPos = currentPos / 1000;
                        int total = mediaPlayer.getDuration() / 1000;
                        if(total == currentPos){
                            playNextSong();
                            //TODO: maybe find a way to make transitions more seamless.
                        }
                        playPause.setImageResource(R.drawable.baseline_play_circle);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { /*do nothing for now */ }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { /*do nothing for now */ }
        });
    }

    private void setResourcesWithMusic(){
        if(!songsList.isEmpty()){
            currentSong = songsList.get(MyMediaPlayer.currentIndex);

            titleTv.setText(currentSong.getTitle());
            artistTv.setText(currentSong.getArtist().isBlank() ? "" : currentSong.getArtist());
            totalTimeTv.setText(convertToMinutesAndSeconds(currentSong.duration));

            playPause.setOnClickListener(v -> pausePlay());
            nextBtn.setOnClickListener(v -> playNextSong());
            previousBtn.setOnClickListener(v -> playPreviousSong());


            if(!mediaPlayer.isPlaying()){
                playMusic();
            }
        }
        else {
            handleEmptySongList();
        }
    }

    @SuppressLint("DefaultLocale") //This should not cause problems as chars are not being used.
    private static String convertToMinutesAndSeconds(String duration){
        long ms = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(ms) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(ms) % TimeUnit.MINUTES.toSeconds(1)
        );
    }

    private void playMusic(){
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(mediaPlayer.getDuration());

        } catch (IOException e){
            e.printStackTrace();
            e.getMessage();
        }
    }

    private void playNextSong(){
//        if(shuffle){
//            //TODO: add logic for shuffle
//        }

        if(MyMediaPlayer.currentIndex != songsList.size() - 1){
            MyMediaPlayer.currentIndex += 1;
            mediaPlayer.reset();
            setResourcesWithMusic();
        }
    }

    private void playPreviousSong(){
        if(MyMediaPlayer.currentIndex != 0){
            MyMediaPlayer.currentIndex -= 1;
            mediaPlayer.reset();
            setResourcesWithMusic();
        }
        /*TODO: if song is not finished then restart
        /* maybe something like if current duration > 0:05 restart */
    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        else{
            mediaPlayer.start();
        }
    }

    private void handleEmptySongList(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MusicPlayerActivity.this);
        builder.setTitle("Alert:");
        builder.setMessage("ERROR: Track could not be found...");
        builder.setCancelable(false);

        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
            //go back
            Intent intent = new Intent(MusicPlayerActivity.this, MainActivity.class);
            startActivity(intent);
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}