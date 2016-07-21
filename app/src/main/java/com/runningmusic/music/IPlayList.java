package com.runningmusic.music;

/**
 * Created by GuDong on 3/19/16 21:10.
 * Contact with gudong.name@gmail.com.
 */
public interface IPlayList {
    //热门推荐
    int TYPE_HR = 0;
    //本地音乐
    int TYPE_LM = 1;
    //跑步歌单
    int TYPE_PL = 1;
    /**
     * 热门推荐或者本地音乐或者跑步歌单
     * @return
     */
    int listType();

    String getTitle();

    String getDescription();

    String getCoverUrl();
}
