package com.runningmusic.music;

import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by guofuming on 2/2/16.
 */
public class PGCMusicList extends Observable {

    private ArrayList<Music> pgcMusicList;

    public PGCMusicList() {
        pgcMusicList = new ArrayList<Music>();
    }
    public ArrayList<Music> getPGCMusic() {
        return pgcMusicList;
    }

    public void addPGCMusic(Music music) {
        if (!pgcMusicList.contains(music)) {
            pgcMusicList.clear();
            this.pgcMusicList.add(music);
            setChanged();
        }
        notifyObservers();
    }

    public void setPGCMusicList(ArrayList<Music> musicList) {
        if (pgcMusicList!=musicList) {
            pgcMusicList.clear();
            this.pgcMusicList = musicList;
            setChanged();
        }
        notifyObservers();
    }
}
