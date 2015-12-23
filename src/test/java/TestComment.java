import config.Config;
import db.DbCon;
import db.User;
import org.bson.Document;
import util.HttpUtil;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by slgu1 on 12/20/15.
 */
public class TestComment {
    public static String uid = "d32c6db1-e64b-49a6-8a3f-c292e004976e";
    public static String [] comments = new String []
            {
                "this is really an exciting game, isn't it",
                    "yes, can not agree more with you"
            };
    public static String tid = "96984778-1976-40f7-b2e4-83381bd10954";
    public static boolean addComment(String text) {
        User user = User.find(uid);
        //add comment into database
        HashMap<String, Object> mp = new HashMap<String, Object>();
        mp.put("uid", uid);
        mp.put("name", user.getName());
        mp.put("text", text);
        mp.put("time", new Date());
        try {
            DbCon.mongodb.getCollection(Config.TopicConnection).findOneAndUpdate(
                    new Document("uid", tid),
                    new Document("$push", new Document("comment_list", mp))
            );
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    public static void main(String [] args) {
        for (int i = 0; i < comments.length; ++i)
            addComment(comments[i]);
    }
}