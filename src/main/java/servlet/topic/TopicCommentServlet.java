package servlet.topic;

import util.HttpUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by slgu1 on 12/17/15.
 */
public class TopicCommentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uid = HttpUtil.checkLogin(req);
        if (uid == null) {
            HttpUtil.writeResp(resp, 1);
            return;
        }
        String comment = req.getParameter("comment");
        if (comment == null || comment.length() < 5 || comment.length() > 30) {
            HttpUtil.writeResp(resp, 2);
            return;
        }
    }
}