package com.runningmusic.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.runningmusic.music.Music;
import com.runningmusic.music.PlayListEntity;
import com.runningmusic.network.service.ImageSingleton;
import com.runningmusic.service.RunsicService;

import java.util.ArrayList;

/**
 * Created by guofuming on 22/7/16.
 */
public class GridListGroupAdapter extends RecyclerView.Adapter<PGCMusicViewHolder> {

    private final LayoutInflater inflater;
    private final Resources res;
    private final int itemLayoutRes;
    private ImageLoader imageLoader;
    private ArrayList<PlayListEntity> listGroup;
    private static PGCItemClickListener itemClickListener;
    private String coverURL;

    public GridListGroupAdapter( Context context, int itemLayoutRes, ArrayList<PlayListEntity> listGroupResult) {
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        listGroup = listGroupResult;
        imageLoader = ImageSingleton.getInstance(context).getImageLoader();
        this.itemLayoutRes = itemLayoutRes;
    }

    @Override
    public PGCMusicViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(itemLayoutRes, parent, false);


        return new PGCMusicViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(PGCMusicViewHolder holder, int position) {
        PlayListEntity playListEntity = listGroup.get(position);
        holder.songTitle.setText(playListEntity.getTitle());
        holder.songArtist.setText(""+playListEntity.stats);

        holder.songCover.setImageUrl(playListEntity.getCoverUrl(), imageLoader);

    }

    @Override
    public int getItemCount() {
        return listGroup != null ? listGroup.size() : 0;
    }

    public void setOnItemClickListener(PGCItemClickListener listener) {
        itemClickListener = listener;
    }



}

