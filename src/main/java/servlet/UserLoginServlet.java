package servlet;

import com.google.gson.Gson;
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
            HttpUtil.writeResp(resp, 0);
        }
        else {
            HttpUtil.writeResp(resp, 1);
        }
    }
}
