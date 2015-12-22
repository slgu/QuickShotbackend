package servlet.topic;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MapReduceIterable;
import config.Config;
import db.DbCon;
import db.Topic;
import db.User;
import org.bson.Document;
import org.elasticsearch.common.Strings;
import util.HttpUtil;
import util.VecUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by slgu1 on 11/30/15.
 */
//recommend topics to users
public class TopicRecommendServlet extends HttpServlet{
    protected LinkedList <String> mapQuery(String uid, ArrayList <Double> vec, int topNum) {
        String map ="function () {"+
                "var another = " + vec.toString() +";"+
                "var res = 0;" +
                "var idx = 0;" +
                "for(idx = 0; idx < this.val.length; ++idx) {" +
                "res += this.val[idx] * another[idx];" +
                "}" +
                "res = Math.sqrt(res);" +
                "emit(-res, this.key);"+
                "}";
        String reduce = "function(key, values) {return values.toString();}";
        MapReduceIterable<Document> res = DbCon.mongodb.getCollection(Config.VecConnection).mapReduce(map,
                reduce);
        Iterator<Document> itr = res.iterator();
        //top 10 return
        LinkedList <String> ret = new LinkedList<String>();
        while (itr.hasNext()) {
            Document doc = itr.next();
            String [] tid_list = ((String)doc.get("value")).split(",");

            //spj
            if (tid_list == null) {
                tid_list = new String[]{(String)doc.get("value")};
            }

            for (String tid: tid_list) {
                //continue if in user likelist
                FindIterable <Document> checkRes = DbCon.mongodb.getCollection(Config.UserConnection).find(
                        new Document("uid", uid)
                                .append("likes_list", new Document("$elemMatch", new Document(
                                        "$eq", tid
                                )))
                );
                //continue if in user likelist
                if (checkRes.iterator().hasNext()) {
                    continue;
                }
                --topNum;
                if (topNum == 0)
                    break;
                ret.add(tid);
            }
            if (topNum == 0)
                break;
        }
        return ret;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = HttpUtil.checkLogin(req);
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        //TODO cache in db
        //time consuming process
        User user = User.find(uid);
        //get vec by likes_list
        ArrayList <Double> arr = new ArrayList<Double>();
        for (int i = 0; i < Config.VEC_DIMENSION; ++i) {
            arr.add(0.0);
        }
        String [] liked_topic  = user.getLikes_list();
        for (String tid: liked_topic) {
            //add together
            FindIterable <Document> res = DbCon.mongodb.getCollection(Config.VecConnection).find(
                    new Document("key", tid)
            );
            if (res == null)
                continue;
            if (!res.iterator().hasNext())
                continue;
            Document doc = res.iterator().next();
            List <Double> vec = (List <Double>)doc.get("val");
            //add
            VecUtil.add(arr, vec);
        }
        //norm
        VecUtil.norm(arr);
        LinkedList <Topic> topics = new LinkedList<Topic>();
        for (String tid: mapQuery(uid, arr, 5)) {
            Topic topic = Topic.getByUid(tid);
            if (topic == null)
                continue;
            topics.add(topic);
        }
        //status 0
        HashMap<String, Object> mp = new HashMap<String, Object>();
        mp.put("status", 0);
        mp.put("info", topics);
        resp.getWriter().write(new Gson().toJson(mp));
    }

    public static void main(String [] args) {
        String tmp = "9fc59fc7-51b2-4fb0-9999-524736609707";
        for (String item : tmp.split(",")) {
            System.out.println(item);
        }
    }
}