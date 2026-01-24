package com.example.musicdemoapp;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import utilities.AlbumArtMap;
import utilities.AlertHandler;
import utilities.QueueManager;

public class MusicPlayerActivity extends AppCompatActivity {
    private TextView titleTv, artistTv, currentTimeTv, totalTimeTv, albumTv;
    private SeekBar seekBar;
    private ImageView playPause, nextBtn, previousBtn, albumArt, shuffleBtn, queueMenu;
    private ArrayList<AudioModel> songsList;
    private AudioModel currentSong;
    private AudioModel last;
    private final MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    private final HashMap<String, String> artMap = AlbumArtMap.getArtMap();
    
    //TODO: make the linkedlists members of MyMediaPlayer Class
    private final static LinkedList<AudioModel>  mainQueue = new LinkedList<>();
    private final static LinkedList<AudioModel> shuffleList = new LinkedList<>();
    private final static LinkedList<AudioModel> history = new LinkedList<>();
    private final static LinkedList<AudioModel> playing = new LinkedList<>();
    private String currentArt;
    private static boolean isShuffle = false;
    private boolean itemClicked = false;

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

        manageSong();

        MusicPlayerActivity.this.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int prog = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(prog);

                    currentTimeTv.setText(convertToMinutesAndSeconds(prog));

                    if(prog != 0){
                        prog = prog / 1000;
                    }

                    int total = mediaPlayer.getDuration();
                    if(total < 0){
                        total = 0;
                    }

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

                    if(isShuffle){
                        shuffleBtn.setImageResource(R.drawable.shuffle_selected);
                    }
                    else{
                        shuffleBtn.setImageResource(R.drawable.shuffle);
                    }

                    new Handler().postDelayed(this, 50);
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
        if(mainQueue.isEmpty() && !songsList.isEmpty()){
            mainQueue.addAll(songsList);
            updateMainQueue();
        }

        //linter will try and simplify this, but i don't want that. I want all nulls to be treated as false no matter what.
        itemClicked = getIntent().getSerializableExtra("shortClick") == null ? false : (boolean) getIntent().getSerializableExtra("shortClick");

