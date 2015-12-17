package servlet.user;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import config.Config;
import db.DbCon;
import db.User;
import org.bson.Document;
import util.HttpUtil;

import javax.jws.soap.SOAPBinding;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by slgu1 on 12/17/15.
 */
public class UserNotifyServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = HttpUtil.checkLogin(req);
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        FindIterable <Document> res = DbCon.mongodb.getCollection(Config.UserConnection).find(
                new Document("uid", uid)
        );
        if (!res.iterator().hasNext()) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        Document doc = res.iterator().next();
        String [] todo_list = new String[]{};
        todo_list = ((List <String>)doc.get("todo_list")).toArray(todo_list);
        List <User> list = new LinkedList<User>();
        for (String todo: todo_list) {
            User user = User.find(todo);
            if (user == null)
                continue;
            list.add(user);
        }
        HashMap <String, Object> mp = new HashMap<String, Object>();
        mp.put("status", 0);
        mp.put("info", new Gson().toJson(list));
        System.out.println(new Gson().toJson(list));
        resp.getWriter().write(new Gson().toJson(mp));
    }
}