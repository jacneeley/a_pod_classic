package com.example.musicdemoapp;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder>{
    private ArrayList<AudioModel> songsList;
    private Context context;

    private Intent intent;

    public MusicListAdapter(ArrayList<AudioModel> songsList, Context context) {
        this.songsList = songsList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MusicListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MusicListAdapter.ViewHolder holder, int position) {
        int pos = position;
        AudioModel songData = songsList.get(position);
        holder.titleTextView.setText(songData.getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            Intent intent;
            @Override
            public void onClick(View v){
                //navigate to song activity

                if(MyMediaPlayer.currentIndex == -1 || MyMediaPlayer.currentIndex != pos){
                    MyMediaPlayer.getInstance().reset();
                    MyMediaPlayer.currentIndex = pos;
                    this.intent = new Intent(context, MusicPlayerActivity.class);
                    this.intent.putExtra("LIST", songsList);
                    this.intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(this.intent);
                }
                else{
                    context.startActivity(this.intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView titleTextView;
        ImageView iconImageView;
        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.music_title_txt);
            iconImageView = itemView.findViewById(R.id.icon_view);
        }
    }
}
