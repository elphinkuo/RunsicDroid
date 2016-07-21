package com.runningmusic.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.runningmusic.music.IPlayList;
import com.runningmusic.network.service.ImageSingleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guofuming on 22/1/16.
 */


public class MusicListAdapter extends RecyclerView.Adapter<PGCMusicViewHolder> {

    private final LayoutInflater inflater;
    private final Resources res;
    private final int itemLayoutRes;
    private ImageLoader imageLoader;
    private List<IPlayList> data;
    private static PGCItemClickListener itemClickListener;
    private String coverURL;

    public MusicListAdapter(Context context, int itemLayoutRes) {
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        imageLoader = ImageSingleton.getInstance(context).getImageLoader();
        this.itemLayoutRes = itemLayoutRes;
        data = new ArrayList<>();
    }


    public void update(List<IPlayList> musicList){
        this.data = musicList;
        notifyDataSetChanged();
    }

    @Override
    public PGCMusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(itemLayoutRes, parent, false);
        return new PGCMusicViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(PGCMusicViewHolder holder, int position) {
        IPlayList music = data.get(position);
        holder.songTitle.setText(music.getTitle());
        holder.songArtist.setText(music.getDescription());
        holder.setListType(music.listType());
        holder.songCover.setImageUrl(music.getCoverUrl(), imageLoader);
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    public void setOnItemClickListener(PGCItemClickListener listener) {
        itemClickListener = listener;
    }



}
