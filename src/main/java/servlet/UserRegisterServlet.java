package servlet;

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
public class UserRegisterServlet  extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String username = req.getParameter("username");
        String passwd = req.getParameter("passwd");
        String code = req.getParameter("verifycode");
        if (email == null || username == null
                || passwd == null || code == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        /* check memcache verify */
        String val = (String)DbCon.memclient.get(email);
        System.out.println(val);
        System.out.println(code);
        if (val == null || !val.equals(code)) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        /* add */
        User user = new User();
        user.setEmail(email);
        /* encrypt */
        user.setName(username);
        user.setPasswd(Util.encrypt(passwd));
        if (!user.insert()) {
            HttpUtil.writeResp(resp, 3);
        }
        else {
            HttpUtil.writeResp(resp, 0);
        }
    }
}