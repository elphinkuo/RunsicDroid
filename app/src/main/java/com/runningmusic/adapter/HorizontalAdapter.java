package com.runningmusic.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.runningmusic.music.Music;
import com.runningmusic.network.service.ImageSingleton;
import com.runningmusic.runninspire.R;

import java.util.ArrayList;

/**
 * Created by guofuming on 23/1/16.
 */
public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.HorizontalMusicViewHolder> {

    private final LayoutInflater inflater;
    private final Resources res;
    private final int itemLayoutRes;
    private ImageLoader imageLoader;
    private ArrayList<Music> musicAdatperList;

    public HorizontalAdapter(Context context, int itemLayoutRes, ArrayList<Music> musicList) {
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        musicAdatperList = musicList;
        imageLoader = ImageSingleton.getInstance(context).getImageLoader();
        this.itemLayoutRes = itemLayoutRes;
    }

    @Override
    public HorizontalMusicViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(itemLayoutRes, parent, false);
        return new HorizontalMusicViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HorizontalAdapter.HorizontalMusicViewHolder holder, int position) {
        Music music = musicAdatperList.get(position);
        holder.songCover.setImageUrl(music.coverURL, imageLoader);

        imageLoader.get(music.coverURL, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean isImmediate) {
                if (isImmediate && imageContainer.getBitmap() == null) return;
                holder.songCover.setBackground(new BitmapDrawable(imageContainer.getBitmap()));
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                android.util.Log.e("HorizontalAdapter", "onErrorResponse");
            }
        });
    }



    @Override
    public int getItemCount() {
        return musicAdatperList != null ? musicAdatperList.size() : 0;
    }

    public static class HorizontalMusicViewHolder extends RecyclerView.ViewHolder {
        private TextView songTitle;
        private TextView songArtist;
        private NetworkImageView songCover;
        private String songAudioURL;

        HorizontalMusicViewHolder(View view) {
            super(view);
            songTitle = (TextView) view.findViewById(R.id.grid_title);
            songArtist = (TextView) view.findViewById(R.id.grid_artist);
            songCover = (NetworkImageView) view.findViewById(R.id.static_recy_big);
        }
    }

}
