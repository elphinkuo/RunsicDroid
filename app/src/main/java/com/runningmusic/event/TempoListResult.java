package com.runningmusic.event;

import com.runningmusic.music.Music;
import com.runningmusic.music.PlayListEntity;

import java.util.ArrayList;

/**
 * Created by guofuming on 25/7/16.
 */
public class TempoListResult {
    public ArrayList<Music> musicArrayList;

    public TempoListResult (ArrayList<Music> result) {
        this.musicArrayList = result;
    }
}
