package com.runningmusic.music;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by guofuming on 30/1/16.
 */
public class CurrentMusicList extends Observable {
    private ArrayList<Music> musicCurrentList;

    public CurrentMusicList() {
        musicCurrentList = new ArrayList<Music>();
    }
    public ArrayList<Music> getCurrentMusicList() {
        return musicCurrentList;
    }

    public void addCurrentMusic(Music music) {
        if (!musicCurrentList.contains(music)) {
            musicCurrentList.clear();
            this.musicCurrentList.add(music);
            setChanged();
        }
        notifyObservers();
    }

    public void setCurrentMusicList(ArrayList<Music> musicList) {
        if (musicCurrentList!=musicList) {
            musicCurrentList.clear();
            this.musicCurrentList = musicList;
            setChanged();
        }
        notifyObservers();
    }

    public void clear() {
        musicCurrentList.clear();
    }

    public void addMusic(Music music) {
        if (!musicCurrentList.contains(music)) {
            musicCurrentList.add(music);
            setChanged();
        }
        notifyObservers();
    }

}
