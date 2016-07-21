package com.runningmusic.music;

import com.runningmusic.db.JSONParceble;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by GuDong on 3/19/16 21:14.
 * Contact with gudong.name@gmail.com.
 */
public class PlayListEntity implements JSONParceble,Serializable,IPlayList{

    /**
     * code : ff
     * description : 有一种音乐叫Rock&Roll，有一些传奇叫情怀，岁月总会给我们留下一两个值得惦念的声音
     * id : 56cc17954f56a25edade61d5
     * picture : http://7xr0lm.com1.z0.glb.clouddn.com/8.png
     * stats : 1
     * title : 摇滚时光
     */

    public String code;
    public String description;
    public String id;
    public String picture;
    public int stats;
    public String title;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCoverUrl() {
        return picture;
    }

    @Override
    public int listType() {
        return IPlayList.TYPE_PL;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean initWithJSONObject(JSONObject obj) {

        try {
            code = obj.getString("code");
            description = obj.getString("description");
            id = obj.getString("id");
            picture = obj.getString("picture");
            stats = obj.getInt("stats");
            title = obj.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }
}
