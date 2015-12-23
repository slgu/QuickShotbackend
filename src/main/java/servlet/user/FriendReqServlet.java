package servlet.user;

import com.mongodb.client.FindIterable;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import config.Config;
import db.DbCon;
import org.bson.Document;
import util.HttpUtil;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by slgu1 on 12/6/15.
 */
public class FriendReqServlet extends HttpServlet {
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
        /* return if uid == other_uid */
        if (uid == other_uid) {
            HttpUtil.writeResp(resp, 5);
            return;
        }
        /*check this user not your friends*/
        FindIterable <Document> res = DbCon.mongodb.getCollection(Config.UserConnection).find(
                new Document("uid", uid)
                    .append("friends_list", new Document("$elemMatch", new Document(
                            "$eq", other_uid
                    )))
        );
        if (res.iterator().hasNext()) {
            HttpUtil.writeResp(resp, 3);
            return;
        }

        //add into request db
        String key = uid + "_" + other_uid;
        try {
            DbCon.mongodb.getCollection(Config.ReqConnection).insertOne(
                new Document("key", key).append("from", uid)
                    .append("to", other_uid)
            );
        }
        catch (Exception e) {
            HttpUtil.writeResp(resp, 4);
            return;
        }

        //add to notify_list upsert
        DbCon.mongodb.getCollection(Config.NotifyConnection)
        .findOneAndUpdate(
                new Document("uid", other_uid),
                new Document("$addToSet", new Document("notify_list", uid)),
                new FindOneAndUpdateOptions().upsert(true)
        );
        //TODO add to sns
        HttpUtil.writeResp(resp, 0);
        return;
    }
}