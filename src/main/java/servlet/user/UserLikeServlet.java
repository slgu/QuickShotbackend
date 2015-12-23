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
        LinkedList <Topic> list = new LinkedList<Topic>();
        for (String tid: user.getLikes_list()) {
            Topic topic = Topic.getByUid(tid);
            //TODO spj
            topic.setImg_uid("http://s3.amazonaws.com/slgucloud/object-d7f3a48d-ab70-495c-92ae-d03f229bc9a8.png");
            list.add(topic);
        }
        HashMap <String, Object> mp = new HashMap<String, Object>();
        mp.put("status", 0);
        mp.put("info", list);
        resp.getWriter().write(new Gson().toJson(mp));
    }
}