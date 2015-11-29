package servlet.user;

import com.google.gson.Gson;
import db.DbCon;
import db.User;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;
import util.HttpUtil;
import util.Util;

import javax.jws.soap.SOAPBinding;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by slgu1 on 11/5/15.
 */
public class UserLoginServlet extends HttpServlet {
    /*
        return
        {"status":true}
        {"status":false}
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String passwd = req.getParameter("passwd");
        System.out.println(username);
        System.out.println(passwd);
        if (username == null || passwd == null) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
        User user = new User();
        user.setName(username);
        user.setPasswd(Util.encrypt(passwd));
        if (user.checkDb()) {
            // save session to memcache
            String session_key = Util.uuid();
            DbCon.memclient.add(session_key, 300, user.getUid());
            HashMap <String, Object> mp = new HashMap<String, Object>();
            mp.put("status", 0);
            mp.put("session_key", session_key);
            resp.getWriter().write(new Gson().toJson(mp));
        }
        else {
            HttpUtil.writeResp(resp, 1);
        }
    }
}