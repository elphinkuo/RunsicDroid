package com.runningmusic.event;

import com.runningmusic.music.CurrentMusic;
import com.runningmusic.music.Music;

/**
 * Created by guofuming on 26/7/16.
 */
public class CurrentMusicEvent {
    public Music currentMusic;
    public CurrentMusicEvent(Music currentMusicEvent) {
        this.currentMusic = currentMusicEvent;
    }
}
