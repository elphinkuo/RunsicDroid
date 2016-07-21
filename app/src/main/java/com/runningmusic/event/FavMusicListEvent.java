package com.runningmusic.event;

import com.runningmusic.music.Music;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by guofuming on 21/7/16.
 */
public class FavMusicListEvent {

    public ArrayList<Music> musicFavList;

    public FavMusicListEvent (ArrayList<Music> musicListResult) {
        this.musicFavList = musicListResult;
    }
}
