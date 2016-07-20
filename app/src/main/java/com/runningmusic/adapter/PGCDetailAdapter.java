package com.runningmusic.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.runningmusic.music.Music;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Log;

import java.util.ArrayList;

/**
 * Created by guofuming on 12/4/16.
 */
public class PGCDetailAdapter extends RecyclerView.Adapter<PGCDetailAdapter.PGCDetailViewHolder> {

    private final LayoutInflater inflater;
    private final Resources res;
    private final int itemLayoutRes;
    private ImageLoader imageLoader;
    private ArrayList<Music> musicPGCDetailList;
    private static PGCDetailItemClickListener itemClickListener;

    public PGCDetailAdapter(Context context, int itemLayoutRes, ArrayList<Music> musicList, LayoutInflater inflater, Resources res) {

        this.inflater = inflater;
        this.res = res;
        this.itemLayoutRes = itemLayoutRes;
        this.musicPGCDetailList = musicList;

    }

    @Override
    public PGCDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(itemLayoutRes, parent, false);

        return new PGCDetailViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(PGCDetailViewHolder holder, int position) {
        Music music = musicPGCDetailList.get(position);
        holder.musicTitle.setText(music.title);
        holder.musicArtist.setText(music.artist);

    }

    @Override
    public int getItemCount() {
        return musicPGCDetailList != null ? musicPGCDetailList.size() : 0;
    }

    public void setOnItemClickListener(PGCDetailItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public class PGCDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView number;
        private TextView musicTitle;
        private TextView musicArtist;
        private ImageView detailIcon;
        private PGCDetailItemClickListener holderItemClickListener;


        public PGCDetailViewHolder(View view, PGCDetailItemClickListener itemClickListener) {
            super(view);

            this.number = (TextView) view.findViewById(R.id.pgc_detail_number);
            this.musicTitle = (TextView) view.findViewById(R.id.pgc_detail_music_title);
            this.musicArtist = (TextView) view.findViewById(R.id.pgc_detail_music_artist);
            this.detailIcon = (ImageView) view.findViewById(R.id.pgc_detail_more_icon);

            this.holderItemClickListener = itemClickListener;

            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            this.getAdapterPosition();
            Log.e("GridAdapter", "onItemClick" + this.getPosition());
            if (itemClickListener!=null) {
                itemClickListener.onItemClick(v, getPosition());
            }
        }
    }
}
