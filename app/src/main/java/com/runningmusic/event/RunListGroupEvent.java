package com.runningmusic.event;

import com.runningmusic.music.PlayListEntity;

import java.util.ArrayList;

/**
 * Created by guofuming on 22/7/16.
 */
public class RunListGroupEvent {

    public ArrayList<PlayListEntity> listGroup;

    public RunListGroupEvent (ArrayList<PlayListEntity> listGroupResult) {
        this.listGroup = listGroupResult;
    }

}
