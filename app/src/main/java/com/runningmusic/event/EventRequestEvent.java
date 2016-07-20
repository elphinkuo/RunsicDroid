package com.runningmusic.event;

import com.runningmusic.music.Event;

import java.util.ArrayList;

/**
 * Created by guofuming on 19/7/16.
 */
public class EventRequestEvent {
    public ArrayList<Event> eventList;

    public EventRequestEvent(ArrayList<Event> eventListResult) {
        this.eventList = eventListResult;
    }
}
