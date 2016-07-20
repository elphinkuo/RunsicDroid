package com.runningmusic.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.runningmusic.music.Music;
import com.runningmusic.network.service.ImageSingleton;
import com.runningmusic.service.RunsicService;

import java.util.ArrayList;

/**
 * Created by guofuming on 22/1/16.
 */


public class GridAdapter extends RecyclerView.Adapter<PGCMusicViewHolder> {

    private final LayoutInflater inflater;
    private final Resources res;
    private final int itemLayoutRes;
    private ImageLoader imageLoader;
    private ArrayList<Music> musicPGCList;
    private static PGCItemClickListener itemClickListener;
    private String coverURL;

    public GridAdapter(Context context, int itemLayoutRes, ArrayList<Music> musicList) {
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        musicPGCList = musicList;
        imageLoader = ImageSingleton.getInstance(context).getImageLoader();
        this.itemLayoutRes = itemLayoutRes;
    }

    @Override
    public PGCMusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(itemLayoutRes, parent, false);


        return new PGCMusicViewHolder(view, this.itemClickListener);
    }

    @Override
    public void onBindViewHolder(PGCMusicViewHolder holder, int position) {
        Music music = RunsicService.getInstance().musicPGCList.get(position);
        holder.songTitle.setText(music.title);
        holder.songArtist.setText(music.artist);

        holder.songCover.setImageUrl(music.coverURL, imageLoader);

    }

    @Override
    public int getItemCount() {
        return musicPGCList != null ? musicPGCList.size() : 0;
    }

    public void setOnItemClickListener(PGCItemClickListener listener) {
        this.itemClickListener = listener;
    }



}
