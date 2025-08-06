package com.example.musicdemoapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {
    TextView titleTv, currentTimeTv, totalTimeTv;
    SeekBar seekBar;
    ImageView playPause, nextBtn, previousBtn, art;

    ArrayList<AudioModel> songsList;
    AudioModel currentSong;


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
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);

        seekBar = findViewById(R.id.seek_bar);

        playPause = findViewById(R.id.play_pause);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);

        art = findViewById(R.id.album_art);

        titleTv.setSelected(true);

        songsList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST");

        setResourcesWithMusic();
    }

    private void setResourcesWithMusic(){
        currentSong = songsList.get(MyMediaPlayer.currentIndex);

        titleTv.setText(currentSong.getTitle());
        totalTimeTv.setText(convertToMMS(currentSong.duration));

        playPause.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());

        playMusic();
    }

    @SuppressLint("DefaultLocale") //locale could cause problems but for now the focus is on english speaking users.
    private static String convertToMMS(String duration){
        long ms = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(ms) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(ms) % TimeUnit.MINUTES.toSeconds(1)
        );
    }

    private void playMusic(){}

    private void playNextSong(){}

    private void playPreviousSong(){}

    private void pausePlay(){}
}