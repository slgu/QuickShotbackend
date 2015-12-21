package servlet.user;

import com.mongodb.client.FindIterable;
import config.Config;
import db.DbCon;
import org.bson.Document;
import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by slgu1 on 12/20/15.
 */
public class UserAddLikeServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = HttpUtil.checkLogin(req);
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        String tid = req.getParameter("tid");
        if (tid == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        //TODO transaction needed
        try {
            FindIterable <Document> res = DbCon.mongodb.getCollection(Config.UserConnection).find(
                    new Document("uid", uid)
                            .append("likes_list", new Document("$elemMatch", new Document(
                                    "$eq", tid)))
            );
            if (res.iterator().hasNext()) {
                //nothing to do if have liked
            }
            else {
                //add to like list
                DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                        new Document("uid", uid),
                        new Document("$addToSet", new Document("likes_list", tid))
                );
                //inc like of topic
                DbCon.mongodb.getCollection(Config.TopicConnection).findOneAndUpdate(
                        new Document("uid", tid),
                        new Document("$inc", new Document("like", 1))
                );
            }
        }
        catch (Exception e) {
            HttpUtil.writeResp(resp, 3);
            return;
        }
        HttpUtil.writeResp(resp, 0);
    }
}