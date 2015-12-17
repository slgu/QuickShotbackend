package servlet.user;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
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
import java.util.*;

/**
 * Created by slgu1 on 11/6/15.
 */
public class UserFindServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = HttpUtil.checkLogin(req);
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        String findString = req.getParameter("username");
        if (findString == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        String [] friends_list = new String[]{};
        FindIterable <Document> res = DbCon.mongodb.getCollection(Config.UserConnection).find(
                new Document("uid", uid)
        );
        if (!res.iterator().hasNext()) {
            HttpUtil.writeResp(resp, 3);
            return;
        }
        Document doc = res.iterator().next();
        friends_list = ((List<String>)doc.get("friends_list")).toArray(friends_list);
        HashSet <String> set = new HashSet<String>();
        for (String string: friends_list) {
            set.add(string);
        }
        List <User> json_res = User.puzzyFind(findString);
        for (User user: json_res) {
            if (set.contains(user.getUid())) {
                //mark friend
                user.setFriendTag(1);
            }
        }
        resp.getWriter().write(new Gson().toJson(json_res));
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String findString = req.getParameter("username");
        if (findString == null)
            return;
        List <User> res = User.puzzyFind(findString);
        resp.getWriter().write(new Gson().toJson(res));
    }
}
