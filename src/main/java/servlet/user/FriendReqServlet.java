package servlet.user;

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
        DbCon.mongodb.getCollection(Config.UserConnection).findOneAndUpdate(
                new Document("uid", uid),
                new Document("$addToSet", new Document("todo_list", other_uid))
        );
    }
}