package com.runningmusic.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Log;

/**
 * Created by guofuming on 19/7/16.
 */
public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView eventTitle;
    public TextView eventDescription;
    public TextView personsTaken;
    public NetworkImageView eventImage;
    public TextView goingOrNot;
    public TextView eventType;
    public EventItemClickListener itemClickListener;

    EventViewHolder(View view, EventItemClickListener itemClickListener) {
        super(view);
        eventTitle = (TextView) view.findViewById(R.id.event_item_title);
        eventDescription = (TextView) view.findViewById(R.id.event_item_sub_title);
        eventImage = (NetworkImageView) view.findViewById(R.id.event_item_image);
        personsTaken = (TextView) view.findViewById(R.id.take_people_bumber);
        goingOrNot = (TextView) view.findViewById(R.id.event_going);
        eventType = (TextView) view.findViewById(R.id.event_type);
        this.itemClickListener = itemClickListener;
        view.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Log.e("EventAdapter", "onItemClick" + this.getPosition());
        if (itemClickListener!=null) {
            itemClickListener.onItemClick(v, getPosition());
        }
    }
}
