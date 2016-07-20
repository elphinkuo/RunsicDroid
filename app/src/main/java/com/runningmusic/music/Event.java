package com.runningmusic.music;

import com.runningmusic.db.JSONParceble;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by guofuming on 19/7/16.
 */
public class Event implements JSONParceble {

    public boolean active;
    public String description;
    public String linkURL;
    public int persons;
    public String pictrueURL;
    public String title;
    public String type;

    @Override
    public boolean initWithJSONObject(JSONObject obj) {

        try {
            active = obj.getBoolean("active");
            description = obj.getString("description");
            linkURL = obj.getString("link");
            persons = obj.getInt("persons");
            pictrueURL = obj.getString("picture");
            title = obj.getString("title");
            type = obj.getString("type");
        } catch (JSONException e ){
            e.printStackTrace();
        }
        return false;
    }


}
