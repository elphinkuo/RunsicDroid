package com.runningmusic.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.runningmusic.db.Record;
import com.runningmusic.network.service.ImageSingleton;

import java.util.ArrayList;

/**
 * Created by guofuming on 15/3/16.
 */
public class VerticalAdapter extends RecyclerView.Adapter<VerticalAdapter.HistoryViewHolder> {

    private LayoutInflater inflater;
    private Resources res;
    private int itemLayoutRes;
    private ImageLoader imageLoader;
    private ArrayList<Record> recordList;


    public VerticalAdapter(Context context, int itemLayoutResInput, ArrayList<Record> recordInputList) {
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        recordList = recordInputList;
        imageLoader = ImageSingleton.getInstance(context).getImageLoader();
        itemLayoutRes = itemLayoutResInput;
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(itemLayoutRes, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        Record record = recordList.get(position);
//        holder.recordCover.setImageUrl(record.coverlist);


    }

    @Override
    public int getItemCount() {
        return recordList != null ? recordList.size() : 0;
    }


    public class HistoryViewHolder extends RecyclerView.ViewHolder{
        private NetworkImageView recordCover;
        private TextView recordDate;
        private TextView reocrdTime;
        private TextView distance;
        private TextView duration;
        public HistoryViewHolder(View view) {
            super(view);

        }
    }
}
