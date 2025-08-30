package com.example.musicdemoapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import utilities.AlbumArtMap;
import utilities.AlertHandler;

public class MusicPlayerActivity extends AppCompatActivity {
    private Context context;
    private TextView titleTv, artistTv, currentTimeTv, totalTimeTv, albumTv;
    private SeekBar seekBar;
    private ImageView playPause, nextBtn, previousBtn, albumArt, shuffleBtn;
    private ArrayList<AudioModel> songsList;
    private AudioModel currentSong;

    private final MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();

    private final HashMap<String, String> artMap = AlbumArtMap.getArtMap();
    private String currentArt;
    private static boolean isShuffle = false;

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

        init();

        setResourcesWithMusic();

        MusicPlayerActivity.this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int prog = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(prog);

                    currentTimeTv.setText(convertToMinutesAndSeconds((long) prog));

                    prog = prog != 0 ? prog / 1000 : 0;
                    int total = mediaPlayer.getDuration() / 1000;
                    String curr = MyMediaPlayer.currentlyPlaying;
                    if(mediaPlayer.isPlaying() && total == prog && curr.equals(currentSong.getTitle())) {
                        playNextSong();
                        //TODO: maybe find a way to make transitions more seamless.
                    }

                    if(mediaPlayer.isPlaying()){
                        playPause.setImageResource(R.drawable.baseline_pause_24);
                    }
                    else {
                        playPause.setImageResource(R.drawable.baseline_play_circle);
                    }

                    new Handler().postDelayed(this, 100);
                }
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

    private void init(){
        titleTv = findViewById(R.id.song_title);
        artistTv = findViewById(R.id.song_artist);
        albumTv = findViewById(R.id.album_text);
        currentTimeTv = findViewById(R.id.current_time);
        totalTimeTv = findViewById(R.id.total_time);
        shuffleBtn = findViewById(R.id.shuffle);

        seekBar = findViewById(R.id.seek_bar);

        playPause = findViewById(R.id.pause);
        nextBtn = findViewById(R.id.next);
        previousBtn = findViewById(R.id.previous);

        albumArt = findViewById(R.id.album_art);

        titleTv.setSelected(true);
        artistTv.setSelected(true);
        albumTv.setSelected(true);
        albumArt.setSelected(true);

        songsList = getIntent().getSerializableExtra("LIST") != null ? (ArrayList<AudioModel>) getIntent().getSerializableExtra("LIST") : new ArrayList<>();
    }

    private void setResourcesWithMusic(){
        if(!songsList.isEmpty()){
            currentSong = songsList.get(MyMediaPlayer.currentIndex);

            titleTv.setText(currentSong.getTitle());
            artistTv.setText(currentSong.getArtist().isBlank() ? "" : currentSong.getArtist());
            albumTv.setText(currentSong.getAlbum().isBlank() ? "" : currentSong.getAlbum());

            getAlbumArt();
            
            totalTimeTv.setText(convertToMinutesAndSeconds(Long.parseLong(currentSong.duration)));

            shuffleBtn.setOnClickListener(v -> isShuffleSongs());

            playPause.setOnClickListener(v -> pausePlay());
            nextBtn.setOnClickListener(v -> playNextSong());
            previousBtn.setOnClickListener(v -> playPreviousSong());

            playMusic();

        }
        else {
            handleEmptySongList();
        }
    }

    @SuppressLint("DefaultLocale") //This should not cause problems as chars are not being used.
    private static String convertToMinutesAndSeconds(long duration){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)
        );
    }

    private void playMusic(){
        try {
            String curr = currentSong.getTitle();
            if(!mediaPlayer.isPlaying() || !curr.equals(MyMediaPlayer.currentlyPlaying)){
                mediaPlayer.reset();
                mediaPlayer.setDataSource(currentSong.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
                seekBar.setProgress(0);
                MyMediaPlayer.currentlyPlaying = currentSong.getTitle();
            }

            seekBar.setMax(Integer.parseInt(currentSong.duration));

        } catch (IOException e){
            e.printStackTrace();
            e.getMessage();
        }
    }

    private void playNextSong(){
        //TODO: figure out why 'Intents' break when in isShuffle mode.

        if(isShuffle){
            int min = 0;
            int max = songsList.size() - 1;
            MyMediaPlayer.currentIndex = new Random().nextInt(max - min) + min;
        }

        else if(MyMediaPlayer.currentIndex != songsList.size() - 1){
            MyMediaPlayer.currentIndex += 1;
        }

        mediaPlayer.reset();
        setResourcesWithMusic();
        //null exception occurs when playNextSong plays the next song and user clicks on a new song, context is null.
        //updateIntent();
    }

    private void playPreviousSong(){
        long prog = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition());
        if((prog) < 5 && MyMediaPlayer.currentIndex != 0){
            MyMediaPlayer.currentIndex -= 1;
        }

        mediaPlayer.reset();
        setResourcesWithMusic();
    }

    private void pausePlay(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        else{
            mediaPlayer.start();
        }
    }

    private void isShuffleSongs(){
        isShuffle = !isShuffle;
    }

    private void getAlbumArt(){
        //set albumArt image
        String albumArtUriStr = artMap.get(currentSong.getAlbumId());
        if(albumArtUriStr == null) {
            albumArt.setImageResource(R.drawable.placeholder);
            currentArt = "";
            //getAlbumArtByAlbumId();
        }
        else if(!albumArtUriStr.equalsIgnoreCase(currentArt)){
            albumArt.setImageURI(Uri.parse(albumArtUriStr));
            currentArt = albumArtUriStr;
        }
    }

    private void handleEmptySongList(){
        AlertDialog.Builder builder = AlertHandler.okAlert(MusicPlayerActivity.this, "Alert:", "ERROR: Track could not be found...");
        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
            //go back
            Intent intent = new Intent(MusicPlayerActivity.this, MainActivity.class);
            startActivity(intent);
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //TODO: delete this later but leave it for now
//    private void getAlbumArtByAlbumId(){
//        String albumId = currentSong.getAlbumId();
//
//        String[] projection = { MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART };
//
//        String where = MediaStore.Audio.Albums._ID + "=?";
//
//        Cursor cursor = getContentResolver().query(
//                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
//                projection,
//                where,
//                new String[] { albumId },
//                null
//        );
//
//        if(cursor != null & cursor.moveToFirst()){
//            String path = cursor.getString(1);
//            if(path != null){
//                albumArt.setImageURI(Uri.parse(path));
//                artMap.put(albumId, path);
//            }
//            else {
//                albumArt.setImageResource(R.drawable.placeholder);
//            }
//        }
//
//        cursor.close();
//    }
}