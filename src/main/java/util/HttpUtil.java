package util;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import config.Config;
import db.DbCon;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
    //use mozilla to send
    private final String USER_AGENT = "Mozilla/5.0";

    //syncronize method
    public static String post(String urlString, HashMap <String, Object> params) throws UnirestException{
        HttpResponse <String> res = Unirest.post(urlString)
                .fields(params)
                .asString();
        return res.getBody();
    }
    public static void main(String [] args) {
        HashMap <String, Object> mp = new HashMap<String, Object>();
        mp.put("text", "I love you");
        try {
            String res = post(Config.VECTOR_URL, mp);
            double [] points = new Gson().fromJson(res, double[].class);
            for (int i = 0; i < points.length; ++i) {
                System.out.println(points[i]);
            }
            System.out.println(points.length);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
