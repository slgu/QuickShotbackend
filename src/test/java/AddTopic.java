import config.Config;
import db.DbCon;
import db.Topic;
import org.bson.Document;
import util.AwsUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by slgu1 on 12/18/15.
 */
public class AddTopic {
    public static String [] user_uids =
            new String [] {
                    "69c6e1c2-50d8-4742-b9a3-4e599dc14ae2",
                    "1841fcbc-74f0-4cdc-9707-babdce4cecf2",
                    "6be21a3b-a54b-4fde-b4cd-c76f03468c30",
                    "d371a70f-d631-4b4c-8a60-bbcf7d7974b8",
                    "dff3ae73-a45e-4c74-9d8e-7d4a74d822e8"
            };
    private static double lat_min = 40.785840;
    private static double lat_max = 40.803548;
    private static double lon_min = -73.968890;
    private static double lon_max = -73.946777;

    static Random random = new Random();
    public static boolean addTopic(String uid, String title, String desc, String video_uid, String img_uid) {
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setDesc(desc);
        double a = random.nextDouble();
        double b = random.nextDouble();
        topic.setLat(String.valueOf(lat_min + a * (lat_max - lat_min)));
        topic.setLon(String.valueOf(lon_min + b * (lon_max - lon_min)));
        topic.setVideo_uid(video_uid);
        topic.setImg_uid(img_uid);
        if (topic.insert()) {
            //store into user list
            try {
                DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                        new Document("uid", uid),
                        new Document("$push", new Document("topics_list", topic.getUid()))
                );
                //to sqs
                AwsUtil.sendTopicToSQS(topic);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private static String videodir = "/Users/slgu1/Desktop/video";
    public static void main(String [] args) {
        File dir = new File(videodir);
        int i = 0;
        for (File f : dir.listFiles()) {
            if (f.isFile()) {
                if (f.getName().startsWith("."))
                    continue;
                String name = f.getName();
                if (!name.substring(name.length() - 3, name.length()).equals("mp4"))
                    continue;

                String [] title_desc = name.substring(0, name.length() - 4).split(",,");
                /*add video and image*/
                String video_uid = "";
                String img_uid = "";
                try {
                    String img_path = f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - 4) + ".png";
                    System.out.println(img_path);
                    video_uid = AwsUtil.uploadS3(f, "mp4", new FileInputStream(f.getAbsoluteFile()));
                    img_uid = AwsUtil.uploadS3(new File(img_path),
                            "png", new FileInputStream(img_path));
                }
                catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
                System.out.println(addTopic(user_uids[i % 5], title_desc[0].trim(), title_desc[1].trim(), Config.S3_VIDEO_URL + video_uid, Config.S3_IMG_URL + img_uid));
                ++i;
            }
        }

    }
}