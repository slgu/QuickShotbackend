package servlet.topic;

import config.Config;
import db.DbCon;
import db.User;
import org.bson.Document;
import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by slgu1 on 12/17/15.
 */
public class TopicCommentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = HttpUtil.checkLogin(req);
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        String comment = req.getParameter("comment");
        if (comment == null || comment.length() < 5 || comment.length() > 30) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        String topic_id = req.getParameter("tid");
        if (topic_id == null) {
            HttpUtil.writeResp(resp, 3);
            return;
        }
        User user = User.find(uid);
        //add comment into database
        HashMap <String, Object> mp = new HashMap<String, Object>();
        mp.put("uid", uid);
        mp.put("name", user.getName());
        mp.put("text", comment);
        mp.put("time", new Date());
        try {
            DbCon.mongodb.getCollection(Config.TopicConnection).findOneAndUpdate(
                    new Document("uid", topic_id),
                    new Document("$push", new Document("comment_list", mp))
            );
        }
        catch (Exception e) {
            HttpUtil.writeResp(resp, 4);
            return;
        }
        //ok
        HttpUtil.writeResp(resp, 0);
    }
}