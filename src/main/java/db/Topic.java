package db;

import com.google.gson.Gson;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import config.Config;
import org.bson.Document;
import org.elasticsearch.search.aggregations.bucket.significant.SignificantTermsAggregatorFactory;
import util.Util;

import java.util.HashMap;

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

    public String[] getComment_list() {
        return comment_list;
    }

    public void setComment_list(String[] comment_list) {
        this.comment_list = comment_list;
    }

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
    public static Topic getByUid(String uid) {
        FindIterable <Document> iter = DbCon.mongodb.getCollection(Config.TopicConnection)
                .find(new Document("uid", uid));
        Topic topic = null;
        if (iter.iterator().hasNext()) {
            Document doc = iter.iterator().next();
            topic = new Topic();
            topic.setTitle((String)doc.get("title"));
            topic.setUid((String) doc.get("uid"));
            topic.setDesc((String) doc.get("description"));
            topic.setLon((String) doc.get("lon"));
            topic.setLat((String) doc.get("lat"));
            topic.setLike((Integer) doc.get("like"));
            topic.setVideo_uid((String) doc.get("video_uid"));
            topic.setComment_list((String [])doc.get("comment_list"));
        }
        return topic;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }
    private boolean validate() {
        return Util.checkFloat(lat) && Util.checkFloat(lon);
    }
    public String toJson() {
        HashMap <String, Object> mp = new HashMap<String, Object>();
        mp.put("uid", getUid());
        mp.put("title", getTitle());
        mp.put("description", getDesc());
        mp.put("lat", getLat());
        mp.put("lon", getLon());
        mp.put("video_uid", getVideo_uid());
        mp.put("comment_list", getComment_list());
        mp.put("like", getLike());
        return new Gson().toJson(mp);
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