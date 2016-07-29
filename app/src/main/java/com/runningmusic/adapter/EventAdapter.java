package com.runningmusic.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.runningmusic.music.Event;
import com.runningmusic.network.service.ImageSingleton;
import com.runningmusic.runninspire.R;

import java.util.ArrayList;

/**
 * Created by guofuming on 19/7/16.
 */
public class EventAdapter extends RecyclerView.Adapter<EventViewHolder>{

    private final LayoutInflater inflater;
    private final Resources res;
    private final int itemLayoutRes;
    private ImageLoader imageLoader;
    private ArrayList<Event> eventArrayList;
    private static EventItemClickListener itemClickListener;

    public EventAdapter(Context context, int itemLayoutRes, ArrayList<Event> eventList) {
        inflater = LayoutInflater.from(context);
        res = context.getResources();
        eventArrayList = eventList;
        imageLoader = ImageSingleton.getInstance(context).getImageLoader();
        this.itemLayoutRes = itemLayoutRes;
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        view = inflater.inflate(itemLayoutRes, parent, false);
        return new EventViewHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        Event event = eventArrayList.get(position);
        holder.eventTitle.setText(event.title);
        holder.eventDescription.setText(event.description);
        holder.eventImage.setImageUrl(event.pictrueURL, imageLoader);
        holder.personsTaken.setText(""+event.persons+"人参与");
        holder.eventType.setText(event.type);
        if (event.type.equals("展览")) {
            holder.eventType.setBackgroundColor(Color.YELLOW);
        } else {
            holder.eventType.setBackgroundColor(Color.RED);
        }

        if (event.active==true) {
            holder.goingOrNot.setText("正在进行");
            holder.goingOrNot.setBackgroundResource(R.mipmap.tab_green);
        } else {
            holder.goingOrNot.setText("已结束");
            holder.goingOrNot.setBackgroundResource(R.mipmap.tab_red);
        }
    }

    @Override
    public int getItemCount() {
        return eventArrayList != null ? eventArrayList.size() : 0;
    }

    public void setOnItemClickListener(EventItemClickListener listener) {
        itemClickListener = listener;
    }
}
