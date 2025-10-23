package com.example.musicdemoapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import utilities.AlbumArtMap;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder>{
    private final ArrayList<AudioModel> songsList;
    private final Context context;
    private static String actv;
    private Intent intent;
    private final HashMap<String, String> artMap = AlbumArtMap.getArtMap();

    public MusicListAdapter(ArrayList<AudioModel> songsList, Context context, String actv) {
        this.songsList = songsList;
        this.context = context;
        MusicListAdapter.actv = actv;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AudioModel songData = songsList.get(position);
        holder.titleTextView.setText(songData.getTitle());

        if(!actv.equalsIgnoreCase("album_actv")){
            setAlbumArt(holder, songData);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            Intent intent = getIntent();
            @Override
            public void onClick(View v){
                //navigate to song activity
                try{
                    if(MyMediaPlayer.currentIndex == -1 || MyMediaPlayer.currentIndex != position){
                        MyMediaPlayer.getInstance().reset();
                        MyMediaPlayer.currentIndex = position;
                        this.intent = new Intent(context, MusicPlayerActivity.class);
                        this.intent.putExtra("LIST", songsList);
                        this.intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }

                    if(this.intent == null){ //hotfix for now
                        MyMediaPlayer.currentIndex = position;
                        this.intent = new Intent(context, MusicPlayerActivity.class);
                        this.intent.putExtra("LIST", songsList);
                        this.intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    }
                    this.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(this.intent);

                } catch (Exception e){
                    e.printStackTrace();
                    Log.e(TAG, "onClick: " + e.getMessage());
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        ImageView iconImageView;
        String actv = MusicListAdapter.actv;
        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.music_title_txt);
            iconImageView = itemView.findViewById(R.id.icon_view);
            if(actv.equalsIgnoreCase("music_actv") || actv.equalsIgnoreCase("show_tracks")) {
                iconImageView.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    private Intent getIntent(){
        return this.intent;
    }

    private void setAlbumArt(MusicListAdapter.ViewHolder holder, AudioModel songData){
        String artId = songData.getArtist() + "_" + songData.getAlbum();
        String albumArtUriStr = artMap.get(artId);
        if(albumArtUriStr != null){
            holder.iconImageView.setImageURI(Uri.parse(albumArtUriStr));
        }
        else {
            holder.iconImageView.setImageResource(R.drawable.placeholder);
        }
    }
}
