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
        if ((lat == null || lon == null) && findString == null) {
            // no parameter selected
            HttpUtil.writeResp(resp, 2);
            return;
        }
        double lat_val;
        double lon_val;

        //parse lat lon
        try {
            lat_val = Double.parseDouble(lat);
            lon_val = Double.parseDouble(lon);
        }
        catch (Exception e) {
            HttpUtil.writeResp(resp, 3);
            return;
        }
        List <Topic> topics = null;
        if (lat == null || lon == null) {
            //document search
            topics = Topic.documentSearch(findString);
        }
        else {
            //geo location search
            topics = Topic.geoSearch(lat_val, lon_val);
        }
        HashMap <String, Object> mp = new HashMap<String, Object>();
        mp.put("status", 0);
        mp.put("info", topics);
        resp.getWriter().write(new Gson().toJson(mp));
    }
}