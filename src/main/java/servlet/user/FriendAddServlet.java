package servlet.user;

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
import java.util.UUID;

/**
 * Created by slgu1 on 11/10/15.
 */
public class FriendAddServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //perform a two-phase transaction semantic update
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
        //add src dest to transaction
        try {
            DbCon.mongodb.getCollection(Config.TransactionConnection).insertOne(
                    new Document().append("uid", Util.uuid())
                            .append("src_uid", uid)
                            .append("dest_uid", other_uid)
                            .append("state", "initial")
            );
        }
        catch (Exception e) {
            HttpUtil.writeResp(resp, 3);
            return;
        }
        HttpUtil.writeResp(resp, 0);
    }
}