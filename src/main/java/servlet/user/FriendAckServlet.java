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
 * Created by slgu1 on 12/6/15.
 */
public class FriendAckServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = HttpUtil.checkLogin(req);
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        String other_uid = req.getParameter("uid");
        if (other_uid == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        String key = other_uid + "_" + uid;
        //check req exists
        FindIterable <Document> res = DbCon.mongodb.getCollection(Config.ReqConnection).find(
                new Document("key", key)
        );
        if (!res.iterator().hasNext()) {
            HttpUtil.writeResp(resp, 3);
            return;
        }

        //add to friends list set
        //TODO Transaction needed
        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                new Document("uid", uid),
                new Document("$addToSet", new Document("friends_list", other_uid))
        );
        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                new Document("uid", other_uid),
                new Document("$addToSet", new Document("friends_list", uid))
        );

        //clear set
        DbCon.mongodb.getCollection(Config.NotifyConnection).findOneAndUpdate(
                new Document("uid", uid),
                new Document("$pull", new Document("notify_list", other_uid))
        );

        //clear req
        System.out.println(key);
        DbCon.mongodb.getCollection(Config.ReqConnection).deleteOne(
                new Document("key", key)
        );
        HttpUtil.writeResp(resp, 0);
    }
}
