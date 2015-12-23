package servlet.user;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.FindOneAndUpdateOptions;
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
import java.util.ArrayList;
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
        Document doc = DbCon.mongodb.getCollection(Config.NotifyConnection).findOneAndUpdate(
                new Document("uid", uid),
                new Document("$setOnInsert", new Document("notify_list", new ArrayList<Object>())),
                new FindOneAndUpdateOptions().upsert(true)
        );
        System.out.println(uid);
        String [] notify_list = new String[]{};
        notify_list = ((List <String>)doc.get("notify_list")).toArray(notify_list);
        System.out.println(notify_list.length);
        List <User> list = new LinkedList<User>();
        for (String notify: notify_list) {
            User user = User.find(notify);
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