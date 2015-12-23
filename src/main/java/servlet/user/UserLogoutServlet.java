package servlet.user;

import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by slgu1 on 12/21/15.
 */
public class UserLogoutServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //clear session
        try {
            req.getSession(true).removeAttribute("uid");
        }
        catch (Exception e) {
            e.printStackTrace();
            HttpUtil.writeResp(resp, 1);
        }
        HttpUtil.writeResp(resp, 0);
    }
}
