package servlet.user;

import com.google.gson.Gson;
import db.User;
import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by slgu1 on 11/17/15.
 */

public class UserGetServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        HashMap <String, Object> mp = new HashMap<String, Object>();
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        User user = User.find(uid);
        if (user == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        mp.put("status", 0);
        mp.put("info", user);
        resp.getWriter().write(new Gson().toJson(mp));
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = req.getParameter("uid");
        HashMap <String, Object> mp = new HashMap<String, Object>();
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        User user = User.find(uid);
        if (user == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        mp.put("status", 0);
        mp.put("info", user);
        resp.getWriter().write(new Gson().toJson(mp));
    }
}
