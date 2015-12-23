import java.util.HashMap;

import config.Config;
import db.DbCon;
import db.User;
import org.bson.Document;
import util.HttpUtil;
import util.Util;

/**
 * Created by slgu1 on 11/29/15.
 */
public class TestUser {
    public static void addUser(HashMap <String, Object> mp) {
        User user = new User();
        user.setAddress((String)mp.get("address"));
        user.setAge((Integer) mp.get("age"));
        user.setEmail((String) mp.get("email"));
        user.setSex((Integer) mp.get("sex"));
        user.setName((String) mp.get("name"));
        user.setPasswd((String)mp.get("passwd"));
        user.setNickname((String)mp.get("nickname"));
        System.out.println(user.insert());
    }

    public static void addFriends(String uid, String other_uid) {
        //add to friends list set
        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                new Document("uid", uid),
                new Document("$addToSet", new Document("friends_list", other_uid))
        );
        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                new Document("uid", other_uid),
                new Document("$addToSet", new Document("friends_list", uid))
        );
        //clear set
        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                new Document("uid", uid),
                new Document("$pull", new Document("todo_list", other_uid))
        );
        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                new Document("uid", other_uid),
                new Document("$pull", new Document("todo_list", uid))
        );
    }

    public static void main(String [] args) {
        //addFriends("033ac322-2028-45e6-8f70-96960f4645d4", "48705893-608a-4765-8cec-0a610311c3ad");
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "kobe3@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "kobe3");
            put("nickname", "blackman8");
        }});
        /*
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "nash1@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "nash1");
            put("nickname", "son of phoenix");
        }});
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "messi1@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "messi");
            put("nickname", "messi1");
        }});
        System.out.println("done");
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "haha@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "kanzhu");
        }});
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "kana12@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "liangjin");
        }});
        addUser(new HashMap<String, Object>(){{
            put("address", "columbia university");
            put("age", 22);
            put("email", "song98@gmail.com");
            put("sex", 0);
            put("passwd", Util.encrypt("123"));
            put("name", "songgao");
        }});
        */
    }
}
