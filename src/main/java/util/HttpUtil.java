package util;

import com.google.gson.Gson;
import db.DbCon;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by slgu1 on 11/5/15.
 */
public class HttpUtil {
    public static void writeResp(HttpServletResponse resp, int code) throws IOException{
        HashMap<String, Integer> res = new HashMap<String, Integer>();
        res.put("status", code);
        resp.getWriter().write(new Gson().toJson(res));
        return;
    }
    public static String checkLogin(HttpServletRequest req) {
        /* use memcache to check */
        String session_key = req.getParameter("session_key");
        return (String)DbCon.memclient.get(session_key);
    }
}
