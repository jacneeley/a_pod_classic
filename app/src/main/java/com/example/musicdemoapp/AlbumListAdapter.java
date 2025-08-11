package com.example.musicdemoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.AlbumViewHolder>{
    private ArrayList<String> albumsList;
    private Context context;

    public AlbumListAdapter(ArrayList<String> albumsList, Context context) {
        this.albumsList = albumsList;
        this.context = context;
    }

    @NonNull
    @Override
    public AlbumListAdapter.AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new AlbumListAdapter.AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumListAdapter.AlbumViewHolder holder, int position) {
        String album = albumsList.get(position);
        holder.albumTitleTextView.setText(album);

        holder.itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                try{
                    //TODO: query tracks by album
                    //TODO: try to relaunch the current activity with the list of tracks

                }  catch (Exception e){
                    e.getMessage();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() { return albumsList.size(); }

    public class AlbumViewHolder extends RecyclerView.ViewHolder{
        TextView albumTitleTextView;

        public AlbumViewHolder(View itemView){
            super(itemView);
            albumTitleTextView = itemView.findViewById(R.id.music_title_txt);
        }
    }
}
