package com.runningmusic.music;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by guofuming on 30/1/16.
 */
public class CurrentMusicList {
    private ArrayList<Music> musicCurrentList;

    public CurrentMusicList() {
        musicCurrentList = new ArrayList<Music>();
    }
    public ArrayList<Music> getCurrentMusicList() {
        return musicCurrentList;
    }

    public void setCurrentMusicList(ArrayList<Music> musicList) {
        if (musicCurrentList!=musicList) {
            musicCurrentList.clear();
            this.musicCurrentList = musicList;
        }
    }

    public void clear() {
        musicCurrentList.clear();
    }

    public void addMusic(Music music) {
        if (!musicCurrentList.contains(music)) {
            musicCurrentList.add(music);
        }
    }

}
