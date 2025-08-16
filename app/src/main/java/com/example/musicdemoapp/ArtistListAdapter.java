package com.example.musicdemoapp;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ArtistViewHolder>{
    private ArrayList<String> artistList;
    private Context context;
    private Intent intent;

    public ArtistListAdapter(ArrayList<String> artistList, Context context){
        this.artistList = artistList;
        this.context = context;
    }

    @NonNull
    @Override
    public ArtistListAdapter.ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ArtistListAdapter.ArtistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistListAdapter.ArtistViewHolder holder, int position) {
        String artist = artistList.get(position);
        holder.artistTv.setText(artist);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            Intent intent = getIntent();
            @Override
            public void onClick(View v) {
                try {
                    this.intent = new Intent(context, ArtistActivity.class);
                    this.intent.putExtra("ARTIST_NAME", artist);
                    this.intent.setFlags(0);

                    context.startActivity(this.intent);
                } catch (Exception e){
                    Log.e(TAG, "Failed to start Artist Activity", e);
                    Log.i(TAG, "onClick: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() { return artistList.size(); }

    public static class ArtistViewHolder extends RecyclerView.ViewHolder{
        TextView artistTv;
        public ArtistViewHolder(View itemView){
            super(itemView);
            artistTv = itemView.findViewById(R.id.music_title_txt);
        }
    }

    public Intent getIntent(){
        return this.intent;
    }
}
