package servlet.topic;
import com.google.gson.Gson;
import config.Config;
import db.DbCon;
import db.Topic;
import db.User;
import org.bson.Document;
import util.HttpUtil;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by slgu1 on 11/10/15.
 */
public class TopicFindServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (HttpUtil.checkLogin(req) == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        String findString = req.getParameter("desc");
        String lat = req.getParameter("lat");
        String lon = req.getParameter("lon");
        if (lat == null && findString == null) {
            // no parameter selected
            HttpUtil.writeResp(resp, 2);
            return;
        }
        if (lat == null) {
            //document search
            LinkedList <Map <String, String> > jsonres = new LinkedList<Map<String, String>>();
            List <Topic> topics = Topic.documentSearch(findString);
            for (Topic topic: topics) {
                Map <String, String> mp = new HashMap<String, String>();
                mp.put("uid", topic.getUid());
                mp.put("title", topic.getTitle());
                jsonres.add(mp);
            }

        }
        else {

        }
        List<User> res = User.puzzyFind(findString);
        LinkedList<Map<String, String> > jsonres = new LinkedList<Map<String, String>>();
        for (User user: res) {
            Map <String, String> mp = new HashMap<String, String>();
            mp.put("uid", user.getUid());
            mp.put("name", user.getName());
            jsonres.add(mp);
        }
        resp.getWriter().write(new Gson().toJson(jsonres));
    }
}