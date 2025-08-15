package com.example.musicdemoapp;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
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

    private Intent intent;

    public AlbumListAdapter(ArrayList<String> albumsList, Context context) {
        this.albumsList = albumsList;
        this.context = context;
    }

    @Override
    public AlbumListAdapter.AlbumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new AlbumListAdapter.AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumListAdapter.AlbumViewHolder holder, int position) {
        String album = albumsList.get(position);
        holder.albumTitleTextView.setText(album);

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            Intent intent = getIntent();
            @Override
            public void onClick(View v) {
                try{
                    this.intent = new Intent(context, AlbumActivity.class);
                    this.intent.putExtra("ALBUM_NAME", album);
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

    public class AlbumViewHolder extends RecyclerView.ViewHolder{
        TextView albumTitleTextView;

        public AlbumViewHolder(View itemView){
            super(itemView);
            albumTitleTextView = itemView.findViewById(R.id.music_title_txt);
        }
    }
}
