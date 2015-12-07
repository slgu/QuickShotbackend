package servlet.topic;

import com.google.gson.Gson;
import db.Topic;
import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by slgu1 on 11/10/15.
 */
public class TopicGetServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         /* user session */
        if (HttpUtil.checkLogin(req)== null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        String topic_uid = req.getParameter("uid");
        Topic topic = Topic.getByUid(topic_uid);
        if (topic ==  null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        HashMap <String, Object> mp = new HashMap<String, Object>();
        mp.put("status", "0");
        mp.put("info", topic);
        resp.getWriter().write(new Gson().toJson(mp));
    }
}