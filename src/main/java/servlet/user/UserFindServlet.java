package servlet.user;

import com.google.gson.Gson;
import db.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by slgu1 on 11/6/15.
 */
public class UserFindServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String findString = req.getParameter("username");
        List <User> res = User.puzzyFind(findString);
        LinkedList <Map <String, String> > jsonres = new LinkedList<Map<String, String>>();
        for (User user: res) {
            Map <String, String> mp = new HashMap<String, String>();
            mp.put("uid", user.getUid());
            mp.put("name", user.getName());
            jsonres.add(mp);
        }
        resp.getWriter().write(new Gson().toJson(jsonres));
    }
}
