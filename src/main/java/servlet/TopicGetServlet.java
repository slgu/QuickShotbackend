package servlet;

import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by slgu1 on 11/10/15.
 */
public class TopicGetServlet extends HttpServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
         /* user session */
        if (HttpUtil.checkLogin(req)== null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        String topic_uid = req.getParameter("uid");

    }
}
