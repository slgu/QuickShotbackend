package servlet.user;

import com.google.gson.Gson;
import db.Topic;
import db.User;
import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by slgu1 on 12/18/15.
 */
public class UserLikeServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = HttpUtil.checkLogin(req);
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        User user = User.find(uid);
        //check user
        if (user == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        LinkedList <Map <String, String> > list = new LinkedList<Map <String, String>>();
        for (String tid: user.getLikes_list()) {
            Topic topic = Topic.getByUid(tid);
            Map <String, String> mp = new HashMap<String, String>();
            mp.put("uid", topic.getUid());
            mp.put("title", topic.getTitle());
            mp.put("desc", topic.getDesc());
            list.add(mp);
        }
        resp.getWriter().write(new Gson().toJson(list));
    }
}
