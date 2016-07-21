package com.runningmusic.event;

import com.runningmusic.music.PlayListEntity;

import java.util.ArrayList;

/**
 * Created by guofuming on 22/7/16.
 */
public class HotListGroupEvent {
    public ArrayList<PlayListEntity> listGroup;

    public HotListGroupEvent (ArrayList<PlayListEntity> result) {
        this.listGroup = result;
    }
}