        //queue menu
        queueMenu = findViewById(R.id.queue_menu);

    }

    private void manageSong(){
        if( itemClicked ){ //bypass everything & get the song that was touched
            if(!mainQueue.isEmpty()){
                currentSong = songsList.get(MyMediaPlayer.selectedIndex);
                MyMediaPlayer.currentIndex = playing.indexOf(currentSong);
                itemClicked = false;

            }
            else {
                handleEmptySongList();
            }
        }
        else if(!QueueManager.isEmpty()) {
            currentSong = QueueManager.getHead();
            QueueManager.next();
        }

        else{ //fallback on mainqueue
            currentSong = playing.get(MyMediaPlayer.currentIndex);
        }
        preparePlayerWithSong();

        playMusic();

    }

    private void preparePlayerWithSong(){
        titleTv.setText(currentSong.getTitle());
        artistTv.setText(currentSong.getArtist().isBlank() ? "" : currentSong.getArtist());
        albumTv.setText(currentSong.getAlbum().isBlank() ? "" : currentSong.getAlbum());

        setAlbumArt();

        totalTimeTv.setText(convertToMinutesAndSeconds(Long.parseLong(currentSong.duration)));

        shuffleBtn.setOnClickListener(v -> isShuffleSongs());

        queueMenu.setOnClickListener(this::prepareQueueMenu);

        playPause.setOnClickListener(v -> pausePlay());
        nextBtn.setOnClickListener(v -> playNextSong());
        previousBtn.setOnClickListener(v -> playPreviousSong());
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
        //TODO: figure out why 'Intents' break when in isShuffle mode. --This seems to be fine 09/30/25
        //TODO: figure out why mediaplayer does not update when shuffle is true.
        if(QueueManager.isEmpty()){
            MyMediaPlayer.currentIndex++;
        }

        history.add(currentSong);

        mediaPlayer.reset();
        manageSong();
    }

    private void playPreviousSong(){
        if(QueueManager.isEmpty()){
            long prog = TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getCurrentPosition());
            if(MyMediaPlayer.currentIndex != 0){
                if((prog) < 5){
                    MyMediaPlayer.currentIndex = MyMediaPlayer.currentIndex - 1;
                }

                mediaPlayer.reset();
                manageSong();
            }
            else {
                Toast.makeText(this, "Queue is already at the most recent.", Toast.LENGTH_SHORT).show();
            }

        }
        else {
            playing.clear();
            playing.addAll(history);

            if(MyMediaPlayer.currentIndex > history.size()){
                MyMediaPlayer.currentIndex = history.size() - 1;
            }
            else {
                MyMediaPlayer.currentIndex = MyMediaPlayer.currentIndex - 1;
            }

            if(MyMediaPlayer.currentIndex == history.size() && isShuffle){
                playing.clear();
                playing.addAll(shuffleList);
            }
            else if(MyMediaPlayer.currentIndex == history.size()){
                playing.clear();
                playing.addAll(mainQueue);
            }
        }
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
        if(isShuffle){
            shuffleBtn.setImageResource(R.drawable.shuffle_selected);
            handleShuffle();
            Toast.makeText(this, "Shuffle is On", Toast.LENGTH_SHORT).show();
        }
        else{
            shuffleBtn.setImageResource(R.drawable.shuffle);
            shuffleList.clear();
            updateMainQueue();
            MyMediaPlayer.currentIndex = playing.indexOf(currentSong);
            Toast.makeText(this, "Shuffle is Off.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setAlbumArt(){
//        if(GlobalConstants.AFTERQ){
//            setAlbumArtAfterQ();
//        }
//        else {
//            setAlbumArtBeforeQ();
//        }
        setAlbumArtBeforeQ();
    }

    private void setAlbumArtBeforeQ(){
        //set albumArt image
        String artId = currentSong.getArtist()+ "_" + currentSong.getAlbum();
        String albumArtUriStr = artMap.get(artId);
        if(albumArtUriStr == null) {
            albumArt.setImageResource(R.drawable.placeholder);
            currentArt = "";
            //getAlbumArtByAlbumId();
        }
        else if(!albumArtUriStr.equalsIgnoreCase(currentArt)){
            albumArt.setImageURI(Uri.parse(albumArtUriStr));
            currentArt = albumArtUriStr;
            currentSong.setAlbumArtPath(albumArtUriStr);
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private void setAlbumArtAfterQ(){
        String artId = currentSong.getArtist()+ "_" + currentSong.getAlbum();
        String mediaUriStr = artMap.get(artId);
        if(!mediaUriStr.equalsIgnoreCase(currentArt)){
            try {
                Bitmap thumbnail = getContentResolver().loadThumbnail(
                        Uri.parse(mediaUriStr),
                        new Size(300, 300),
                        null
                );
                albumArt.setImageBitmap(thumbnail);
                currentArt = mediaUriStr;
                currentSong.setAlbumArtPath(mediaUriStr);
            } catch (IOException e){
                Log.i(TAG, "setAlbumArtAfterQ: Could not find album art...\nUsing placeholder.");
                albumArt.setImageResource(R.drawable.placeholder);
                currentArt = "";
                currentSong.setAlbumArtPath("");
            }
        }
    }

    private void prepareQueueMenu(View v){
        try{
            PopupMenu queueMenu = new PopupMenu(this, v);

            Menu menu = queueMenu.getMenu();
            menu.clear();

            HashMap<Integer, AudioModel> queueMap;

            if(QueueManager.isEmpty()){
                queueMap = null;
                menu.add(0, -1, Menu.NONE,"Queue is Empty" );

            } else {
                queueMap = new HashMap<>();
                var queue = QueueManager.getQueue();

                int itemId = 0;
                MenuItem clearItem = menu.add(0, itemId, Menu.NONE, "CLEAR");

                clearItem.setOnMenuItemClickListener(v1 -> {
                QueueManager.clearQueue();

                Toast.makeText(this, "Queue has been cleared.", Toast.LENGTH_SHORT).show();

                queueMenu.dismiss();
                prepareQueueMenu(v);
                    return true;
                });

                itemId++;
                MenuItem editItem = menu.add(0, itemId, Menu.NONE, "EDIT");

                editItem.setOnMenuItemClickListener(v2 -> {
                    Toast.makeText(this, "edit queue clicked.", Toast.LENGTH_SHORT).show(); //temp
                    return true;
                });

                for(var q : queue){
                    itemId++;
                    MenuItem menuItem = menu.add(0, itemId, Menu.NONE, q.title);
                    queueMap.put(itemId, q);
                    menuItem.setOnMenuItemClickListener(v3 -> {
                        currentSong = q;
                        mediaPlayer.reset();
                        preparePlayerWithSong();
                        playMusic();
                        queueMenu.dismiss();
                        Toast.makeText(this, "Bypassing queue.", Toast.LENGTH_SHORT).show();
                        return true;
                    });
                }
            }

            //handle empty queue
            queueMenu.setOnMenuItemClickListener(v4 -> true);

            queueMenu.show();

        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(TAG, "prepareQueueMenu error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    ///////////////////////
    /// HELPER METHODS ///
    /////////////////////

    /**
     * convert long duration to string.
     * @param duration the duration in milliseconds represented as a long
     * @return MM:SS as string
     */
    @SuppressLint("DefaultLocale") //This should not cause problems as chars are not being used.
    private static String convertToMinutesAndSeconds(long duration){
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(duration) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(duration) % TimeUnit.MINUTES.toSeconds(1)
        );
    }

    /**
     * maintain history for playPreviousSong() using a Double LinkedList for easy traversal.
     */
    private void handleHistory(){
        MyMediaPlayer.currentIndex -= 1;
        if(last == null){
            currentSong = history.peekLast();
            last = history.peekLast();

            preparePlayerWithSong();

            playMusic();
        }

        else {
            int idxLast = history.lastIndexOf(last);
            if(idxLast - 1 >= 0){
                currentSong = history.get(idxLast - 1);

                preparePlayerWithSong();

                playMusic();
            }
            else {
                Toast.makeText(this, "Queue is already at the most recent.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * shuffle songsList and update queue with shuffledList
     */
    private void handleShuffle(){
        ArrayList<AudioModel> tmpShuffleList = shuffleAlgo(songsList);
        tmpShuffleList.trimToSize();

        if(!tmpShuffleList.isEmpty()) {
            shuffleList.clear();
            shuffleList.addAll(tmpShuffleList);
            playing.clear();
            playing.addAll(shuffleList);
            MyMediaPlayer.currentIndex = -1;
        }
        tmpShuffleList = null;
    }

    /**
     * Randomize songsList in place using Fisher-Yates algorithm.
     * This algorithm has an efficient time complexity for objects of size 10^8, worst case 10^6.
     * Time Complexity: O(n) amortized.
     * @param list arraylist of AudioModels
     * @return list the shuffled list
     */
    private ArrayList<AudioModel> shuffleAlgo(ArrayList<AudioModel> list){
        try{
            list.trimToSize();

            int n = list.size();
            Random r = new Random();

            //start from the end and swap one-by-one
            //skip the first element
            for(int i = n - 1; i > 0; i--){
                //random index for shuffling
                int j = r.nextInt(i + 1);

                //swap objects at index
                AudioModel temp = list.get(i);
                list.remove(i);
                list.add(i, list.get(j));
                list.remove(j);
                list.add(j, temp);
            }

            return list;

        } catch (Exception e) {
            Log.e(TAG, "shuffleAlgo: Failed to shuffle - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void updateMainQueue(){
        playing.clear();
        playing.addAll(mainQueue);
    }

    private void handleEmptySongList(){
        AlertDialog.Builder builder = AlertHandler.okAlert(MusicPlayerActivity.this, "Alert:", "ERROR: Track could not be found...");
        builder.setPositiveButton("OK", (dialog, which) -> {
            //go back
            Intent intent = new Intent(MusicPlayerActivity.this, MainActivity.class);
            startActivity(intent);
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
