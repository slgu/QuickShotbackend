import db.Topic;

import java.util.List;

/**
 * Created by slgu1 on 12/18/15.
 */
public class TestEs {
    public static void main(String [] args) {
        List <Topic> res = Topic.documentSearch("girl");
        for (Topic topic: res) {
            System.out.println(topic.toJson());
        }
    }
}
