package com.runningmusic.music;

import java.util.Observable;

/**
 * Created by guofuming on 29/1/16.
 */
public class CurrentMusic {

    private Music currentMusic;

    public CurrentMusic() {
        currentMusic = new Music();
    }
    public Music getCurrentMusic() {
        return currentMusic;
    }

    public void setCurrentMusic(Music music) {
        if (!this.currentMusic.equals(music)) {
            this.currentMusic = music;
        }
    }
}
