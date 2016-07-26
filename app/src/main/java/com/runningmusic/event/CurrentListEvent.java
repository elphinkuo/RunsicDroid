package com.runningmusic.event;

import com.runningmusic.music.Music;

import java.util.ArrayList;

/**
 * Created by guofuming on 26/7/16.
 */
public class CurrentListEvent {
    public ArrayList<Music> currentMusicList;
    public CurrentListEvent(ArrayList<Music> currentMusicListInput) {
        this.currentMusicList = currentMusicListInput;
    }
}
