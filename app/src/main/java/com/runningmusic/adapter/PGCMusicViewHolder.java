package com.runningmusic.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Log;
import com.runningmusic.utils.Util;

/**
 * Created by guofuming on 18/2/16.
 */
public  class PGCMusicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView songTitle;
    public TextView songArtist;
    public NetworkImageView songCover;
    private PGCItemClickListener itemClickListener;

    PGCMusicViewHolder(View view, PGCItemClickListener itemClickListener) {
        super(view);
        songTitle = (TextView) view.findViewById(R.id.grid_title);
        songArtist = (TextView) view.findViewById(R.id.grid_artist);
        songCover = (NetworkImageView) view.findViewById(R.id.grid_item_image);
        int width = Util.getScreenWidth();
        songCover.getLayoutParams().height = (int)(width/2 - Util.dp2px(view.getResources(), 12));

        Log.e("DEBUG", "widht" + width);
        this.itemClickListener = itemClickListener;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.e("GridAdapter", "onItemClick" + this.getPosition());
        if (itemClickListener!=null) {
            itemClickListener.onItemClick(v, getPosition());
        }
    }
}
