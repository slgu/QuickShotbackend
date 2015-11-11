package db;

import config.Config;
import org.bson.Document;
import util.Util;

/**
 * Created by slgu1 on 11/5/15.
 */
public class Topic {
    private String uid = "";
    private String title = "";
    private String desc = "";
    private String video_uid = "";
    private int like = 0;
    private String lat = "";
    private String lon = "";
    private String [] comment_list = new String[]{};
    private String user_uid = "";
    public String getUser_uid() {
        return user_uid;
    }

    public void setUser_uid(String user_uid) {
        this.user_uid = user_uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getVideo_uid() {
        return video_uid;
    }

    public void setVideo_uid(String video_uid) {
        this.video_uid = video_uid;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
    private boolean validate() {
        return Util.checkFloat(lat) && Util.checkFloat(lon);
    }
    public boolean insert() {
        //validate first
        if (!validate())
            return false;
        setLike(0);
        setUid(Util.uuid());
        Document doc = new Document();
        doc.append("uid", getUid()).append("title", title)
                .append("descrption", desc)
                .append("lat", lat)
                .append("lon", lon)
                .append("video_uid", video_uid)
                .append("like", like)
                .append("comment_list", comment_list);
        try {
            DbCon.mongodb.getCollection(Config.TopicConnection)
                    .insertOne(doc);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
}