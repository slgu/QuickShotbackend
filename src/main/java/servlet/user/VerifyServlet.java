package servlet.user;

import com.google.gson.Gson;
import db.DbCon;
import db.User;
import util.HttpUtil;
import util.Util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by slgu1 on 11/5/15.
 */


public class VerifyServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        if (email == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        /* send random number and store */
        String randomString = (String)DbCon.memclient.get(email);
        if (randomString == null) {
            randomString = Util.random(6);
            DbCon.memclient.add(email, 300, randomString);
        }
        /* send email */
        try {
            Util.sendEmail(email, randomString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        HttpUtil.writeResp(resp, 0);
    }
}