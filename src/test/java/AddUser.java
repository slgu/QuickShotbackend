import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;

import config.Config;
import db.DbCon;
import db.User;
import org.bson.Document;
import util.AwsUtil;
import util.HttpUtil;
import util.Util;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.Part;

/**
 * Created by slgu1 on 11/29/15.
 */
public class AddUser {
    public static boolean addUser(HashMap<String, Object> mp) {
        User user = new User();
        user.setAddress((String) mp.get("address"));
        user.setAge((Integer) mp.get("age"));
        user.setEmail((String) mp.get("email"));
        user.setSex((Integer) mp.get("sex"));
        user.setName((String) mp.get("name"));
        user.setPasswd((String) mp.get("passwd"));
        user.setNickname((String) mp.get("nickname"));
        user.setImg_uid((String) mp.get("img_uid"));
        return user.insert();
    }

    static String [] names = new String[]{
            "shenlonggu",
            "songgao",
            "kanzhu",
            "liangjin",
            "messi",
    };

    static String [] nick_names = new String []{
            "longdd",
            "song",
            "zhukan",
            "liangshuai",
            "small flea"
    };

    static String [] emails = new String [] {
            "longdd@gmail.com",
            "song@gmail.com",
            "zhukan@gmail.com",
            "liangshuai@gmail.com",
            "messi@gmail.com"
    };

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
    public static String imgDir = "/Users/slgu1/Desktop/touxiang";

    //add user
    public static void main(String[] args) {
        File dir = new File(imgDir);
        int i = 0;
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                if (f.getName().startsWith("."))
                    continue;
                InputStream io = null;
                try {
                    io = new BufferedInputStream(new FileInputStream(f.getAbsoluteFile()));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                try {
                    String key = AwsUtil.uploadS3(f, "png", new FileInputStream(f.getAbsoluteFile()));
                    HashMap<String, Object> mp = new HashMap<>();
                    mp.put("name", names[i]);
                    mp.put("nickname", nick_names[i]);
                    mp.put("sex", 0);
                    mp.put("age", 18);
                    mp.put("passwd", Util.encrypt("123"));
                    mp.put("address", "Columbia University");
                    mp.put("email", emails[i]);
                    mp.put("img_uid", Config.S3_IMG_URL + key);
                    System.out.println(addUser(mp));
                    ++i;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
    }
}
