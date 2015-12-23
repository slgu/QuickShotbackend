import config.Config;
import db.DbCon;
import db.Topic;
import org.bson.Document;
import util.AwsUtil;

import java.util.HashMap;

/**
 * Created by slgu1 on 12/18/15.
 */
public class TestTopic {
    public static String uid = "d32c6db1-e64b-49a6-8a3f-c292e004976e";

    public static boolean addTopic(String title, String desc) {
        Topic topic = new Topic();
        topic.setLat("40.799650");
        topic.setLon("-73.963741");
        topic.setTitle(title);
        topic.setDesc(desc);
        topic.setVideo_uid("https://s3.amazonaws.com/slgucloud/object-77b9defc-3ca7-4590-ac3e-57d19060e2b1.mp4");
        topic.setUser_uid(uid);
        //store topic
        HashMap<String, Object> mp = new HashMap<String, Object>();
        //Transaction needed
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

    public static String [] titles = new String []
            {"food", "food", "food", "food", "food"};

    public static String [] descs = new String []
            /*
            {"Happy day ! Enjoy sunshine and yummy noodles",
                "I really enjoy this food, very delicious apple"};
                */
            {"This banana is the best food I have eaten.",
            "This Apple is the best food I have eaten.",
            "This fish is the best food I have eaten.",
            "This sushi is the best food I have eaten.",
            "This Orange is the best food I have eaten."};
    public static void main(String [] args) {
        for (int i = 0; i < titles.length; ++i)
            System.out.println(addTopic(titles[i], descs[i]));
    }
}