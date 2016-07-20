package com.runningmusic.music;

/**
 * Created by guofuming on 9/3/16.
 */
public interface MusicPlayCallback {
    boolean onPrevious();

    boolean onNext();

    boolean onMusicPause();

    boolean onMusicGoOn();

    boolean onMusicStop();
}
