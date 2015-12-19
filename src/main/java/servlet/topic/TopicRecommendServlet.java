package servlet.topic;

import com.mongodb.client.MapReduceIterable;
import config.Config;
import db.DbCon;
import org.bson.Document;
import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by slgu1 on 11/30/15.
 */
//recommend topics to users
public class TopicRecommendServlet extends HttpServlet{
    protected LinkedList <String> mapQuery(ArrayList <Double> vec, int topNum) {
        String map ="function () {"+
                "var another = " + vec.toString() +";"+
                "var res = 0;" +
                "var idx = 0;" +
                "for(idx = 0; idx < this.val.length; ++idx) {" +
                "res += (this.val[idx] - another[idx]) * (this.val[idx] - another[idx]);" +
                "}" +
                "res = Math.sqrt(res);" +
                "emit(-res, this.key);"+
                "}";
        String reduce = "function(key, values) {return values.toString();}";
        System.out.println(map);
        System.out.println(reduce);
        MapReduceIterable<Document> res = DbCon.mongodb.getCollection(Config.VecConnection).mapReduce(map,
                reduce);
        Iterator<Document> itr = res.iterator();
        //top 10 return
        LinkedList <String> ret = new LinkedList<String>();
        while (itr.hasNext() && ((topNum--) != 0)) {
            //insert into priority queue
            List <String> result = (List <String>)itr.next().get("value");
            ret.addAll(result);
        }
        return ret;
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
    }

}