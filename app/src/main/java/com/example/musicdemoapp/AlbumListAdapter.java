package com.example.musicdemoapp;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import utilities.AlbumArtMap;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder>{
    private ArrayList<ArrayList<String>> albumsList;
    private Context context;
    private Intent intent;
    private final HashMap<String, String> artMap = AlbumArtMap.getArtMap();

    public AlbumListAdapter(ArrayList<ArrayList<String>> albumsList, Context context) {
        this.albumsList = albumsList;
        this.context = context;
    }

    @Override
    public AlbumListAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new AlbumListAdapter.AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumListAdapter.AlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String album = albumsList.get(position).get(1);
        holder.albumTitleTextView.setText(album);

        setAlbumArt(holder, albumsList.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            Intent intent = getIntent();
            @Override
            public void onClick(View v) {
                try{
                    this.intent = new Intent(context, AlbumActivity.class);
                    this.intent.putExtra("ALBUM", albumsList.get(position));
                    this.intent.setFlags(0);

                    context.startActivity(this.intent);
                }  catch (Exception e){
                    Log.e(TAG, "Failed to start AlbumActivity", e);
                    //Log.e(TAG, "onClick: ", e);
                    Log.i(TAG, "onClick: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() { return albumsList.size(); }

    public Intent getIntent(){
        return this.intent;
    }

    private void setAlbumArt(AlbumListAdapter.AlbumViewHolder holder, ArrayList<String> albumInfo){
        String albumId = albumInfo.get(0);
        String albumName = albumInfo.get(1);
        String albumArtUriStr = artMap.get(albumId) != null ? artMap.get(albumId) : artMap.get(albumName);
        if(albumArtUriStr != null){
            holder.albumCoverArt.setImageURI(Uri.parse(albumArtUriStr));
            if(!albumInfo.isEmpty()){
                albumInfo.add(albumArtUriStr);
            }

        }
        else {
            holder.albumCoverArt.setImageResource(R.drawable.placeholder);
            if(!albumInfo.isEmpty()){
                albumInfo.add("");
            }
        }
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{
        TextView albumTitleTextView;
        ImageView albumCoverArt;

        public AlbumViewHolder(View itemView){
            super(itemView);
            albumTitleTextView = itemView.findViewById(R.id.music_title_txt);
            albumCoverArt = itemView.findViewById(R.id.icon_view);
            albumCoverArt.setVisibility(View.VISIBLE);
        }
    }
}
